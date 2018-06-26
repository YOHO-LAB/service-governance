package com.yoho.service.governance.model;

import lombok.Data;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:21 in 2018/1/3
 * @ModifyBy:
 */
@Data
public class SourceLimit {
	private String source;
	private Double limit;

	public SourceLimit() {

	}

	public SourceLimit(String source, Double limit) {
		this.source = source;
		this.limit = limit;
	}
}
