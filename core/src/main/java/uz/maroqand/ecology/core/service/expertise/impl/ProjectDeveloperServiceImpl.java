package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;
import uz.maroqand.ecology.core.repository.expertise.ProjectDeveloperRepository;
import uz.maroqand.ecology.core.service.expertise.ProjectDeveloperService;

import java.util.List;

@Service
public class ProjectDeveloperServiceImpl implements ProjectDeveloperService {

    private final ProjectDeveloperRepository projectDeveloperRepository;

    @Autowired
    public ProjectDeveloperServiceImpl(ProjectDeveloperRepository projectDeveloperRepository) {
        this.projectDeveloperRepository = projectDeveloperRepository;
    }

    @Override
    public List<ProjectDeveloper> getList() {
        return projectDeveloperRepository.findAll();
    }

    @Override
    public ProjectDeveloper getById(Integer id) {
        if(id==null){
            return new ProjectDeveloper();
        }
        return projectDeveloperRepository.findById(id).orElse(null);
    }

    @Override
    public ProjectDeveloper save(ProjectDeveloper projectDeveloper) {
        return projectDeveloperRepository.save(projectDeveloper);
    }
}
