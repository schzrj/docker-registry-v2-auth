package com.zhangj.registry.auth.model;

import lombok.Data;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/13
 */
@Data
public class UserInfo {
    private Long userId;
    private String userName;
    private Boolean isAdmin;
}
