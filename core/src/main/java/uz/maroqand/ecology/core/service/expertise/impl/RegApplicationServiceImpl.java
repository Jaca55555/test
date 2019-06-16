package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationRepository;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class RegApplicationServiceImpl implements RegApplicationService {

    private RegApplicationRepository regApplicationRepository;

    @Autowired
    public RegApplicationServiceImpl(RegApplicationRepository regApplicationRepository) {
        this.regApplicationRepository = regApplicationRepository;
    }

    public RegApplication create(User user){
        RegApplication regApplication = new RegApplication();
        regApplication.setCreatedAt(new Date());
        regApplication.setCreatedById(user.getId());

        regApplicationRepository.save(regApplication);
        return regApplication;
    }

    public void save(RegApplication regApplication){
        regApplicationRepository.save(regApplication);
    }

    public RegApplication getById(Integer id) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndDeletedFalse(id);
    }

    public RegApplication getById(Integer id, Integer createdBy) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndCreatedByIdAndDeletedFalse(id, createdBy);
    }

    @Override
    public Page<RegApplication> findFiltered(Integer userId, Pageable pageable) {
        return regApplicationRepository.findAll(getFilteringSpecification(userId),pageable);
    }

    private static Specification<RegApplication> getFilteringSpecification(
            final Integer userId
    ) {
        return new Specification<RegApplication>() {
            @Override
            public Predicate toPredicate(Root<RegApplication> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(userId!=null){
                    Predicate notDeleted = criteriaBuilder.equal(root.get("createdById"), userId);
                }

                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}
