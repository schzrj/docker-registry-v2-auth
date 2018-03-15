package com.zhangj.registry.auth.model;

import lombok.Data;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Data
public class RegistryImage {
    private String namespace;
    private String repo;
    private String tag;
}
