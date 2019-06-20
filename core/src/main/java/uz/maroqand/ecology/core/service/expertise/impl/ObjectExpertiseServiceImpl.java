package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.repository.expertise.ObjectExpertiseRepository;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Service
public class ObjectExpertiseServiceImpl implements ObjectExpertiseService {

    private final ObjectExpertiseRepository objectExpertiseRepository;

    @Autowired
    public ObjectExpertiseServiceImpl(ObjectExpertiseRepository objectExpertiseRepository) {
        this.objectExpertiseRepository = objectExpertiseRepository;
    }

    @Override
    public List<ObjectExpertise> getList() {
        return objectExpertiseRepository.findAll();
    }

    @Override
    public ObjectExpertise getById(Integer id){
        return objectExpertiseRepository.getOne(id);
    }

    @Override
    public ObjectExpertise save(ObjectExpertise objectExpertise) {
        return objectExpertiseRepository.save(objectExpertise);
    }

    @Override
    public Page<ObjectExpertise> findFiltered(Integer id,String name, String nameRu, Pageable pageable) {
        return objectExpertiseRepository.findAll(getFilteringSpecification(id,name,nameRu),pageable);
    }

    private static Specification<ObjectExpertise> getFilteringSpecification(
            final Integer id,
            final String name,
            final String nameRu
    ) {
        return new Specification<ObjectExpertise>() {
            @Override
            public Predicate toPredicate(Root<ObjectExpertise> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (name != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("name"), "%" + name + "%"));
                }
                if (nameRu != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("nameRu"), "%" + nameRu + "%"));
                }
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }

                /*Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );*/
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
