package com.yoho.service.governance.influxDb;

import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午5:26 in 2017/12/29
 * @ModifyBy:
 */
public interface InfluxMapper {

    QueryResult getByServiceName(String cloud, String serviceName);

    QueryResult getServiceReport(String cloud, long start, long end);

    QueryResult getServiceIpRank(String cloud, String event, long start, long end);

    QueryResult selectGroupByEventIp(String cloud, String event, long start, long end, String ip);

    QueryResult getServiceErrors(String cloud, long start, long end);

    QueryResult getServiceErrorDetail(String cloud);

    QueryResult getServiceCallTime(String cloud, String event);

    QueryResult getByServiceTps(String cloud, String event);

    void addRecode(String cloud, String db, Point point);

    QueryResult queryThreadPool(String cloud);

    QueryResult queryHystrixCommand(String cloud);

    QueryResult  queryGroupCallRank(String cloud,String groupName);

}
