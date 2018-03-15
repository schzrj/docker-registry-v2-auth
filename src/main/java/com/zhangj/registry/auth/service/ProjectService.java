package com.zhangj.registry.auth.service;

import com.zhangj.registry.auth.model.Project;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/13
 */
public interface ProjectService {
    /**
     * 获取项目信息
     * @param project 项目名称
     * @return
     */
    Project getProjectInfo(String project);

    /**
     * 获取项目成员角色
     * @param projectId 项目id
     * @param userId 项目成员账户id
     * @return
     */
    Integer getProjectRole(Long projectId, Long userId);
}
