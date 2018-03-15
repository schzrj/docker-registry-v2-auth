package com.zhangj.registry.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "components.auth")
public class RegistryAuthProperties {
    private String service;
    private String issuer;
    private String tokenExpire;

}
