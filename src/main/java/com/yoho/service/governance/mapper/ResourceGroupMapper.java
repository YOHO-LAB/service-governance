package com.yoho.service.governance.mapper;

import com.yoho.service.governance.model.ResourceGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:31 in 2017/12/28
 * @ModifyBy:
 */
public interface ResourceGroupMapper {

	List<ResourceGroup> queryAll(@Param("cloud") String cloud);

	ResourceGroup queryByGroupName(ResourceGroup group);

	int insert(ResourceGroup group);

	int update(ResourceGroup group);

	int delete(ResourceGroup group);

	ResourceGroup queryById(ResourceGroup group);
}
