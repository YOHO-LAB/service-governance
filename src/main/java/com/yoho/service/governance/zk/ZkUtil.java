package com.yoho.service.governance.zk;

import com.yoho.core.rest.common.InstanceDetail;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 上午10:08 in 2017/12/29
 * @ModifyBy:
 */
public class ZkUtil {

	public static final String SERVICE_NODE_PATH = "/yh/services";

	public static final String EXTRA_SERVICE_NODE_PATH = "/yh/extraServices";

	public static final String CONFIG_NODE_PATH = "/yh/config";

	public static final String SERVICE_GOVERNANCE_NODE_PATH = "/yh/serviceGovernance";

	public static final String SERVICE_GROUP_NODE_PATH = "/yh/serviceGroup";

	public static final String HYSTRIX_THREADPOOL_CORESIZE = "hystrix.threadpool.%s.coreSize";

	public static final String HYSTRIX_THREADPOOL_MAXQUEUESIZ = "hystrix.threadpool.%s.maxQueueSize";

	public static final String HYSTRIX_THREADPOOL_QUEUESIZEREJECTIONTHRESHOLD = "hystrix.threadpool.%s.queueSizeRejectionThreshold";

	public static final String HYSTRIX_SERVICE_TIMEOUT = "hystrix.command.%s.execution.isolation.thread.timeoutInMilliseconds";

	public static InstanceSerializer<InstanceDetail> serializer = new JsonInstanceSerializer(InstanceDetail.class);


	public static void add(CuratorFramework zkClient, String path, String data) throws Exception {

		zkClient.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes());
	}

	public static void delete(CuratorFramework zkClient, String path) throws Exception {
		if (zkClient.checkExists().forPath(path) != null) {
			zkClient.delete().forPath(path);
		}
	}

	public static void update(CuratorFramework zkClient, String path, String data) throws Exception {
		zkClient.setData().forPath(path, data.getBytes());
	}
}
