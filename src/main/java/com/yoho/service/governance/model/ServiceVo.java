package com.yoho.service.governance.model;

import com.yoho.core.rest.governance.model.ServiceGovernStrategy;
import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午3:28 in 2017/12/21
 * @ModifyBy:
 */
@Data
@ToString
public class ServiceVo extends Service {
	private ServiceGovernStrategy serviceGovernStrategy;
	private List<SourceLimit> sourceLimits;
	private String serviceType;
}
