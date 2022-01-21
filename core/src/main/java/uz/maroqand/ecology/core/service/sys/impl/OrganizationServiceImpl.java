package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.repository.sys.OrganizationRepository;
import uz.maroqand.ecology.core.service.sys.OrganizationService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization getById(Integer id) {
        return organizationRepository.getOne(id);
    }

    @Override
    public Organization getByRegionId(Integer id) {
        return organizationRepository.findTop1ByRegionId(id);
    }

    @Override
    public Organization getByTin(Integer tin) {
        return organizationRepository.findByTin(tin);
    }

    @Override
    public List<Organization> getList() {
        return organizationRepository.findAll();
    }

    @Override
    public String getContractNumber(Integer organizationId) {
        Organization organization = organizationRepository.getOne(organizationId);
        organization.setLastNumber(organization.getLastNumber()+1);
        organizationRepository.save(organization);

        String number = organization.getLastNumber()+"-"+"/22";
        return number;
    }

    @Override
    public List<String> getOrganizationNames(){
        List<Organization> orgs = getList();
        List<String> names = new ArrayList<String>(orgs.size());
        for(Organization organization: orgs){
            names.add(organization.getName());
        }
        return names;
    }

    @Override
    public Organization save(Organization organization) {
        return  organizationRepository.save(organization);
    }

    private static Specification<Organization> getFilteringSpecification(String name, String address, String account) {
        return (Specification<Organization>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }
            if (address != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address")),
                        "%" + address.toLowerCase() + "%"));
            }
            if (account != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("account")),
                        "%" + account.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    @Override
    public Organization getByName(String name){
        return organizationRepository.getByName(name);
    }
    @Override
    public Organization create(Organization organization){
        return organizationRepository.save(organization);
    }
    @Override
    public Page<Organization> getFiltered(String name,String account,String address,Pageable pageable) {
        return organizationRepository.findAll(getFilteringSpecification(name,address,account), pageable);
    }




}
