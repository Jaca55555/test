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
import uz.maroqand.ecology.docmanagement.entity.DocumentView;
import uz.maroqand.ecology.docmanagement.entity.Journal;
import uz.maroqand.ecology.docmanagement.repository.DocumentViewRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentViewService;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class DocumentViewServiceImpl implements DocumentViewService {

    private final DocumentViewRepository documentViewRepository;

    @Autowired
    public DocumentViewServiceImpl(DocumentViewRepository documentViewRepository) {
        this.documentViewRepository = documentViewRepository;
    }

    @Override
    @Cacheable(value = "documentViewGetById", key = "#id",unless="#result == ''")
    public DocumentView getById(Integer id) {
        return documentViewRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    @CachePut(value = "documentViewGetById", key = "#id")
    public DocumentView updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return documentViewRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    @Cacheable("documentViewStatusActive")
    public List<DocumentView> getStatusActive() {
        return documentViewRepository.findByStatusTrue();
    }

    @Override
    public List<DocumentView> getStatusActiveAndByType(Integer organizationId,String type) {
        return documentViewRepository.findByStatusTrueAndType(organizationId,type);
    }

    @Override
    @CacheEvict(value = "documentViewStatusActive", allEntries = true)
    public List<DocumentView> documentViewGetStatusActive() {
        return documentViewRepository.findByStatusTrue();
    }

    @Override
    @CacheEvict(value = "documentViewStatusActive", allEntries = true)
    public DocumentView create(DocumentView documentView) {
        documentView.setDeleted(Boolean.FALSE);
        documentView.setCreatedAt(new Date());
        return documentViewRepository.save(documentView);
    }

    @Override
    public DocumentView update(DocumentView documentView) {
        return documentViewRepository.save(documentView);
    }

    @Override
    public DataTablesOutput<DocumentView> getAll(DataTablesInput input) {
        return documentViewRepository.findAll(input,getFilteringSpecification());
    }

    private static Specification<DocumentView> getFilteringSpecification() {
        return (Specification<DocumentView>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
            predicates.add( notDeleted );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<DocumentView> getFilteringSpecification(String name,Integer organizationId, Integer status, String locale){
        return (Specification<DocumentView>) (root, criteriaQuery, criteriaBuilder) -> {
            Join<DocumentView, User> joinUser = root.join("user");

            List<Predicate> predicates = new LinkedList<>();
            if(organizationId!=null){
                predicates.add(criteriaBuilder.equal(joinUser.get("organizationId"), organizationId));
            }
            if(name != null){
                if(locale.equals("oz"))
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + name.toLowerCase() + "%"));
                else if(locale.equals("ru"))
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameRu")), "%" + name.toLowerCase() + "%"));
            }
            if(status != null){
                if(status.equals(1))
                    predicates.add(criteriaBuilder.equal(root.get("status"), true));
                else if(status.equals(2))
                    predicates.add(criteriaBuilder.equal(root.get("status"), false));
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Page<DocumentView> findFiltered(String name,Integer organizationId, Integer status, String locale, Pageable pageable){
        return documentViewRepository.findAll(getFilteringSpecification(name,organizationId, status, locale), pageable);
    }

}
