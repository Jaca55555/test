package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.Activity;

import java.util.List;

public interface ActivityService {

    List<Activity> getList();

    Activity getById(Integer id);

}
