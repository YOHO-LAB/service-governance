package com.yoho.service.governance.config;

import com.google.common.collect.Maps;
import com.yoho.service.governance.config.Enum.Cloud;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午3:55 in 2017/12/28
 * @ModifyBy:
 */
@Configuration("zkConfig")
public class ZkClient {

	@Value("${qCloud1.zk.address}")
	private String qCloud1ZkAddress;

	@Value("${qCloud2.zk.address}")
	private String qCloud2ZkAddress;

	@Value("${qCloud3.zk.address}")
	private String qCloud3ZkAddress;

	@Bean(name = "qCloud1ZkClient")
	CuratorFramework qCloud1ZkClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(qCloud1ZkAddress, retryPolicy);
		curatorFramework.start();
		return curatorFramework;
	}

	@Bean(name = "qCloud2ZkClient")
	CuratorFramework qCloud2ZkClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(qCloud2ZkAddress, retryPolicy);
		curatorFramework.start();
		return curatorFramework;
	}

	@Bean(name = "qCloud3ZkClient")
	CuratorFramework qCloud3ZkClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(qCloud3ZkAddress, retryPolicy);
		curatorFramework.start();
		return curatorFramework;
	}


	@Bean(name = "chooseZkClient")
	Map<String, CuratorFramework> chooseZkClient(CuratorFramework qCloud1ZkClient, CuratorFramework qCloud2ZkClient,CuratorFramework qCloud3ZkClient) {
		Map<String, CuratorFramework> map = Maps.newHashMap();
		map.put(Cloud.QCLOUD1.getName(), qCloud1ZkClient);
		map.put(Cloud.QCLOUD2.getName(), qCloud2ZkClient);
		map.put(Cloud.QCLOUD3.getName(), qCloud3ZkClient);
		return map;
	}
}
