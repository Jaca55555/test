package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Activity;

import java.util.List;

public interface ActivityService {

    List<Activity> getList();

    Activity getById(Integer id);

    Page<Activity> findFiltered(Integer id, Category category , String name,String locale, Pageable pageable);

    Activity createActivity(Activity activity);

    Activity updateActivity(Activity activity);
}
