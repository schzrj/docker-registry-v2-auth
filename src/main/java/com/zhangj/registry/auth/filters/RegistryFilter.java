package com.zhangj.registry.auth.filters;


import com.zhangj.registry.auth.model.AccessScope;
import com.zhangj.registry.auth.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Component
@Slf4j
public class RegistryFilter implements AccessFilter {
    @Override
    public void filter(UserInfo userInfo, AccessScope accessScope) {
        if (!accessScope.getName().equals("catalog")) {
            log.error("参数非法，参数是{}", accessScope.getName());
        }
        if (!userInfo.getIsAdmin()) {
            accessScope.setActions(new String[]{});
        }
    }
}
