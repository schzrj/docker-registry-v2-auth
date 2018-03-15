package test.com.zhangj.registry.auth.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/15
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "test.com.zhangj.registry.auth.service",
        "com.zhangj.registry.auth"
})
public class RegistryAuthApplication {
    public static void main(String[] args) {
        SpringApplication application=new SpringApplication(RegistryAuthApplication.class);
        application.run(args);
    }
}
