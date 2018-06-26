package com.yoho.service.governance.model;

import lombok.Data;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:21 in 2018/1/3
 * @ModifyBy:
 */
@Data
public class ServiceError {
    private String serviceName;
    private String source;
    private String time;
    private String stack;
    private String destHost;
    private String originContext;
    private String originIp;

    public ServiceError() {

    }


    public ServiceError(String serviceName, String source, String time, String stack, String destHost, String originContext, String originIp) {
        this.serviceName = serviceName;
        this.source = source;
        this.time = time;
        this.stack = stack;
        this.destHost = destHost;
        this.originContext = originContext;
        this.originIp = originIp;
    }
}
