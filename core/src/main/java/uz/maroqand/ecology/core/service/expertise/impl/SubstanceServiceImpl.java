package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.expertise.Substance;
import uz.maroqand.ecology.core.repository.expertise.SubstanceRepository;
import uz.maroqand.ecology.core.service.expertise.SubstanceService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Service
public class SubstanceServiceImpl implements SubstanceService {

    private final SubstanceRepository substanceRepository;

    public SubstanceServiceImpl(SubstanceRepository substanceRepository) {
        this.substanceRepository = substanceRepository;
    }

    @Override
    public Substance save(Substance substance) {
        return substanceRepository.save(substance);
    }

    @Override
    @Cacheable(value = "getSubstanceById", key = "{#id}",condition="#id!= null",unless="#result == ''")
    public Substance getById(Integer id) {
        return substanceRepository.getOne(id);
    }

    @Override
    public Page<Substance> getAll(Pageable pageable, SubstanceType type) {
        return this.getAll(pageable,type, false);
    }

    @Override
    @Cacheable(value = "getSubstanceList")
    public List<Substance> getList() {
        return substanceRepository.findByDeletedFalse();
    }


    public Page<Substance> getAll(Pageable pageable,SubstanceType type, Boolean deleted){
        Specification spec = new Specification<Substance>() {
            @Override
            public Predicate toPredicate(Root<Substance> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(type!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }

                predicates.add(criteriaBuilder.equal(root.get("deleted"), deleted));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return substanceRepository.findAll(spec, pageable);
    }

    @Override
    public Substance delete(Substance substance, Integer userId) {
       substance.setDeleted(true);
       substance.setUpdateAt(new Date());
       substance.setUpdateBy(userId);
        return substanceRepository.save(substance);
    }

    @Override
    @CachePut(value = "getMaterialList")
    public List<Substance> updateList() {
        return substanceRepository.findByDeletedFalse();
    }
}
