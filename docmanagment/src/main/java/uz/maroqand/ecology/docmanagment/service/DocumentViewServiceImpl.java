package uz.maroqand.ecology.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagment.entity.DocumentView;
import uz.maroqand.ecology.docmanagment.repository.DocumentViewRepository;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentViewService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    @CacheEvict(value = "documentViewStatusActive", allEntries = true)
    public List<DocumentView> documentViewGetStatusActive() {
        return documentViewRepository.findByStatusTrue();
    }

    @Override
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
        return new Specification<DocumentView>() {
            @Override
            public Predicate toPredicate(Root<DocumentView> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}