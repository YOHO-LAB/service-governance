package com.yoho.service.governance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoho.core.rest.governance.model.ServiceGovernStrategy;
import com.yoho.service.governance.config.Enum.Cloud;
import com.yoho.service.governance.influxDb.InfluxMapper;
import com.yoho.service.governance.mapper.ApiSourceMapper;
import com.yoho.service.governance.mapper.ResourceGroupMapper;
import com.yoho.service.governance.model.*;
import com.yoho.service.governance.service.GovernanceService;
import com.yoho.service.governance.util.DateFormatUtil;
import com.yoho.service.governance.util.QueryResultUtil;
import com.yoho.service.governance.zk.ZkUtil;
import com.yoho.service.governance.zk.ZookeeperListener;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:44 in 2017/12/28
 * @ModifyBy:
 */
@Service
public class GovernanceServiceImpl implements GovernanceService {


    @Autowired
    ResourceGroupMapper mapper;

    @Autowired
    ApiSourceMapper apiSourceMapper;
    @Autowired
    InfluxMapper influxMapper;

    @Resource(name = "chooseZkClient")
    Map<String, CuratorFramework> chooseZkClient;

    private static Map<String, Long> serviceQueryTime = Maps.newHashMap();

    private static final Logger logger = LoggerFactory.getLogger(GovernanceServiceImpl.class);

    @Override
    public List<ResourceGroup> queryAll(ResourceGroup group) {

        List<ResourceGroup> resourceGroups = mapper.queryAll(group.getCloud());
        if (CollectionUtils.isEmpty(resourceGroups)) {
            return resourceGroups;
        }

        QueryResult queryResult = influxMapper.queryThreadPool(group.getCloud());
        List<QueryResult.Series> series = QueryResultUtil.getSeries(queryResult);

        if (series != null) {
            series.forEach(s -> {
                Map<String, String> tags = s.getTags();
                List<String> columns = s.getColumns();
                List<List<Object>> values = s.getValues();


                String threadPool = tags.get("threadPool");
                resourceGroups.forEach(o -> {
                    if (o.getGroupName().equals(threadPool)) {
                        o.setHost(values.get(0).get(columns.indexOf("ratePerSecondPerHost")) + "");
                        o.setCluster(values.get(0).get(columns.indexOf("ratePerSecond")) + "");
                        o.setActive(values.get(0).get(columns.indexOf("active")) + "");
                        o.setQueued(values.get(0).get(columns.indexOf("queued")) + "");
                        o.setPoolSize(values.get(0).get(columns.indexOf("poolSize")) + "");
                        o.setMaxActive(values.get(0).get(columns.indexOf("maxActive")) + "");
                        o.setExecutions(values.get(0).get(columns.indexOf("executions")) + "");
                        o.setQueueSize(values.get(0).get(columns.indexOf("queueSize")) + "");
                    }
                });
            });
        }

        return resourceGroups;
    }


    @Override
    @Transactional
    public void delete(ResourceGroup group) {
        group = mapper.queryById(group);
        mapper.delete(group);
        try {
            ZkUtil.delete(chooseZkClient.get(group.getCloud()),
                    ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_THREADPOOL_CORESIZE, group.getGroupName()));
            ZkUtil.delete(chooseZkClient.get(group.getCloud()),
                    ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_THREADPOOL_MAXQUEUESIZ, group.getGroupName()));
            for (Map.Entry<String, ServiceGovernStrategy> entry : ZookeeperListener.serviceGovernanceMap.get(group.getCloud()).entrySet()) {
                ServiceGovernStrategy value = entry.getValue();
                if (value.getExecutorGroup().equals(group.getGroupName())) {
                    value.setExecutorGroup(ServiceGovernStrategy.EXECUTOR_GROUP_DEFAULT);
                    String data = JSON.toJSONString(value);
                    CuratorFramework client = chooseZkClient.get(group.getCloud());
                    String path = ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/" + entry.getKey();
                    if (client.checkExists().forPath(path) != null) {
                        ZkUtil.update(client, path, data);
                    }
                }
            }

