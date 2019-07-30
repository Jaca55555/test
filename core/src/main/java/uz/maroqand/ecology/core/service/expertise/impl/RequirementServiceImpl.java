package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.repository.expertise.RequirementRepository;
import uz.maroqand.ecology.core.service.expertise.RequirementService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class RequirementServiceImpl implements RequirementService {

    private final RequirementRepository requirementRepository;

    @Autowired
    public RequirementServiceImpl(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public Requirement getById(Integer id){
        return requirementRepository.getOne(id);
    }

    @Override
    public List<Requirement> getRequirementMaterials(Integer objectExpertiseId, Category category){
        return requirementRepository.findByObjectExpertiseIdAndCategory(objectExpertiseId,category);
    }

    @Override
    public List<Requirement> getRequirementExpertise(Integer objectExpertiseId){
        return requirementRepository.findByObjectExpertiseId(objectExpertiseId);
    }

    @Override
    public List<Requirement> getAllList() {
        return requirementRepository.findAll();
    }

    @Override
    public Page<Requirement> findFiltered(Integer id, Integer objectExpertiseId, Category category, Integer materialId, Integer reviewId, Double qty, Integer deadline, Pageable pageable) {
        return requirementRepository.findAll(getFilteringSpecification(id,objectExpertiseId,category,materialId,reviewId,qty,deadline),pageable);
    }

    @Override
    public Requirement save(Requirement requirement) {
        return requirementRepository.save(requirement);
    }

    private static Specification<Requirement> getFilteringSpecification(
            final Integer id,
            final Integer objectExpertiseId,
            final Category category,
            final Integer materialId,
            final Integer reviewId,
            final Double qty,
            final Integer deadline
    ) {
        return new Specification<Requirement>() {
            @Override
            public Predicate toPredicate(Root<Requirement> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if (objectExpertiseId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("objectExpertiseId"), objectExpertiseId));
                }
                if (materialId != null) {
                    predicates.add(criteriaBuilder.in(root.join("materials").in(materialId)));
                }
                if (reviewId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("reviewId"), reviewId));
                }
                if (qty != null) {
                    predicates.add(criteriaBuilder.equal(root.get("qty"), qty));
                }
                if (deadline != null) {
                    predicates.add(criteriaBuilder.equal(root.get("deadline"), deadline));
                }
                if (category != null) {
                    predicates.add(criteriaBuilder.equal(root.get("category"), category.ordinal()));
                }
                /*Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );*/
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}