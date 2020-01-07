package uz.maroqand.ecology.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagment.entity.DocumentType;
import uz.maroqand.ecology.docmanagment.repository.DocumentTypeRepository;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentTypeService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentTypeServiceImpl(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    //GetById
    @Override
    @Cacheable(value = "documentTypeGetById", key = "#id", condition="#id != null", unless="#result == null")
    public DocumentType getById(Integer id) throws IllegalArgumentException {
        if(id==null) return null;
        return documentTypeRepository.getOne(id);
    }

    //PutById
    @Override
    @CachePut(value = "documentTypeGetById", key = "#id")
    public DocumentType updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return documentTypeRepository.getOne(id);
    }

    //GetStatusActive
    @Override
    @Cacheable("documentTypeGetStatusActive")
    public List<DocumentType> getStatusActive() {
        return documentTypeRepository.findByStatusTrue();
    }

    //RemoveAllStatusActiveFromCache
    @Override
    @CacheEvict(value = "documentTypeGetStatusActive", allEntries = true)
    public List<DocumentType> removeStatusActive() {
        return documentTypeRepository.findAll();
    }

    @Override
    public DataTablesOutput<DocumentType> getAll(DataTablesInput input) {
        return documentTypeRepository.findAll(input,getFilteringSpecification());
    }

    private static Specification<DocumentType> getFilteringSpecification() {
        return new Specification<DocumentType>() {
            @Override
            public Predicate toPredicate(Root<DocumentType> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    @Override
    public DocumentType create(DocumentType documentType) {
        documentType.setDeleted(Boolean.FALSE);
        documentType.setCreatedAt(new Date());
        return documentTypeRepository.save(documentType);
    }

    @Override
    public DocumentType update(DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }

}
