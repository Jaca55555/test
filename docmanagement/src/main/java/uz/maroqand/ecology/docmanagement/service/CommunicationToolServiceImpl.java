package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.entity.CommunicationTool;
import uz.maroqand.ecology.docmanagement.repository.CommunicationToolRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.CommunicationToolService;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class CommunicationToolServiceImpl implements CommunicationToolService {

    private final CommunicationToolRepository communicationToolRepository;

    @Autowired
    public CommunicationToolServiceImpl(CommunicationToolRepository communicationToolRepository) {
        this.communicationToolRepository = communicationToolRepository;
    }

    //GetById
    @Override
    @Cacheable(value = "communicationToolGetById", key = "#id",unless="#result == ''")
    public CommunicationTool getById(Integer id) {
        return updateCacheableById(id);
    }

    //PutById
    @Override
    @CachePut(value = "communicationToolGetById", key = "#id")
    public CommunicationTool updateCacheableById(Integer id) {
        if(id==null)return null;
        return communicationToolRepository.getOne(id);
    }

    //GetStatusActive
    @Override
    @Cacheable("communicationToolGetStatusActive")
    public List<CommunicationTool> getStatusActive() {
        return communicationToolRepository.findByStatusTrueOrderByIdAsc();
    }

    //RemoveAllStatusActiveFromCache
    @CacheEvict(value = "communicationToolGetStatusActive", allEntries = true)
    public List<CommunicationTool> updateStatusActive() {
        return communicationToolRepository.findByStatusTrueOrderByIdAsc();
    }

    public CommunicationTool create(CommunicationTool communicationTool) {
        communicationTool.setDeleted(Boolean.FALSE);
        communicationTool.setCreatedAt(new Date());
        return communicationToolRepository.save(communicationTool);
    }


    @Override
    public Page<CommunicationTool> findFiltered(
            Integer id,
            String name,
            Integer organizationId,
            Pageable pageAble
    ){
        return communicationToolRepository.findAll(getFilteringSpecification2(id, name,organizationId), pageAble);
    }

    private static Specification<CommunicationTool> getFilteringSpecification2(Integer id, String name,Integer organizationId) {
        return (Specification<CommunicationTool>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }
            Join<CommunicationTool, User> joinUser = root.join("user");
            if(organizationId!=null){
                predicates.add(criteriaBuilder.equal(joinUser.get("organizationId"), organizationId));
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public CommunicationTool update(CommunicationTool communicationTool) {
        return communicationToolRepository.save(communicationTool);
    }

    @Override
    public DataTablesOutput<CommunicationTool> getAll(DataTablesInput input) {
        return communicationToolRepository.findAll(input,getFilteringSpecification());
    }

    private static Specification<CommunicationTool> getFilteringSpecification() {
        return (Specification<CommunicationTool>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
            predicates.add( notDeleted );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}