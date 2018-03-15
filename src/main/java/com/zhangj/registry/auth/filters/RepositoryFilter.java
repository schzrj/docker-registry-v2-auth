package com.zhangj.registry.auth.filters;


import com.zhangj.registry.auth.enums.ProjectMemberRole;
import com.zhangj.registry.auth.model.AccessScope;
import com.zhangj.registry.auth.model.Project;
import com.zhangj.registry.auth.model.RegistryImage;
import com.zhangj.registry.auth.model.UserInfo;
import com.zhangj.registry.auth.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhangjun
 * @description
 * @date 2018/1/12
 */
@Component
public class RepositoryFilter implements AccessFilter {
    @Autowired
    private ProjectService projectService;

    @Override
    public void filter(UserInfo userInfoDto, AccessScope accessScope) {
        RegistryImage registryImage = new RegistryImage();
        String projectInfo = "";
        String[] repo = accessScope.getName().split("/");
        if (repo.length >= 2) {
            String[] temp = repo[1].split(":");
            registryImage.setNamespace(repo[0]);
            registryImage.setRepo(temp[0]);
            if (temp.length == 2) {
                registryImage.setTag(temp[1]);
            }
            projectInfo = registryImage.getNamespace();
        } else {
            projectInfo = repo[0];
        }

        StringBuffer permission = new StringBuffer();
        Project project = null;
       if (StringUtils.isNotEmpty(projectInfo)) {
            project = projectService.getProjectInfo(projectInfo);
        }
        if (project == null) {
            accessScope.setActions(new String[]{});
            return;
        }
        if (userInfoDto.getIsAdmin()) {
            permission.append("pull,push,*");
        } else {
           Integer projectRole = projectService.getProjectRole(project.getId(), userInfoDto.getUserId());
            if (projectRole != null) {

                permission.append(getImageAcl(projectRole));
            }
        }
        accessScope.setActions(permission.toString().split(","));
    }

    private String getImageAcl(int role) {
        Map<Integer, String> roleImageAcl = new HashMap<>();
        roleImageAcl.put(ProjectMemberRole.MASTER.value(), "*");
        roleImageAcl.put(ProjectMemberRole.DEVELOPER.value(), "pull,push");
        roleImageAcl.put(ProjectMemberRole.GUEST.value(), "pull");
        return roleImageAcl.get(role);
    }
}
