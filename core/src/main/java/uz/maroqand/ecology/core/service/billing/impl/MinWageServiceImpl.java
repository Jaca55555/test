package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.repository.billing.MinWageRepository;
import uz.maroqand.ecology.core.service.billing.MinWageService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class MinWageServiceImpl implements MinWageService {

    private final MinWageRepository minWageRepository;

    @Autowired
    public MinWageServiceImpl(MinWageRepository minWageRepository) {
        this.minWageRepository = minWageRepository;
    }

    public MinWage getMinWage(){
        return minWageRepository.findFirst1ByBeginDateLessThanEqualOrderByBeginDateDesc(new Date());
    }

    @Override
    public MinWage getById(Integer id) {
        return minWageRepository.findById(id).get();
    }

    @Override
    public Page<MinWage> findFiltered(Integer id,Pageable pageable) {
        return this.findFiltered(id, false, pageable);
    }

    public Page<MinWage> findFiltered(Integer id, Boolean deleted, Pageable pageable) {
        return minWageRepository.findAll(getFilteringSpecifications(id, deleted),pageable);
    }

    @Override
    public MinWage createMinWage(MinWage minWage) {
        return minWageRepository.save(minWage);
    }

    @Override
    public MinWage updateMinWage(MinWage minWage) {
        return minWageRepository.save(minWage);
    }

    private static Specification<MinWage> getFilteringSpecifications(
            final Integer id,
            final Boolean deleted
    ){
        return new Specification<MinWage>() {
            @Override
            public Predicate toPredicate(Root<MinWage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if (id!=null){
                    predicates.add(criteriaBuilder.equal(root.get("id"),id));
                }
                predicates.add(criteriaBuilder.equal(root.get("deleted"),deleted));

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public MinWage delete(MinWage minWage, Integer userId, String msg) {
        minWage.setDeleted(true);
        minWage.setUpdateAt(new Date());
        minWage.setUpdateBy(userId);
        minWage.setUpdateMessage(msg);
        return minWageRepository.save(minWage);
    }
}