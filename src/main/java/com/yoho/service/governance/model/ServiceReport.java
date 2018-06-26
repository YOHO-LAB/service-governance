package com.yoho.service.governance.model;

import lombok.Data;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 上午10:49 in 2018/1/5
 * @ModifyBy:
 */
@Data
public class ServiceReport {
    private String serviceName;
    private String url;
    private Integer countNum = new Integer(0);
    private Integer errors = new Integer(0);
    private Double meanCost = new Double(0);
    private Double costSum = new Double(0);
    private Double concurrency = new Double(0);
    private Double countNumPercent = new Double(0);
    private Double costSumPercent = new Double(0);


    private int persent90=0;
    private int persent95=0;
    private int persent99=0;

    private String group="";

    public ServiceReport() {
    }

    public ServiceReport(String serviceName, String url, Integer countNum, Double meanCost, Double costSum) {
        this.serviceName = serviceName;
        this.url = url;
        this.countNum = countNum;
        this.meanCost = meanCost;
        this.costSum = costSum;
    }
}
