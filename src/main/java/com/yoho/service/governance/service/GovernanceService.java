package com.yoho.service.governance.service;

import com.yoho.service.governance.model.*;

import java.util.List;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:42 in 2017/12/28
 * @ModifyBy:
 */
public interface GovernanceService {

	List<ResourceGroup> queryAll(ResourceGroup group);

	void delete(ResourceGroup group);

	void save(ResourceGroup group);

	List<SourceLimit> getServiceSourceLimit(String cloud,String serviceName );

	List<ServiceReport> getServiceReport(String cloud,String dateBegin,String dateEnd);

	List<ServiceIpRank> getServiceIpRank(String cloud, String event, String dateBegin, String dateEnd);

	List<ServiceSourceCall> getServiceSourceCallTimes(String cloud, String event, String dateBegin, String dateEnd);

    List<ServiceError> getServiceError(String cloud);

	Object getServiceCallTime(String cloud, String event);

	List<ServiceTps> getServiceTps(String cloud, String event);

    List<GroupCall> resourceGroupCallRank(ResourceGroup group);
}
