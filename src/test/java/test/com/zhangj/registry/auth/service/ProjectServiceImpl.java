package test.com.zhangj.registry.auth.service;

import com.zhangj.registry.auth.enums.ProjectMemberRole;
import com.zhangj.registry.auth.model.Project;
import com.zhangj.registry.auth.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/15
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Override
    public Project getProjectInfo(String project) {
        Project temp = new Project();
        temp.setId(1L);
        temp.setName("test");
        return temp;
    }

    @Override
    public Integer getProjectRole(Long projectId, Long userId) {
        return ProjectMemberRole.DEVELOPER.value();
    }
}
