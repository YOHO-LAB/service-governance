package com.yoho.service.governance.model;

import lombok.Data;


/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午4:35 in 2017/12/29
 * @ModifyBy:
 */
@Data
public class ServiceTps {
    private String time;
    private String tps;

    public ServiceTps() {
    }

    public ServiceTps(String time, String tps) {
        this.time = time;
        this.tps = tps;
    }
}
