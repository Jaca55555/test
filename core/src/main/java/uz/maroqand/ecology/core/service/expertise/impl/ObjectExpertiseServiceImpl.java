package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.repository.expertise.ObjectExpertiseRepository;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
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
    @Cacheable(value = "getObjectExpertiseList")
    public List<ObjectExpertise> getList() {
        return objectExpertiseRepository.findByDeletedFalse();
    }

    @Override
    @CachePut(value = "getObjectExpertiseList")
    public List<ObjectExpertise> updateList() {
        return objectExpertiseRepository.findByDeletedFalse();
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
    public Page<ObjectExpertise> findFiltered(Integer id,String name, String nameOz,String nameEn,String nameRu, Pageable pageable, Boolean deleted) {
        return objectExpertiseRepository.findAll(getFilteringSpecification(id,name,nameOz,nameEn,nameRu, deleted),pageable);
    }

    private static Specification<ObjectExpertise> getFilteringSpecification(
            final Integer id,
            final String name,
            final String nameOz,
            final String nameEn,
            final String nameRu,
            final Boolean deleted
    ) {
        return new Specification<ObjectExpertise>() {
            @Override
            public Predicate toPredicate(Root<ObjectExpertise> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (name != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.<String>get("name")),
                            "%" + name.toLowerCase() + "%"));
                }
                if (nameOz != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.<String>get("nameOz")),
                            "%" + nameOz.toLowerCase() + "%"));
                }
                if (nameEn != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.<String>get("nameEn")),
                            "%" + nameEn.toLowerCase() + "%"));
                }
                if (nameRu != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.<String>get("nameRu")),
                            "%" + nameRu.toLowerCase() + "%"));
                }
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                predicates.add(criteriaBuilder.equal(root.get("deleted"), deleted));
                /*Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );*/
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public ObjectExpertise delete(ObjectExpertise objectExpertise, Integer userId, String msg) {
        objectExpertise.setDeleted(true);
        objectExpertise.setUpdateBy(userId);
        objectExpertise.setUpdateAt(new Date());
        objectExpertise.setUpdateMessage(msg);
        return objectExpertiseRepository.save(objectExpertise);
    }
}
