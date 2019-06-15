package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.repository.sys.OrganizationRepository;
import uz.maroqand.ecology.core.service.sys.OrganizationService;

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
    public List<Organization> getList() {
        return organizationRepository.findAll();
    }

    @Override
    public String getContractNumber(Integer organizationId) {
        Organization organization = organizationRepository.getOne(organizationId);
        organization.setLastNumber(organization.getLastNumber()+1);
        organizationRepository.save(organization);

        String number = organization.getLastNumber()+"-"+"/19";
        return number;
    }

}
