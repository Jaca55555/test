package uz.maroqand.ecology.docmanagment.service;

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
import uz.maroqand.ecology.docmanagment.entity.CommunicationTool;
import uz.maroqand.ecology.docmanagment.repository.CommunicationToolRepository;
import uz.maroqand.ecology.docmanagment.service.interfaces.CommunicationToolService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        return communicationToolRepository.findByIdAndDeletedFalse(id);
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
    public void removeCacheableStatusActive() { }

    //GetList
    @Override
    @Cacheable("communicationToolGetList")
    public List<CommunicationTool> getList() {
        return communicationToolRepository.findByStatusTrueOrderByIdAsc();
    }

    //RemoveAll
    @CacheEvict(value = "communicationToolGetList", allEntries = true)
    public void removeList() {}

    public CommunicationTool create(CommunicationTool communicationTool) {
        communicationTool.setDeleted(Boolean.FALSE);
        communicationTool.setCreatedAt(new Date());
        return communicationToolRepository.save(communicationTool);
    }


    @Override
    public Page<CommunicationTool> findFiltered(
            Integer id,
            String name,
            Pageable pageAble
    ){
        return communicationToolRepository.findAll(getFilteringSpecification2(id, name), pageAble);
    }

    private static Specification<CommunicationTool> getFilteringSpecification2(Integer id, String name) {
        return new Specification<CommunicationTool>() {
            @Override
            public Predicate toPredicate(Root<CommunicationTool> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if (name != null) {
                    predicates.add(criteriaBuilder.equal(root.get("name"),"%" + name + "%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
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
        return new Specification<CommunicationTool>() {
            @Override
            public Predicate toPredicate(Root<CommunicationTool> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}