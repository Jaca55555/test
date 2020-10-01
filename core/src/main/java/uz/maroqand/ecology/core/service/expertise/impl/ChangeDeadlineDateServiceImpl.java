package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ChangeDeadlineDateStatus;
import uz.maroqand.ecology.core.entity.expertise.ChangeDeadlineDate;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.ChangeDeadlineDateRepository;
import uz.maroqand.ecology.core.service.expertise.ChangeDeadlineDateService;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Service
public class ChangeDeadlineDateServiceImpl implements ChangeDeadlineDateService {

    private final ChangeDeadlineDateRepository changeDeadlineDateRepository;
    private final UserService userService;

    public ChangeDeadlineDateServiceImpl(ChangeDeadlineDateRepository changeDeadlineDateRepository, UserService userService) {
        this.changeDeadlineDateRepository = changeDeadlineDateRepository;
        this.userService = userService;
    }

    @Override
    public ChangeDeadlineDate getById(Integer id) {
        return changeDeadlineDateRepository.findById(id).get();
    }

    @Override
    public Page<ChangeDeadlineDate> findFiltered(
            Integer regApplicationId,
            ChangeDeadlineDateStatus changeDeadlineDateStatus,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        return changeDeadlineDateRepository.findAll(getFilteringSpecifications(regApplicationId,changeDeadlineDateStatus,user.getOrganizationId()),pageable);
    }

    @Override
    public List<ChangeDeadlineDate> getListByRegApplicationId(Integer id) {
        return changeDeadlineDateRepository.findByRegApplicationId(id);
    }

    @Override
    public ChangeDeadlineDate save(ChangeDeadlineDate changeDeadlineDate) {
        return changeDeadlineDateRepository.save(changeDeadlineDate);
    }

    @Override
    public ChangeDeadlineDate getByRegApplicationId(Integer id) {
        return changeDeadlineDateRepository.findTop1ByRegApplicationIdOrderByIdDesc(id);
    }

    @Override
    public Integer getCountByStatusInitial() {
        return changeDeadlineDateRepository.countByStatus(ChangeDeadlineDateStatus.Initial);
    }

    private static Specification<ChangeDeadlineDate> getFilteringSpecifications(
            final Integer regApplicationId,
            final ChangeDeadlineDateStatus changeDeadlineDateStatus,
            final Integer reviewId
            ){
        return new Specification<ChangeDeadlineDate>() {
            @Override
            public Predicate toPredicate(Root<ChangeDeadlineDate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(regApplicationId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("regApplicationId"), regApplicationId));
                }
                if(reviewId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("reviewId"), reviewId));
                }
                if(changeDeadlineDateStatus!=null){
                    predicates.add(criteriaBuilder.equal(root.get("changeDeadlineDateStatus"),changeDeadlineDateStatus.ordinal()));
                }
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
