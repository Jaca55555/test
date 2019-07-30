package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ChangeDeadlineDateStatus;
import uz.maroqand.ecology.core.entity.expertise.ChangeDeadlineDate;
import uz.maroqand.ecology.core.repository.expertise.ChangeDeadlineDateRepository;
import uz.maroqand.ecology.core.service.expertise.ChangeDeadlineDateService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Service
public class ChangeDeadlineDateServiceImpl implements ChangeDeadlineDateService {

    private final ChangeDeadlineDateRepository changeDeadlineDateRepository;

    public ChangeDeadlineDateServiceImpl(ChangeDeadlineDateRepository changeDeadlineDateRepository) {
        this.changeDeadlineDateRepository = changeDeadlineDateRepository;
    }

    @Override
    public ChangeDeadlineDate getById(Integer id) {
        return changeDeadlineDateRepository.findById(id).get();
    }

    @Override
    public Page<ChangeDeadlineDate> findFiltered(Integer regApplicationId, ChangeDeadlineDateStatus changeDeadlineDateStatus, Pageable pageable) {
        return changeDeadlineDateRepository.findAll(getFilteringSpecifications(regApplicationId,changeDeadlineDateStatus),pageable);
    }

    @Override
    public List<ChangeDeadlineDate> getListByRegApplicationId(Integer id) {
        return changeDeadlineDateRepository.findByRegApplicationId(id);
    }

    @Override
    public ChangeDeadlineDate save(ChangeDeadlineDate changeDeadlineDate) {
        return changeDeadlineDateRepository.save(changeDeadlineDate);
    }

    private static Specification<ChangeDeadlineDate> getFilteringSpecifications(
            final Integer regApplicationId,
            final ChangeDeadlineDateStatus changeDeadlineDateStatus
    ){
        return new Specification<ChangeDeadlineDate>() {
            @Override
            public Predicate toPredicate(Root<ChangeDeadlineDate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(regApplicationId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("regApplicationId"), regApplicationId));
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
