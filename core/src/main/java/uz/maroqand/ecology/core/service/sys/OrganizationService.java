package uz.maroqand.ecology.core.service.sys;

import uz.maroqand.ecology.core.entity.sys.Organization;

import java.util.List;

public interface OrganizationService {

    Organization getById(Integer id);

    List<Organization> getList();

    String getContractNumber(Integer organizationId);

    List<String> getOrganizationNames();

    Organization getByName(String organizationName);

    Organization create(Organization organization);
}
