package com.zhangj.registry.auth.controller;

import com.zhangj.registry.auth.service.RegistryAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Controller
@RequestMapping("/api/registry")
public class RegistryAuthController {
    @Autowired
    private RegistryAuthService registryAuthService;

    @ResponseBody
    @RequestMapping(value = "auth", method = RequestMethod.GET)
    public void auth(HttpServletRequest request, HttpServletResponse response) {
        try {

            String token = registryAuthService.auth(request);
            if (StringUtils.isEmpty(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                PrintWriter printWriter = response.getWriter();
                response.setContentType("application/json");
                printWriter.write(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
