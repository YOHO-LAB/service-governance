package com.yoho.service.governance.contorller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoho.core.rest.common.InstanceDetail;
import com.yoho.core.rest.governance.model.DegradeStrategy;
import com.yoho.core.rest.governance.model.SerLimitRateStrategy;
import com.yoho.core.rest.governance.model.ServiceGovernStrategy;
import com.yoho.service.governance.ApiResponse;
import com.yoho.service.governance.mapper.AccountMapper;
import com.yoho.service.governance.model.*;
import com.yoho.service.governance.service.GovernanceService;
import com.yoho.service.governance.util.MD5Utils;
import com.yoho.service.governance.zk.ZkUtil;
import com.yoho.service.governance.zk.ZookeeperListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:52 in 2017/12/28
 * @ModifyBy:
 */
@Controller
@RequestMapping("/api")
public class ApiController {
    public static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    GovernanceService governanceService;

    @Resource(name = "chooseZkClient")
    Map<String, CuratorFramework> chooseZkClient;

    @Autowired
    private AccountMapper accountMapper;


    @RequestMapping("/doLogin")
    @ResponseBody
    public ApiResponse doLogin(String username, String password, HttpSession session, HttpServletResponse response) {
        Account account = accountMapper.queryAccountByUsernameAndPassword(username, password);
        if (account != null) {
            session.setAttribute("account", account);
            //添加cookie
            Cookie usernameCookie = new Cookie("u_", username);
            usernameCookie.setMaxAge(7 * 24 * 3600);
            usernameCookie.setPath("/");
            response.addCookie(usernameCookie);
            Cookie passwordCookie = new Cookie("p_", MD5Utils.md5(password));
            passwordCookie.setMaxAge(7 * 24 * 3600);
            passwordCookie.setPath("/");
            response.addCookie(passwordCookie);
            return new ApiResponse.ApiResponseBuilder().data("success").build();
        } else {
            return new ApiResponse.ApiResponseBuilder().data("fail").build();
        }
    }


    @RequestMapping("/manager_data")
    @ResponseBody
    public ApiResponse managerData(String cloud) {
        List list = Lists.newArrayList();
        if (checkParam(cloud)) {


            for (Map.Entry<String, Service> entry : ZookeeperListener.serviceMap.get(cloud).entrySet()) {
                ServiceVo vo = new ServiceVo();
                vo.setServiceType("zk");
                vo.setServiceName(entry.getValue().getServiceName());
                vo.setServiceUrl(entry.getValue().getServiceUrl());
                vo.setIpList(entry.getValue().getIpList());

                ServiceGovernStrategy serviceGovernStrategy = ZookeeperListener.serviceGovernanceMap.get(cloud).get(vo.getServiceName());
                if (serviceGovernStrategy == null) {
                    serviceGovernStrategy = new ServiceGovernStrategy();
                    String weight = "";
                    if (entry.getValue().getIpList().size() > 0) {
                        for (String ip : entry.getValue().getIpList()) {
                            weight += "," + ip + ":" + 0;
                        }
                        weight.substring(1);
                    }
                    serviceGovernStrategy.setWeight(weight);
                } else {
                    String weight = serviceGovernStrategy.getWeight();
                    if (StringUtils.isEmpty(weight)) {
                        weight = "";
                        if (entry.getValue().getIpList().size() > 0) {
                            for (String ip : entry.getValue().getIpList()) {
                                weight += "," + ip + ":" + 0;
                            }
                            weight.substring(1);
                        }
                        serviceGovernStrategy.setWeight(weight);
                    }
                }

                setServiceGroup(serviceGovernStrategy,entry.getValue().getServiceName(),cloud);

                vo.setServiceGovernStrategy(serviceGovernStrategy);

                list.add(vo);
            }

            for (Map.Entry<String, Service> entry : ZookeeperListener.extraServiceMap.get(cloud).entrySet()) {
                ServiceVo vo = new ServiceVo();
                vo.setServiceName(entry.getValue().getServiceName());
                vo.setServiceUrl(entry.getValue().getServiceUrl());
                vo.setServiceType("notZk");
                ServiceGovernStrategy serviceGovernStrategy = ZookeeperListener.serviceGovernanceMap.get(cloud).get(vo.getServiceName());
                if (serviceGovernStrategy == null) {
                    serviceGovernStrategy = new ServiceGovernStrategy();
                }
                vo.setServiceGovernStrategy(serviceGovernStrategy);
                setServiceGroup(serviceGovernStrategy,entry.getValue().getServiceName(),cloud);
                list.add(vo);
            }
        }

        return new ApiResponse.ApiResponseBuilder().data(list).build();
    }


