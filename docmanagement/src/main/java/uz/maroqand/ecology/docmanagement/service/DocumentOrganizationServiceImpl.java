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
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.repository.DocumentOrganizationRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentOrganizationService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Service
public class DocumentOrganizationServiceImpl implements DocumentOrganizationService {

    private final DocumentOrganizationRepository documentOrganizationRepository;

    @Autowired
    public DocumentOrganizationServiceImpl(DocumentOrganizationRepository documentOrganizationRepository) {
        this.documentOrganizationRepository = documentOrganizationRepository;
    }

    @Override
    @Cacheable(value = "organizationGetById", key = "#id",unless="#result == ''")
    public DocumentOrganization getById(Integer id) {
        return documentOrganizationRepository.findByIdAndDeletedFalse(id);
    }

    //PutById
    @Override
    @CachePut(value = "organizationGetById", key = "#id")
    public DocumentOrganization updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return documentOrganizationRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public DocumentOrganization getByName(String name) {
        return documentOrganizationRepository.findByNameContaining(name);
    }

    //GetStatusActive
    @Override
    @Cacheable("organizationGetStatusActive")
    public List<DocumentOrganization> getStatusActive() {
        return documentOrganizationRepository.findByStatusTrue();
    }

    //RemoveAllStatusActiveFromCache
    @Override
    @CacheEvict(value = "organizationGetStatusActive", allEntries = true)
    public List<DocumentOrganization> organizationGetStatusActive() {
        return documentOrganizationRepository.findByStatusTrue();
    }

    @Override
    public DataTablesOutput<DocumentOrganization> getAll(DataTablesInput input) {
        return documentOrganizationRepository.findAll(input,getFilteringSpecification(null));
    }

    @Override
    public Page<DocumentOrganization> findFiltered(Integer id, String name, Integer status, Pageable pageable){
        return documentOrganizationRepository.findAll(getFilteringSpecification(id, name, status), pageable);
    }

    @Override
    public Page<DocumentOrganization> getOrganizationList(String name, Pageable pageable) {
        return documentOrganizationRepository.findAll(getFilteringSpecification(name),pageable);
    }

    private static Specification<DocumentOrganization> getFilteringSpecification(final String name) {
        return new Specification<DocumentOrganization>() {
            @Override
            public Predicate toPredicate(Root<DocumentOrganization> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if(name!=null){
                    predicates.add( criteriaBuilder.like(root.get("name"), "%" + name.toUpperCase() + "%") );
                }
                predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
    private static Specification<DocumentOrganization> getFilteringSpecification(Integer id, String name, Integer status){
        return new Specification<DocumentOrganization>(){
            @Override
            public Predicate toPredicate(Root<DocumentOrganization> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new LinkedList<>();

                if(id != null)
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                if(name != null)
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                if(status != null)
                    predicates.add(criteriaBuilder.equal(root.get("status"), status == 1));

                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
    public DocumentOrganization create(DocumentOrganization organization) {
        organization.setDeleted(Boolean.FALSE);
        organization.setCreatedAt(new Date());
        return documentOrganizationRepository.save(organization);
    }

    public DocumentOrganization update(DocumentOrganization organization) {
        return documentOrganizationRepository.save(organization);
    }
}
