package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.repository.expertise.ActivityRepository;
import uz.maroqand.ecology.core.service.expertise.ActivityService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
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
    public List<Activity> getByInCategory(List<Category> categories){
        return activityRepository.findByCategoryIn(categories);
    }

    @Override
    public Activity getById(Integer id){
        return activityRepository.findById(id).get();
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Page<Activity> findFiltered(Integer id, Category category, String name, String locale, Pageable pageable) {
        return activityRepository.findAll(getFilteringSpecifications(id,category,name,locale),pageable);
    }

    private static Specification<Activity> getFilteringSpecifications(
            final Integer id,
            final Category category,
            final String name,
            final String locale
            ){
        return new Specification<Activity>() {
            @Override
            public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(id!=null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if(category!=null){
                    predicates.add(criteriaBuilder.equal(root.get("category"),category));
                }
                if (locale.equals("uz")){
                    predicates.add(criteriaBuilder.like(root.get("name"),"%" + name + "%"));
                }
                if (locale.equals("ru")){
                    predicates.add(criteriaBuilder.like(root.get("nameRu"),"%" + name + "%"));
                }
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
