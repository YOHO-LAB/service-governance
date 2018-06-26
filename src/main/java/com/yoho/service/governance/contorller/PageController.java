package com.yoho.service.governance.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:52 in 2017/12/28
 * @ModifyBy:
 */
@Controller
public class PageController {

    @RequestMapping("/login")
    public String manager() {
        return "login";
    }



    @RequestMapping("/manager")
    public String manager(Map<String, Object> map) {
        map.put("pageName", "service-manager");
        return "service-manager";
    }

    @RequestMapping("/report")
    public String report(Map<String, Object> map) {
        map.put("pageName", "service-report");
        map.put("date_begin", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00");
        map.put("date_end", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 23:59:59");
        return "service-report";
    }

    @RequestMapping("/errors")
    public String errors(Map<String, Object> map) {
        map.put("pageName", "service-errors");
        return "service-errors";
    }

    @RequestMapping("/resourceGroup")
    public String sourceGroup(Map<String, Object> map) {
        map.put("pageName", "resource-group");
        return "resource-group";
    }



}
