package com.yoho.service.governance.model;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午4:35 in 2017/12/29
 * @ModifyBy:
 */
@Data
public class Service {
	private String serviceName;
	private String serviceUrl;
	private Set<String> ipList = Sets.newHashSet();
}
