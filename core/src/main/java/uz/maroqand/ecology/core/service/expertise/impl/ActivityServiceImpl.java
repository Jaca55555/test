package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.repository.expertise.ActivityRepository;
import uz.maroqand.ecology.core.service.expertise.ActivityService;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> getList() {
        return activityRepository.findAll();
    }

    @Override
    public Activity getById(Integer id){
        return activityRepository.getOne(id);
    }

}