    private void setServiceGroup( ServiceGovernStrategy serviceGovernStrategy,String serviceName,String cloud){
        //获取所有的线程组
        Set<String> serviceGroups = ZookeeperListener.SERVICE_GROUPS.get(cloud);
        serviceGroups=serviceGroups==null? Sets.newHashSet():serviceGroups;
        if (serviceGovernStrategy == null || serviceGovernStrategy.getExecutorGroup() == null) {
            String s = serviceName.split("\\.", 2)[0];
            if (serviceGroups.contains(s)) {
                serviceGovernStrategy = serviceGovernStrategy == null ? new ServiceGovernStrategy() : serviceGovernStrategy;
                serviceGovernStrategy.setExecutorGroup(s);
            }
        }
    }

    @RequestMapping("/report_data")
    @ResponseBody
    public ApiResponse reportData(String cloud, String date_begin, String date_end) {
        List<ServiceReport> list = null;

        if (checkParam(cloud)) {
            list = governanceService.getServiceReport(cloud, date_begin, date_end);
        }
        if (list == null) {
            list = Lists.newArrayList();
        }
        return new ApiResponse.ApiResponseBuilder().data(list).build();
    }

    @RequestMapping("/error_data")
    @ResponseBody
    public ApiResponse errorData(String cloud) {
        List<ServiceError> list = Lists.newArrayList();
        if (checkParam(cloud)) {
            list = governanceService.getServiceError(cloud);
        }
        if (CollectionUtils.isEmpty(list)) {
            list = Lists.newArrayList();
        }

        return new ApiResponse.ApiResponseBuilder().data(list).build();
    }

    @RequestMapping("/getServiceIpRank")
    @ResponseBody
    public ApiResponse getServiceIpRank(String cloud, String event, String date_begin, String date_end) {
        List<ServiceIpRank> serviceIpRank = null;
        if ( checkParam(cloud)) {
            serviceIpRank = governanceService.getServiceIpRank(cloud, event, date_begin, date_end);
        }

        return new ApiResponse.ApiResponseBuilder().data(serviceIpRank).build();
    }

    @RequestMapping("/getServiceSourceCallTimes")
    @ResponseBody
    public ApiResponse getServiceSourceCallTimes(String cloud, String event, String date_begin, String date_end) {
        List list = null;
        if ( checkParam(cloud)) {
            list = governanceService.getServiceSourceCallTimes(cloud, event, date_begin, date_end);
        }
        return new ApiResponse.ApiResponseBuilder().data(list).build();
    }


    @RequestMapping("/addService")
    @ResponseBody
    public ApiResponse addService(String cloud, String serviceName, String serviceUrl) {
        if (checkParam(cloud)) {
            InstanceDetail detail = new InstanceDetail(serviceName, serviceUrl);
            ServiceInstance<InstanceDetail> instance = new ServiceInstance(serviceName, UUID.randomUUID().toString(), null, null, null, detail, 0, ServiceType.DYNAMIC, null);
            try {
                ZkUtil.add(chooseZkClient.get(cloud), ZkUtil.EXTRA_SERVICE_NODE_PATH + "/" + serviceName, new String(ZkUtil.serializer.serialize(instance)));
            } catch (Exception e) {
                return new ApiResponse.ApiResponseBuilder().data("ok").build();
            }
        }
        return new ApiResponse.ApiResponseBuilder().data("error").build();
    }

