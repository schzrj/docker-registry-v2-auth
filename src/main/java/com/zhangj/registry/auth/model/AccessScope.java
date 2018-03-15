package com.zhangj.registry.auth.model;

import lombok.Data;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Data
public class AccessScope {
    private String type;
    private String name;
    private String[] actions;

    public AccessScope(String scope) {
        String[] items = scope.split(":");
        this.setType(items[0]);
        if (items.length > 1) {
            this.setName(items[1]);
        }
        if (items.length > 2) {
            this.setActions(items[2].split(","));
        }
    }

}