            saveGroup2Zk(group.getCloud());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void save(ResourceGroup group) {

        ResourceGroup resourceGroup = mapper.queryByGroupName(group);
        String path1 = ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_THREADPOOL_CORESIZE, group.getGroupName());
        String path2 = ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_THREADPOOL_MAXQUEUESIZ, group.getGroupName());
        String path3 = ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_THREADPOOL_QUEUESIZEREJECTIONTHRESHOLD, group.getGroupName());
        if (null == group.getId()) { //新增
            if (resourceGroup == null) { //不存在
                mapper.insert(group);
                try {
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path1) == null) {
                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path1, String.valueOf(group.getCoreSize()));
                    } else {
                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path1, String.valueOf(group.getCoreSize()));
                    }
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path2) == null) {
                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path2, String.valueOf(group.getMaxQueueSize()));
                    } else {
                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path2, String.valueOf(group.getMaxQueueSize()));
                    }
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path3) == null) {
                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path3, String.valueOf(group.getMaxQueueSize()));
                    } else {
                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path3, String.valueOf(group.getMaxQueueSize()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("group is exist");
            }
        } else { //修改
            if (resourceGroup.getId() == group.getId()) {
                mapper.update(group);
                try {
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path1) != null) {

                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path1, String.valueOf(group.getCoreSize()));

                    } else {

                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path1, String.valueOf(group.getCoreSize()));

                    }
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path2) != null) {

                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path2, String.valueOf(group.getMaxQueueSize()));

                    } else {

                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path2, String.valueOf(group.getMaxQueueSize()));
                    }
                    if (chooseZkClient.get(group.getCloud()).checkExists().forPath(path3) != null) {

                        ZkUtil.update(chooseZkClient.get(group.getCloud()), path3, String.valueOf(group.getMaxQueueSize()));

                    } else {

                        ZkUtil.add(chooseZkClient.get(group.getCloud()), path3, String.valueOf(group.getMaxQueueSize()));

                    }


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else { //存在重复记录
                throw new RuntimeException("group is exist");
            }

        }

        saveGroup2Zk(group.getCloud());

    }


    private void saveGroup2Zk(String cloud){

        List<ResourceGroup> resourceGroups = mapper.queryAll(cloud);
        if(CollectionUtils.isEmpty(resourceGroups)){
            return;
        }
        Set<String> groups = resourceGroups.stream().map(ResourceGroup::getGroupName).collect(Collectors.toSet());
        try {
            if (chooseZkClient.get(cloud).checkExists().forPath(ZkUtil.SERVICE_GROUP_NODE_PATH) != null) {
                ZkUtil.update(chooseZkClient.get(cloud), ZkUtil.SERVICE_GROUP_NODE_PATH, JSON.toJSONString(groups));
            } else {
                ZkUtil.add(chooseZkClient.get(cloud), ZkUtil.SERVICE_GROUP_NODE_PATH, JSON.toJSONString(groups));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SourceLimit> getServiceSourceLimit(String cloud, String serviceName) {
        List<SourceLimit> result = Lists.newArrayList();
        if (serviceQueryTime.get(serviceName) == null || serviceQueryTime.get(serviceName) < (System.currentTimeMillis() / 1000 - 3600)) {
            addAPiSource(serviceName,cloud);
            serviceQueryTime.put(serviceName, System.currentTimeMillis() / 1000);
        }
        List<ApiSource> sources = apiSourceMapper.getByServiceName(serviceName);

        if (CollectionUtils.isEmpty(sources)) {
            return result;
        }

        ServiceGovernStrategy serviceGovernStrategy = ZookeeperListener.serviceGovernanceMap.get(cloud).get(serviceName);

        Map<String, Double> srcCallerLimit = null;
        if (serviceGovernStrategy != null && serviceGovernStrategy.getSerLimitRateStrategy() != null) {
            srcCallerLimit = serviceGovernStrategy.getSerLimitRateStrategy().getSrcCallerLimit();
        }
        if (srcCallerLimit == null) {
            srcCallerLimit = Maps.newHashMap();
        }
        for (ApiSource apiSource : sources) {
            result.add(new SourceLimit(apiSource.getSource(), srcCallerLimit.get(apiSource.getSource())));
        }
        return result;
    }

    private void addAPiSource(String serviceName,String cloud) {
        QueryResult all = influxMapper.getByServiceName(cloud, serviceName);

        List<QueryResult.Series> seriesList = QueryResultUtil.getSeries(all);

        if (null == seriesList) {
            return;
        }
        try {
            Set<String> set = Sets.newHashSet();
            for (QueryResult.Series series : seriesList) {
                set.add(series.getTags().get("source"));
            }
            if (set.size() > 0) {
                List<ApiSource> apiSources = apiSourceMapper.getByServiceName(serviceName);
                Set<String> existSet = Sets.newHashSet();
                if (!CollectionUtils.isEmpty(apiSources)) {
                    for (ApiSource apiSource : apiSources) {
                        existSet.add(apiSource.getTarget() + apiSource.getSource());
                    }
                }

                for (String s : set) {
                    if (!existSet.contains(serviceName + s)) {
                        apiSourceMapper.insert(new ApiSource(serviceName, s));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("addTargetSource error e is {}", e);
        }
    }

    @Override
    public List<ServiceReport> getServiceReport(String cloud, String dateBegin, String dateEnd) {


        QueryResult queryResult = influxMapper.queryHystrixCommand(cloud);
        List<QueryResult.Series> seriesList = QueryResultUtil.getSeries(queryResult);
        if (seriesList == null) {
            return null;
        }

        Map<String, String> event2Service = ZookeeperListener.getEvent2Service();
        Map<String, String> serviceUrlMap = new HashedMap();
        if (event2Service != null) {
            event2Service.forEach((k, v) -> {
                serviceUrlMap.put(v, k);
            });
        }

        List<ServiceReport> serviceReports = new ArrayList<>();

        seriesList.forEach(s -> {
            String serviceName = s.getTags().get("command");
            String url = serviceUrlMap.get(serviceName);
            List<String> columns = s.getColumns();
            List<List<Object>> values = s.getValues();

            double errorCount = NumberUtils.toDouble(values.get(0).get(columns.indexOf("errorCount")) + "");
            double meanCost = NumberUtils.toDouble(values.get(0).get(columns.indexOf("meanExecute")) + "");
            double persent90 = NumberUtils.toDouble(values.get(0).get(columns.indexOf("persent90")) + "");
            double persent95 = NumberUtils.toDouble(values.get(0).get(columns.indexOf("persent95")) + "");
            double persent99 = NumberUtils.toDouble(values.get(0).get(columns.indexOf("persent99")) + "");
            double ratePerSecond = NumberUtils.toDouble(values.get(0).get(columns.indexOf("ratePerSecond")) + "");
            double countNum = NumberUtils.toDouble(values.get(0).get(columns.indexOf("requestCount")) + "");
            double totalCost = NumberUtils.toDouble(values.get(0).get(columns.indexOf("totalCost")) + "");
            String group = values.get(0).get(columns.indexOf("groupName")) + "";

            ServiceReport model = new ServiceReport(serviceName, url, new Double(countNum).intValue(), meanCost, totalCost);
            model.setConcurrency(ratePerSecond);
            model.setErrors(new Double(errorCount).intValue());
            model.setPersent90(new Double(persent90).intValue());
            model.setPersent95(new Double(persent95).intValue());
            model.setPersent99(new Double(persent99).intValue());
            model.setGroup(group);
            serviceReports.add(model);
        });

        return serviceReports;
    }


    @Override
    public List<ServiceIpRank> getServiceIpRank(String cloud, String event, String dateBegin, String dateEnd) {
        long end =System.currentTimeMillis();
        long start = end - 60 * 60 * 24 * 1000;

        QueryResult queryResult = influxMapper.getServiceIpRank(cloud, event, start, end);
        List<List<Object>> valuesList = QueryResultUtil.getValues(queryResult);
        if (valuesList == null) {
            return null;
        }
        List<ServiceIpRank> serviceIpRanks = new ArrayList<>();

        DecimalFormat decimalFormat = new DecimalFormat("######0.0000");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        for (List<Object> value : valuesList) {
            String ip = value.get(1) == null ? null : (String) value.get(1);
            if (ip != null) {
                QueryResult queryResult2 = influxMapper.selectGroupByEventIp(cloud, event, start, end, ip);
                List<List<Object>> valuesList2 = QueryResultUtil.getValues(queryResult2);
                if (valuesList2 == null) {
                    continue;
                }
                for (List<Object> value2 : valuesList2) {
                    int countNum = value2.get(1) == null ? 0 : (int) (double) value2.get(1);
                    double meanCost = value2.get(2) == null ? 0 : (double) value2.get(2);
                    ServiceIpRank model = new ServiceIpRank(ip, countNum, Double.valueOf(decimalFormat.format(meanCost)), Double.valueOf(decimalFormat.format(countNum * meanCost)));
                    model.setConcurrency(model.getCountNum() * 1d / (end - start) * 1000);
                    serviceIpRanks.add(model);
                }
            }
        }
        return serviceIpRanks;
    }

    @Override
    public List<ServiceSourceCall> getServiceSourceCallTimes(String cloud, String event, String dateBegin, String dateEnd) {
        List<ServiceSourceCall> list = Lists.newArrayList();
        QueryResult all = influxMapper.getByServiceName(cloud, event);
        List<QueryResult.Series> seriesList = QueryResultUtil.getSeries(all);

        if (null == seriesList) {
            return list;
        }
        try {
            for (QueryResult.Series series : seriesList) {
                String source = series.getTags().get("source");
                String times = String.valueOf(series.getValues().get(0).get(1));
                list.add(new ServiceSourceCall(source, Double.valueOf(times).intValue()));
            }

        } catch (Exception e) {
            logger.error("getServiceSourceCallTimes error e is {}", e);
        }
        return list;
    }

    @Override
    public List<ServiceError> getServiceError(String cloud) {
        List<ServiceError> list = Lists.newArrayList();

        QueryResult all = influxMapper.getServiceErrorDetail(cloud);
        List<QueryResult.Series> seriesList = QueryResultUtil.getSeries(all);

        if (null == seriesList) {
            return list;
        }
        try {
            for (QueryResult.Series series : seriesList) {
                List<List<Object>> values = series.getValues();
                if (!CollectionUtils.isEmpty(values)) {
                    for (List<Object> row : values) {
                        String time = DateFormatUtil.displayFormat(String.valueOf(row.get(0)));
                        String event = String.valueOf(row.get(1));
                        String source = String.valueOf(row.get(2));
                        String stack = String.valueOf(row.get(3));
                        String destHost = String.valueOf(row.get(4));
                        String originContext = String.valueOf(row.get(5));
                        String originIp = String.valueOf(row.get(6));
                        list.add(new ServiceError(event, source, time, stack,destHost,originContext,originIp));
                    }

                }

            }

        } catch (Exception e) {
            logger.error("getServiceError error e is {}", e);
        }
        list.sort((o1, o2) -> 0 - o1.getTime().compareTo(o2.getTime()));
        return list;
    }

    @Override
    public Object getServiceCallTime(String cloud, String event) {
        JSONObject object = null;

        QueryResult result = influxMapper.getServiceCallTime(cloud, event);

        List<List<Object>> valuesList = QueryResultUtil.getValues(result);
        if (valuesList != null) {
            double meanCost = valuesList.get(0).get(1) == null ? 0 : (double) valuesList.get(0).get(1);
            double maxCost = valuesList.get(0).get(2) == null ? 0 : (double) valuesList.get(0).get(2);
            object = new JSONObject();
            object.put("maxCost", maxCost);
            object.put("meanCost", meanCost);
        }
        return object;
    }

    @Override
    public List<ServiceTps> getServiceTps(String cloud, String event) {
        List<ServiceTps> data = Lists.newArrayList();
        QueryResult result = influxMapper.getByServiceTps(cloud, event);
        List<List<Object>> valuesList = QueryResultUtil.getValues(result);
        if (valuesList != null) {
            for (List<Object> row : valuesList) {
                String time = DateFormatUtil.displayFormat(String.valueOf(row.get(0)));
                String tps = String.valueOf(row.get(1));
                data.add(new ServiceTps(time, tps));
            }
        }
        return data;
    }

    @Override
    public List<GroupCall> resourceGroupCallRank(ResourceGroup group) {
        List<GroupCall> data = Lists.newArrayList();
        QueryResult result = influxMapper.queryGroupCallRank(group.getCloud(), group.getGroupName());
        List<QueryResult.Series> seriesList = QueryResultUtil.getSeries(result);

        if (null != seriesList) {
            try {
                for (QueryResult.Series series : seriesList) {
                    String serviceName = series.getTags().get("command").toString();
                    String period = String.valueOf(series.getValues().get(0).get(1));
                    String count = String.valueOf(series.getValues().get(0).get(2));
                    double v;
                    try {
                        v = new BigDecimal(count).divide(new BigDecimal(period)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    } catch (Exception e) {
                        v = 0;
                    }
                    ServiceGovernStrategy serviceGovernStrategy = ZookeeperListener.serviceGovernanceMap.get(group.getCloud()).get(serviceName);
                    if ((serviceGovernStrategy == null || StringUtils.isEmpty(serviceGovernStrategy.getExecutorGroup())) && group.getGroupName().equals("default")) {
                        data.add(new GroupCall(serviceName, v, Double.valueOf(count).intValue()));
                    } else if (serviceGovernStrategy.getExecutorGroup().equals(group.getGroupName())) {
                        data.add(new GroupCall(serviceName, v, Double.valueOf(count).intValue()));
                    }

                }

            } catch (Exception e) {
                logger.error("getServiceSourceCallTimes error e is {}", e);
            }
        }
        data.sort(Comparator.comparingInt(GroupCall::getRequestCount).reversed());

        return data.subList(0, data.size() > 10 ? 10 : data.size());
    }

}