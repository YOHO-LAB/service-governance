package com.yoho.service.governance.service;

import com.google.common.collect.Maps;
import com.yoho.service.governance.config.Enum.Cloud;
import com.yoho.service.governance.influxDb.InfluxMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 描述:
 * Created by jie.pang@yoho.cn on 2018/1/18.
 */
@Service
public class TurbineDataAggService {

    private static final Logger logger = LoggerFactory.getLogger(TurbineDataAggService.class);

    public static Map<String, String> turbines = Maps.newConcurrentMap();

    public static ExecutorService executorService = new ThreadPoolExecutor(3, 3,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(100));

    private static final String DATA_PREFIX = "data";

    private static final String OPEN_BRACE = "{";

    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";

    @Autowired
    private InfluxMapper influxMapper;

    private static final String HYSTRIX_THREAD_POOL = "HystrixThreadPool";

    private static final String HYSTRIX_COMMAND = "HystrixCommand";

    static {
        turbines.put(Cloud.QCLOUD1.getName(), "http://qq-monitor.yohops.com/proxy.stream?origin=http://127.0.0.1:8080/turbine/turbine.stream?cluster=yhpro");
        turbines.put(Cloud.QCLOUD2.getName(), "http://qq-az2-monitor.yohops.com/proxy.stream?origin=http://127.0.0.1:8080/turbine/turbine.stream?cluster=yhpro");
        turbines.put(Cloud.QCLOUD3.getName(), "http://qq-az3-monitor.yohops.com/proxy.stream?origin=http://127.0.0.1:8080/turbine/turbine.stream?cluster=yhpro");

    }

    @PostConstruct
    public void init() {
        turbines.forEach((k, v) -> {
            executorService.submit(new Task(k, v, influxMapper));
        });
    }

    private static class Task implements Runnable {

        private String cloud;

        private String url;

        private DefaultTubineHttpClient tubineHttpClient;

        private ObjectReader objectReader;

        private InfluxMapper influxMapper;

        public Task(String cloud, String url, InfluxMapper influxMapper) {
            this.cloud = cloud;
            this.url = url;
            this.tubineHttpClient = new DefaultTubineHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();
            objectReader = objectMapper.reader(Map.class);
            this.influxMapper = influxMapper;
        }


