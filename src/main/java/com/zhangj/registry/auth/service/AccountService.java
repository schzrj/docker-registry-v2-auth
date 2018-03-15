package com.zhangj.registry.auth.service;

import com.zhangj.registry.auth.model.Account;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/13
 */
public interface AccountService {

    /**
     * 项目成员登录
     * @param userName 用户名
     * @param password 密码
     * @return 项目成员信息
     */
    Account login(String userName, String password);

    /**
     * 获取项目成员信息
     * @param userName 项目成员用户名
     * @return 项目成员信息
     */
    Account findByAccount(String userName);

    /**
     * 该项目成员是否是管理员角色
     * @param userId
     * @return
     */
    Boolean isAdmin(Long userId);
}
