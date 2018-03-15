package com.zhangj.registry.auth.token;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/13
 */
@Data
@AllArgsConstructor
public class AccountLoginToken {
    private String userName;
    private String password;
}
