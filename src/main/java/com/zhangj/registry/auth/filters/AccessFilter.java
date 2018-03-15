package com.zhangj.registry.auth.filters;


import com.zhangj.registry.auth.model.AccessScope;
import com.zhangj.registry.auth.model.UserInfo;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
public interface AccessFilter {
    void filter(UserInfo userInfo, AccessScope accessScope);
}
