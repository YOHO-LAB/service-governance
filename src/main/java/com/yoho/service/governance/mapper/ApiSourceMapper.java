package com.yoho.service.governance.mapper;

import com.yoho.service.governance.model.ApiSource;
import com.yoho.service.governance.model.ResourceGroup;

import java.util.List;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:31 in 2017/12/28
 * @ModifyBy:
 */
public interface ApiSourceMapper {

    int insert(ApiSource group);

    List<ApiSource> getByServiceName(String serviceName);
}
