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
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.repository.DocumentTypeRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;

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
    public List<DocumentType> updateStatusActive() {
        return documentTypeRepository.findByStatusTrue();
    }

    @Override
    public Page<DocumentType> getFiltered(DocumentTypeEnum type, String name, Boolean status , Pageable pageable) {
        return documentTypeRepository.findAll(getFilteringSpecification(type, name, status), pageable);
    }

    @Override
    public DataTablesOutput<DocumentType> getAll(DataTablesInput input) {
        return documentTypeRepository.findAll(input);
    }

    private static Specification<DocumentType> getFilteringSpecification(DocumentTypeEnum type, String name, Boolean status) {
        return (Specification<DocumentType>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }
            predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
