package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.repository.expertise.MaterialRepository;
import uz.maroqand.ecology.core.service.expertise.MaterialService;

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
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    @Cacheable(value = "getMaterialById", key = "{#id}",condition="#id!= null",unless="#result == ''")
    public Material getById(Integer id){
        return materialRepository.getOne(id);
    }

    @Override
    public Page<Material> getAll(Pageable pageable){
        return this.getAll(pageable, false);
    }

    public Page<Material> getAll(Pageable pageable, Boolean deleted){
        Specification spec = new Specification<Material>() {
            @Override
            public Predicate toPredicate(Root<Material> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                predicates.add(criteriaBuilder.equal(root.get("deleted"), deleted));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return materialRepository.findAll(spec, pageable);
    }

    @Override
    public Material save(Material material){
        return materialRepository.save(material);
    }

    @Override
    @Cacheable(value = "getMaterialList")
    public List<Material> getList() {
        return materialRepository.findByDeletedFalse();
    }

    @Override
    @CachePut(value = "getMaterialList")
    public List<Material> updateList() {
        return materialRepository.findByDeletedFalse();
    }

    @Override
    public Material delete(Material material, Integer userId, String msg) {
        material.setDeleted(true);
        material.setUpdateAt(new Date());
        material.setUpdateBy(userId);
        material.setUpdateMessage(msg);
        return materialRepository.save(material);
    }
}