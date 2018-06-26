package com.yoho.service.governance.influxDb.impl;

import com.yoho.service.governance.config.InfluxDb.InfluxDBQuery;
import com.yoho.service.governance.influxDb.InfluxMapper;
import com.yoho.service.governance.service.impl.GovernanceServiceImpl;
import com.yoho.service.governance.util.DateFormatUtil;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午5:27 in 2017/12/29
 * @ModifyBy:
 */
@Service
public class InfluxMapperImpl extends InfluxDBQuery implements InfluxMapper {

    private static final Logger logger = LoggerFactory.getLogger(InfluxMapperImpl.class);

    @Override
    public QueryResult getByServiceName(String cloud, String serviceName) {
        String command = "select count(path) from service_call where event='" + serviceName + "' group by source;";
        return query(cloud, command, "yoho_event");
    }


    @Override
    public QueryResult getServiceErrors(String cloud, long start, long end) {
        String command = "select count(ip) from service_call  where  is_success='N' group by event";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult getServiceErrorDetail(String cloud) {
        String command = "select event,source,stack,dest_host,context,ip from service_call  where  is_success='N' ";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult getServiceReport(String cloud, long start, long end) {

        String command = "SELECT SUM(count_num) AS count_num, SUM(sum_cost)/SUM(count_num) AS mean_cost " +
                "FROM service_access_event WHERE time > '"
                + DateFormatUtil.influxDBTimeFormat(start) + "' AND time < '" + DateFormatUtil.influxDBTimeFormat(end) + "' GROUP BY event";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult getServiceCallTime(String cloud, String event) {
        String command = "SELECT SUM(sum_cost)/SUM(count_num),MAX(mean_cost) " +
                "FROM service_access_event WHERE  event='" + event + "' ";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult getServiceIpRank(String cloud, String event, long start, long end) {

        String command = "SELECT distinct(ip)  FROM service_access  WHERE context <> 'gateway' and time > '" + DateFormatUtil.influxDBTimeFormat(start) + "' AND time < '" + DateFormatUtil.influxDBTimeFormat(end) + "' and event = '" + event + "'";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult selectGroupByEventIp(String cloud, String event, long start, long end, String ip) {

        String command = "SELECT count(ip) AS count_num, MEAN(cost) as mean_cost FROM service_access "
                + " WHERE context <> 'gateway' and  time > '" + DateFormatUtil.influxDBTimeFormat(start) + "' AND time < '" + DateFormatUtil.influxDBTimeFormat(end) + "' and  event = '" + event + "' and ip ='" + ip + "'";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult getByServiceTps(String cloud, String serviceName) {
        String command = "select count(ip) from service_call where time>now() - 6h  and event='" + serviceName + "' group by time(1m)";
        return query(cloud, command, "yoho_event");
    }

    public void addRecode(String cloud, String db, Point point) {
        add(cloud, db, point);
    }

    @Override
    public QueryResult queryThreadPool(String cloud) {
        String command = " select * from hystrix_thread_pool group by threadPool order by time desc limit 1;";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult queryHystrixCommand(String cloud) {
        String command = " select * from hystrix_command group by command order by time desc limit 1;";
        return query(cloud, command, "yoho_event");
    }

    @Override
    public QueryResult queryGroupCallRank(String cloud, String groupName) {
        String command = "select period, requestCount  from hystrix_command  where time > '" + DateFormatUtil.influxDBTimeFormat(System.currentTimeMillis() - 120 * 1000) + "' and groupName='" + groupName + "' group by command order by time desc limit 1;";
        logger.info("command is {}",command);
        return query(cloud, command, "yoho_event");
    }
}
