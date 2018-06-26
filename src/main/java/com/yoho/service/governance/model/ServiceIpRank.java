package com.yoho.service.governance.model;

import lombok.Data;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 上午10:49 in 2018/1/5
 * @ModifyBy:
 */
@Data
public class ServiceIpRank {
	private String ip;
	private Integer countNum = new Integer(0);
	private Double meanCost = new Double(0);
	private Double costSum = new Double(0);
	private Double concurrency = new Double(0);
	private Double countNumPercent = new Double(0);
	private Double costSumPercent = new Double(0);

	public ServiceIpRank() {
	}

	public ServiceIpRank(String ip, Integer countNum, Double meanCost, Double costSum) {
		this.ip = ip;
		this.countNum = countNum;
		this.meanCost = meanCost;
		this.costSum = costSum;
	}
}
