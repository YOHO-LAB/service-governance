package com.yoho.service.governance.model;

import lombok.Data;


/**
 * @Author: lingjie.meng
 * @Descroption:
 * @Date: craete on 下午4:35 in 2017/12/29
 * @ModifyBy:
 */
@Data
public class ApiSource {
    private String id;
    private String target;
    private String source;

    public ApiSource() {
    }

    public ApiSource(String target, String source) {
        this.target = target;
        this.source = source;
    }
}
