package com.yoho.service.governance.model;

import lombok.Data;


/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午4:35 in 2017/12/29
 * @ModifyBy:
 */
@Data
public class GroupCall {
    String serviceName;
    Double ratePerSecond;
    Integer requestCount;

    public GroupCall() {
    }

    public GroupCall(String serviceName, Double ratePerSecond, Integer requestCount) {
        this.serviceName = serviceName;
        this.ratePerSecond = ratePerSecond;
        this.requestCount = requestCount;
    }
}
