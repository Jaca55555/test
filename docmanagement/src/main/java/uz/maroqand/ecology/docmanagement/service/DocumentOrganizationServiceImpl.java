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
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.repository.DocumentOrganizationRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentOrganizationService;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;

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
    public Set<DocumentOrganization> getByParent(Integer parentId) {
        return documentOrganizationRepository.findByParentAndDeletedFalse(parentId);
    }

    @Override
    public Set<Integer> getByOrganizationId(Integer organizationId) {
        return documentOrganizationRepository.findByStatusTrueAndOrganizationId(organizationId);
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
        return documentOrganizationRepository.findAll(input,getFilteringSpecification(null,null));
    }

    @Override
    public List<DocumentOrganization> getList(){
        return documentOrganizationRepository.findAll();
    }

    @Override
    public List<DocumentOrganization> getLevel(Integer organizationId,Integer id) {
        return documentOrganizationRepository.getAllByLevel(organizationId,id);
    }

    @Override
    public Page<DocumentOrganization> findFiltered(Integer id,Integer organizationId, String name, Integer status, Pageable pageable){
        return documentOrganizationRepository.findAll(getFilteringSpecification(id,organizationId, name, status), pageable);
    }

    @Override
    public Page<DocumentOrganization> getOrganizationList(String name,Integer organizationId, Pageable pageable) {
        return documentOrganizationRepository.findAll(getFilteringSpecification(name,organizationId),pageable);
    }

    private static Specification<DocumentOrganization> getFilteringSpecification(final String name,Integer organizationId) {
        return (Specification<DocumentOrganization>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            Join<DocumentOrganization, User> joinUser = root.join("user");
            if(name!=null){
                predicates.add( criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + name.toUpperCase() + "%") );
            }
            if(organizationId!=null){
                predicates.add(criteriaBuilder.equal(joinUser.get("organizationId"), organizationId));
            }

            predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    private static Specification<DocumentOrganization> getFilteringSpecification(Integer id,Integer organizationId, String name, Integer status){
        return (Specification<DocumentOrganization>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            Join<DocumentOrganization, User> joinUser = root.join("user");
            if(id != null)
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            if(name != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if(status != null)
                predicates.add(criteriaBuilder.equal(root.get("status"), status == 1));
            if(organizationId!=null){
                predicates.add(criteriaBuilder.equal(joinUser.get("organizationId"), organizationId));
            }
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
