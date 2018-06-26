package com.yoho.service.governance.model;

        import lombok.Data;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 上午10:49 in 2018/1/5
 * @ModifyBy:
 */
@Data
public class ServiceSourceCall {
    private String source;
    private Integer callTimes;

    public ServiceSourceCall() {
    }

    public ServiceSourceCall(String source, Integer callTimes) {
        this.source = source;
        this.callTimes = callTimes;
    }
}
