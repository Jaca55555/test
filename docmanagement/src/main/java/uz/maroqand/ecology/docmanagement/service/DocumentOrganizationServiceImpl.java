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

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
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

    @Override
    @Cacheable(value = "organizationGetByName", key = "#name",unless="#result == ''")
    public DocumentOrganization getByName(String name) {
        return documentOrganizationRepository.getByName(name);
    }

    @Override
    public List<String> getDocumentOrganizationNames(){
        List<DocumentOrganization> orgs = getList();
        List<String> names = new ArrayList<String>(orgs.size());
        for(DocumentOrganization documentOrganization: orgs){
            names.add(documentOrganization.getName());
        }
        return names;
    }

    //PutById
    @Override
    @CachePut(value = "organizationGetById", key = "#id")
    public DocumentOrganization updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return documentOrganizationRepository.findByIdAndDeletedFalse(id);
    }

    //PutByName
    @Override
    @CachePut(value = "organizationGetById", key = "#name")
    public DocumentOrganization updateByNameFromCache(String name) {
        if(name==null)return null;
        return documentOrganizationRepository.getByName(name);
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
    public List<DocumentOrganization> getList(){
        return documentOrganizationRepository.findAll();
    }

    @Override
    public List<DocumentOrganization> getLevel(Integer id) {
        return documentOrganizationRepository.getAllByLevel(id);
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
        return (Specification<DocumentOrganization>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if(name!=null){
                predicates.add( criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + name.toUpperCase() + "%") );
            }
            predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    private static Specification<DocumentOrganization> getFilteringSpecification(Integer id, String name, Integer status){
        return (Specification<DocumentOrganization>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if(id != null)
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            if(name != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if(status != null)
                predicates.add(criteriaBuilder.equal(root.get("status"), status == 1));

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