        @Override
        public void run() {
            InputStream inputStream=null;
            CloseableHttpResponse response = null;
            try {
                Map<String, Long> lastLogTimeMap = Maps.newConcurrentMap();
                Map<String, Long> serviceLastLogTimeMap = Maps.newConcurrentMap();

                HttpGet httpget = new HttpGet(url);
                response = tubineHttpClient.getHttpClient().execute(httpget);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    tubineHttpClient.releaseConnections();
                    return;
                }

                String line ;
                int errCount = 0;
                while ((line = reader.readLine()) != null) {
                    try {
                        //错误次数大于100次重新开始
                        if (errCount > 100) {
                            break;
                        }

                        line = line.trim();
                        if (line.length() == 0 || (!line.startsWith(DATA_PREFIX))) {
                            // empty line or invalid so skip processing to next line
                            continue;
                        }

                        // we expect JSON on data lines
                        int pos = line.indexOf(OPEN_BRACE);
                        if (pos < 0) {
                            continue;
                        }

                        String jsonString = line.substring(pos);

                        Map<String, Object> json = objectReader.readValue(jsonString);
                        String type = (String) json.remove(TYPE_KEY);
                        String name = (String) json.remove(NAME_KEY);

                        if (type == null || name == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Type and/or name missing, skipping line: " + line);
                            }
                            continue;
                        }

                        if (type.equalsIgnoreCase(HYSTRIX_COMMAND)) {
                            logHystrixCommand(serviceLastLogTimeMap, json, name);
                        } else if (type.equalsIgnoreCase(HYSTRIX_THREAD_POOL)) {
                            logHystrixThreadPool(lastLogTimeMap, json, name);
                        }
                        //睡500ms
                        Thread.sleep(500);
                    } catch (Exception e) {
                        errCount++;
                        logger.error("write to influx db error:{}", e);
                    }
                }
            } catch (Exception e) {
                tubineHttpClient.releaseConnections();
                logger.error("turbine data monitor error:{}", e);
            } catch (Throwable e) {
                tubineHttpClient.releaseConnections();
                logger.error("turbine data monitor error:{}", e);
            }finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(response);
                //上面的任务因为异常结束，启动一个新任务
                executorService.submit(new Task(this.cloud, this.url, this.influxMapper));
            }
        }


        private void logHystrixThreadPool(Map<String, Long> lastLogTimeMap, Map<String, Object> json, String name) {
            //每隔10s条记录一条
            boolean logFlag = false;
            Long lastLogTime = lastLogTimeMap.get(name);
            long now = System.currentTimeMillis();
            if (lastLogTime == null) {
                logFlag = true;
            } else {
                logFlag = (now - lastLogTime > 10000) ? true : false;
            }

            if (logFlag) {
                lastLogTimeMap.put(name, now);
                //获取相关数据录influxdb
                int hostCount = NumberUtils.toInt(json.get("reportingHosts") + "", 1);
                int second = NumberUtils.toInt(json.get("propertyValue_metricsRollingStatisticalWindowInMilliseconds") + "") / 1000 / hostCount;
                int total = NumberUtils.toInt(json.get("rollingCountThreadsExecuted") + "");

                String clusterRate = String.format("%.1f", (total + 0.0) / second);
                String hostRate = String.format("%.1f", (total + 0.0) / second / hostCount);

                int currentPoolSize = NumberUtils.toInt(json.get("currentPoolSize") + "");
                int rollingMaxActiveThreads = NumberUtils.toInt(json.get("rollingMaxActiveThreads") + "");
                int rollingCountThreadsExecuted = NumberUtils.toInt(json.get("rollingCountThreadsExecuted") + "");
                int propertyValue_queueSizeRejectionThreshold = NumberUtils.toInt(json.get("propertyValue_queueSizeRejectionThreshold") + "") / hostCount;
                int currentActiveCount = NumberUtils.toInt(json.get("currentActiveCount") + "");
                int currentQueueSize = NumberUtils.toInt(json.get("currentQueueSize") + "");

                Point point = Point.measurement("hystrix_thread_pool")
                        .tag("threadPool", name)
                        .addField("ratePerSecond", clusterRate)
                        .addField("ratePerSecondPerHost", hostRate)
                        .addField("poolSize", currentPoolSize)
                        .addField("maxActive", rollingMaxActiveThreads)
                        .addField("executions", rollingCountThreadsExecuted)
                        .addField("queueSize", propertyValue_queueSizeRejectionThreshold)
                        .addField("active", currentActiveCount)
                        .addField("queued", currentQueueSize)
                        .build();
                influxMapper.addRecode(cloud, "yoho_event", point);
            }
        }

        private void logHystrixCommand(Map<String, Long> lastLogTimeMap, Map<String, Object> json, String name) {
            //每隔10s条记录一条
            boolean logFlag = false;
            Long lastLogTime = lastLogTimeMap.get(name);
            long now = System.currentTimeMillis();
            if (lastLogTime == null) {
                logFlag = true;
            } else {
                logFlag = (now - lastLogTime > 10000) ? true : false;
            }

            if (logFlag) {
                lastLogTimeMap.put(name, now);
                //获取相关数据录influxdb
                int reportingHosts = NumberUtils.toInt(json.get("reportingHosts") + "", 1);
                int requestCount = NumberUtils.toInt(json.get("requestCount") + "", 0);
                int period = NumberUtils.toInt(json.get("propertyValue_metricsRollingStatisticalWindowInMilliseconds") + "", 1000) / 1000;
                int latencyExecute_mean = NumberUtils.toInt(json.get("latencyExecute_mean") + "", 0);
                int errorCount = NumberUtils.toInt(json.get("errorCount") + "", 0);

                Map<String, Object> latencyExecute = (Map<String, Object>) json.get("latencyExecute");
                int persent90 = NumberUtils.toInt(latencyExecute.get("90") + "") / reportingHosts;
                int persent95 = NumberUtils.toInt(latencyExecute.get("95") + "") / reportingHosts;
                int persent99 = NumberUtils.toInt(latencyExecute.get("99") + "") / reportingHosts;
                String ratePerSecond = String.format("%.2f", (requestCount + 0.0) / reportingHosts);
                String meanExecute = String.format("%.2f", (latencyExecute_mean + 0.0) / reportingHosts);
                int totalCost = new Double((latencyExecute_mean + 0.0) / reportingHosts * requestCount).intValue();
                String group = json.get("group") + "";

                Point point = Point.measurement("hystrix_command")
                        .tag("command", name)
                        .tag("groupName", group)
                        .addField("requestCount", requestCount)
                        .addField("errorCount", errorCount)
                        .addField("persent90", persent90)
                        .addField("persent95", persent95)
                        .addField("persent99", persent99)
                        .addField("meanExecute", meanExecute)
                        .addField("ratePerSecond", ratePerSecond)
                        .addField("period", period)
                        .addField("totalCost", totalCost)
                        .build();
                influxMapper.addRecode(cloud, "yoho_event", point);
            }
        }
    }

    private interface TubineHttpClient {
        HttpClient getHttpClient() throws Exception;

        void releaseConnections();
    }

    private static class DefaultTubineHttpClient implements TubineHttpClient {

        CloseableHttpClient httpClient;

        @Override
        public CloseableHttpClient getHttpClient() throws Exception {

            HttpClientBuilder b = HttpClientBuilder.create();

            // setup a Trust Strategy that allows all certificates.
            //
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] arg0, String arg1)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            b.setSSLContext(sslContext);

            // don't check Hostnames, either.
            // -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if
            // you don't want to weaken
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

            // here's the special part:
            // -- need to create an SSL Socket Factory, to use our weakened
            // "trust strategy";
            // -- and create a Registry, to register it.
            //
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create()
                    .register("http",
                            PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory).build();

            // now, we create connection-manager using our Registry.
            // -- allows multi-threaded use
            PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            connMgr.setMaxTotal(10);
            connMgr.setDefaultMaxPerRoute(10);
            b.setConnectionManager(connMgr);

            //request config
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000)
                    .build();
            b.setDefaultRequestConfig(requestConfig);

            // finally, build the HttpClient;
            // -- done!
            httpClient = b.build();
            return httpClient;
        }

        @Override
        public void releaseConnections() {
            try {
                if (httpClient != null && httpClient.getConnectionManager() != null) {
                    // When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate deallocation of all system resources
                    httpClient.getConnectionManager().shutdown();
                    httpClient = null;
                }
            } catch (Exception e) {
                logger.error("We failed closing connection to the HTTP server", e);
            }
        }
    }

}
