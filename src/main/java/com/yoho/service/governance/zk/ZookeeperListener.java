package com.yoho.service.governance.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yoho.core.rest.common.InstanceDetail;
import com.yoho.core.rest.governance.model.ServiceGovernStrategy;
import com.yoho.service.governance.config.Enum.Cloud;
import com.yoho.service.governance.model.Service;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ZookeeperListener {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperListener.class);

    public static Map<String, Map<String, Service>> serviceMap = Maps.newHashMap();

    public static Map<String, Map<String, Service>> extraServiceMap = Maps.newHashMap();

    public static Map<String, String> path2Ip = Maps.newHashMap();

    private static Map<String, String> event2Service = Maps.newHashMap();

    public static Map<String, Set<String>> SERVICE_GROUPS = Maps.newHashMap();

    public static Map<String, Map<String, ServiceGovernStrategy>> serviceGovernanceMap = Maps.newHashMap();


    @Resource(name = "chooseZkClient")
    Map<String, CuratorFramework> chooseZkClient;

    public static Map<String, String> getEvent2Service() {
        return event2Service;
    }

    public static void setEvent2Service(Map<String, String> event2Service) {
        ZookeeperListener.event2Service = event2Service;
    }

    @PostConstruct
    @ConditionalOnBean(name = "chooseZkClient")
    private void init() {
        for (Cloud cloud : Cloud.values()) {
            try {
                CuratorFramework client = chooseZkClient.get(cloud.getName());

                serviceMap.put(cloud.getName(), Maps.newHashMap());
                serviceGovernanceMap.put(cloud.getName(), Maps.newHashMap());
                extraServiceMap.put(cloud.getName(), Maps.newHashMap());

                if (client.checkExists().forPath(ZkUtil.CONFIG_NODE_PATH) == null) {
                    client.create().withMode(CreateMode.PERSISTENT).forPath(ZkUtil.CONFIG_NODE_PATH);
                }
                if (client.checkExists().forPath(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH) == null) {
                    client.create().withMode(CreateMode.PERSISTENT).forPath(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH);
                }
                if (client.checkExists().forPath(ZkUtil.SERVICE_NODE_PATH) == null) {
                    client.create().withMode(CreateMode.PERSISTENT).forPath(ZkUtil.SERVICE_NODE_PATH);
                }
                if (client.checkExists().forPath(ZkUtil.EXTRA_SERVICE_NODE_PATH) == null) {
                    client.create().withMode(CreateMode.PERSISTENT).forPath(ZkUtil.EXTRA_SERVICE_NODE_PATH);
                }

                this.setListenerService(client, cloud);
                this.setListenerServiceGovernance(client, cloud);
                this.setListenerExtraService(client, cloud);
                this.listenServiceGroup(client,cloud);
            } catch (Exception e) {
                //TODO
            }
        }
    }

    private void setListenerExtraService(CuratorFramework client, Cloud cloud) {
        TreeCache treeCache = new TreeCache(client, ZkUtil.EXTRA_SERVICE_NODE_PATH);
        treeCache.getListenable().addListener((c, e) -> {
            ChildData data = e.getData();
            if (data != null) {
                switch (e.getType()) {
                    case NODE_ADDED:
                        if (data.getPath().startsWith(ZkUtil.EXTRA_SERVICE_NODE_PATH + "/") && data.getData() != null) { //增加
                            handelExtraServiceEvent(cloud, TreeCacheEvent.Type.NODE_ADDED, data.getData());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开始监听
        try {
            treeCache.start();
        } catch (Exception e) {
            //TODO
        }
    }


    /**
     * 监听服务配置变化
     *
     * @throws Exception
     */
    private void setListenerServiceGovernance(CuratorFramework client, Cloud cloud) {

        TreeCache treeCache = new TreeCache(client, ZkUtil.SERVICE_GOVERNANCE_NODE_PATH);
        treeCache.getListenable().addListener((c, e) -> {
            ChildData data = e.getData();
            if (data != null) {
                switch (e.getType()) {
                    case NODE_ADDED:
                        if (data.getPath().startsWith(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/") && data.getData() != null) { //增加
                            handelServiceGovernanceEvent(cloud, TreeCacheEvent.Type.NODE_ADDED, data.getPath().replace(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/", ""), data.getData());
                        }
                        break;
                    case NODE_UPDATED:
                        if (data.getPath().startsWith(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/")) { //更新
                            handelServiceGovernanceEvent(cloud, TreeCacheEvent.Type.NODE_UPDATED, data.getPath().replace(ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/", ""), data.getData());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开始监听
        try {
            treeCache.start();
        } catch (Exception e) {
            //TODO
        }
    }

    /**
     * 监听服务变化
     *
     * @throws Exception
     */
    private void setListenerService(CuratorFramework client, Cloud cloud) {
        TreeCache treeCache = new TreeCache(client, ZkUtil.SERVICE_NODE_PATH);
        treeCache.getListenable().addListener((c, e) -> {
            ChildData data = e.getData();
            if (data != null) {
                switch (e.getType()) {
                    case NODE_ADDED:
                        if (data.getPath().startsWith(ZkUtil.SERVICE_NODE_PATH + "/") && data.getData() != null && data.getData().length > 0) { //增加
                            handelServiceEvent(cloud, TreeCacheEvent.Type.NODE_ADDED, data.getPath().replace(ZkUtil.SERVICE_NODE_PATH + "/", ""), data.getData());
                        }
                        break;
                    case NODE_REMOVED:
                        if (data.getPath().startsWith(ZkUtil.SERVICE_NODE_PATH + "/")) { //移除
                            handelServiceEvent(cloud, TreeCacheEvent.Type.NODE_REMOVED, data.getPath().replace(ZkUtil.SERVICE_NODE_PATH + "/", ""), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开始监听
        try {
            treeCache.start();
        } catch (Exception e) {
            //TODO
        }
    }


    private void listenServiceGroup(CuratorFramework client, Cloud cloud) {

        Set<String> serviceGroups = new HashSet<>();
        TreeCache treeCache = new TreeCache(client, ZkUtil.SERVICE_GROUP_NODE_PATH);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) {
                try {
                    switch (event.getType()) {
                        case NODE_REMOVED:
                            SERVICE_GROUPS.put(cloud.getName(), new HashSet<>());
                        default:
                            String s = new String(event.getData().getData(), "UTF-8");
                            SERVICE_GROUPS.put(cloud.getName(), new HashSet<>(JSON.parseArray(s, String.class)));
                    }
                } catch (Exception e) {
                    logger.warn("listener zk service group change error:{}", e.getMessage());
                }
            }
        });
        try {
            treeCache.start();
        } catch (Exception e) {
            logger.warn("listener zk service group change error:{}", e.getMessage());
        }
    }


    private void handelServiceGovernanceEvent(Cloud cloud, TreeCacheEvent.Type eventType, String path, byte[] data) {
        switch (eventType) {
            case NODE_ADDED:
                if (data != null && data.length > 0)
                    serviceGovernanceMap.get(cloud.getName()).put(path, JSON.parseObject(new String(data), ServiceGovernStrategy.class));
                break;
            case NODE_UPDATED:
                if (data != null && data.length > 0)
                    serviceGovernanceMap.get(cloud.getName()).put(path, JSON.parseObject(new String(data), ServiceGovernStrategy.class));
                break;
            default:
                break;
        }
    }

    private void handelExtraServiceEvent(Cloud cloud, TreeCacheEvent.Type eventType, byte[] data) {
        switch (eventType) {
            case NODE_ADDED:
                ServiceInstance<InstanceDetail> serviceInstance;
                try {
                    serviceInstance = ZkUtil.serializer.deserialize(data);
                    if (serviceInstance != null) {
                        String serviceName = serviceInstance.getName();
                        if (StringUtils.isEmpty(serviceName)) break;
                        Service info = new Service();
                        info.setServiceName(serviceName);
                        info.setServiceUrl(serviceInstance.getPayload().getRequestUrl());
                        extraServiceMap.get(cloud.getName()).put(info.getServiceName(), info);
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    private void handelServiceEvent(Cloud cloud, TreeCacheEvent.Type eventType, String path, byte[] data) {
        switch (eventType) {
            case NODE_ADDED:
                ServiceInstance<InstanceDetail> serviceInstance = null;
                try {
                    serviceInstance = ZkUtil.serializer.deserialize(data);
                } catch (Exception e) {
                }
                if (serviceInstance != null) {
                    String serviceName = serviceInstance.getName();
                    String ip = serviceInstance.getAddress();
                    if (StringUtils.isEmpty(serviceName)) break;
                    if (serviceMap.get(cloud.getName()).containsKey(serviceName)) {
                        path2Ip.put(serviceInstance.getId(), ip);
                        serviceMap.get(cloud.getName()).get(serviceName).getIpList().add(ip);
                    } else {
                        Service info = new Service();
                        info.setServiceName(serviceName);
                        info.getIpList().add(ip);
                        InstanceDetail instanceDetail = serviceInstance.getPayload();
                        if (instanceDetail != null) {
                            info.setServiceUrl(
                                    new StringBuffer("/").
                                            append(instanceDetail.getContext()).
                                            append(instanceDetail.getControllerRequestMapping() == null ? "" : instanceDetail.getControllerRequestMapping()).
                                            append(instanceDetail.getMethodRequestMapping() == null ? "" : instanceDetail.getMethodRequestMapping()).toString()
                            );
                            getEvent2Service().put(info.getServiceUrl(), info.getServiceName());
                            serviceMap.get(cloud.getName()).put(info.getServiceName(), info);
                        }
                    }
                }
                break;
            case NODE_REMOVED:
                if (path.contains("/")) { //移除服务子节点
                    String serviceName = path.split("/")[0];
                    String serviceNode = path.split("/")[1];

                    serviceMap.get(cloud.getName()).get(serviceName).getIpList().remove(path2Ip.get(serviceNode));

                    path2Ip.remove(path.split("/")[1]);
                } else { //移除服务

                    serviceMap.get(cloud.getName()).remove(path);
                }
                break;
            default:
                break;
        }
    }


}