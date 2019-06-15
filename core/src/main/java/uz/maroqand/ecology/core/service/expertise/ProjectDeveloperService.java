package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;

import java.util.List;

public interface ProjectDeveloperService {

    List<ProjectDeveloper> getList();

    ProjectDeveloper getById(Integer id);

    ProjectDeveloper save(ProjectDeveloper projectDeveloper);
}
