package com.yoho.service.governance.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午2:35 in 2017/12/27
 * @ModifyBy:
 */
@Data
@ToString
public class ResourceGroup {
    private Integer id;
    private String groupName;
    private String groupDesc;
    private Integer coreSize;
    private Integer maxQueueSize;
    private String cloud;


    private String host="0.0";

    private String cluster="0.0";

    private String active="0";

    private String queued="0";

    private String poolSize="0";

    private String maxActive="0";

    private String executions="0";

    private String queueSize="0";
}