    @RequestMapping("/modifyGovernance")
    @ResponseBody
    public ApiResponse modifyGovernance(String cloud, String serviceType, String serviceName, String serviceDesc, Integer timeoutMs,
                                        Integer degrade, Integer limit, Double globalLimit, String serviceLimit,
                                        String executorGroup, String loadBalanceStrategy, String weight) {
        try {
            ServiceGovernStrategy serviceGovernStrategy = new ServiceGovernStrategy();
            serviceGovernStrategy.setServiceDesc(serviceDesc);
            serviceGovernStrategy.setTimeoutMs(timeoutMs);
            serviceGovernStrategy.setExecutorGroup(executorGroup);
            if (serviceType.equals("zk")) {
                serviceGovernStrategy.setLoadBalanceStrategy(loadBalanceStrategy);
                DegradeStrategy degradeStrategy = new DegradeStrategy();
                degradeStrategy.setDegrade(degrade == 1);
                serviceGovernStrategy.setDegradeStrategy(degradeStrategy);
                if (limit == 1) {
                    if (globalLimit != null) {
                        SerLimitRateStrategy serLimitRateStrategy = new SerLimitRateStrategy();
                        serLimitRateStrategy.setGlobalLimit(globalLimit);
                        if (!StringUtils.isEmpty(serviceLimit)) {
                            String[] serviceLimits = serviceLimit.split(";");
                            Map<String, Double> srcCallerLimit = Maps.newHashMap();
                            for (String s : serviceLimits) {
                                srcCallerLimit.put(s.split("-")[0], Double.parseDouble(s.split("-")[1]));
                            }
                            serLimitRateStrategy.setSrcCallerLimit(srcCallerLimit);
                        }
                        serviceGovernStrategy.setSerLimitRateStrategy(serLimitRateStrategy);
                    }
                }
                if ("weightRoundRobin".equals(loadBalanceStrategy)) {
                    serviceGovernStrategy.setWeight(weight);
                }
            }
            String data = JSON.toJSONString(serviceGovernStrategy);
            CuratorFramework client = chooseZkClient.get(cloud);
            String path = ZkUtil.SERVICE_GOVERNANCE_NODE_PATH + "/" + serviceName;
            if (client.checkExists().forPath(path) != null) {
                ZkUtil.update(client, path, data);
            } else {
                ZkUtil.add(client, path, data);
            }
            path = ZkUtil.CONFIG_NODE_PATH + "/" + String.format(ZkUtil.HYSTRIX_SERVICE_TIMEOUT, serviceName);
            if (client.checkExists().forPath(path) != null) {
                ZkUtil.update(client, path, String.valueOf(timeoutMs));
            } else {
                ZkUtil.add(client, path, String.valueOf(timeoutMs));
            }
            return new ApiResponse.ApiResponseBuilder().data("ok").build();
        } catch (Exception e) {
            logger.error("modifyServiceGovernance e is {}", e);
            return new ApiResponse.ApiResponseBuilder().data("error").build();
        }

    }

    @RequestMapping("/getServiceSourceLimit")
    @ResponseBody
    public ApiResponse getServiceSourceLimit(String cloud, String serviceName) {
        if (StringUtils.isEmpty(serviceName)) {
            return new ApiResponse.ApiResponseBuilder().build();
        }
        return new ApiResponse.ApiResponseBuilder().data(governanceService.getServiceSourceLimit(cloud, serviceName)).build();
    }

    @RequestMapping("/getServiceCallTime")
    @ResponseBody
    public ApiResponse getServiceCallTime(String cloud, String event) {
        if (StringUtils.isEmpty(event)) {
            return new ApiResponse.ApiResponseBuilder().build();
        }
        return new ApiResponse.ApiResponseBuilder().data(governanceService.getServiceCallTime(cloud, event)).build();
    }

    @RequestMapping("/getServiceTps")
    @ResponseBody
    public ApiResponse getServiceTps(String cloud, String event) {
        if (StringUtils.isEmpty(event)) {
            return new ApiResponse.ApiResponseBuilder().build();
        }
        return new ApiResponse.ApiResponseBuilder().data(governanceService.getServiceTps(cloud, event)).build();
    }

    @RequestMapping("/resourceGroup_data")
    @ResponseBody
    public ApiResponse resourceGroupDta(ResourceGroup group) {
        return new ApiResponse.ApiResponseBuilder().data(governanceService.queryAll(group)).build();
    }
    @RequestMapping("/resourceGroupCallRank")
    @ResponseBody
    public ApiResponse resourceGroupCallRank(ResourceGroup group) {
        return new ApiResponse.ApiResponseBuilder().data(governanceService.resourceGroupCallRank(group)).build();
    }
    @RequestMapping("/saveResourceGroup")
    @ResponseBody
    public ApiResponse saveGroup(ResourceGroup group) {
        try {
            governanceService.save(group);
            return new ApiResponse.ApiResponseBuilder().data("ok").build();
        } catch (Exception e) {
            logger.error("saveResourceGroup e is {}", e);
            return new ApiResponse.ApiResponseBuilder().data("error").build();
        }

    }

    @RequestMapping("/deleteResourceGroup")
    @ResponseBody
    public ApiResponse deleteResourceGroup(ResourceGroup group) {
        try {
            governanceService.delete(group);
            return new ApiResponse.ApiResponseBuilder().data("ok").build();
        } catch (Exception e) {
            logger.error("deleteResourceGroup e is {}", e);
            return new ApiResponse.ApiResponseBuilder().data("error").build();
        }

    }

    private boolean checkParam(String cloud){
        if ( "qCloud1".equals(cloud) || "qCloud2".equals(cloud) || "qCloud3".equals(cloud)) {
            return true;
        }
        return false;
    }


}
