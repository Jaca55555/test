package uz.maroqand.ecology.core.service.sys;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.Organization;

import java.util.List;

public interface OrganizationService {

    Organization getById(Integer id);
    Organization getByRegionId(Integer id);

    List<Organization> getList();

    String getContractNumber(Integer organizationId);

    List<String> getOrganizationNames();
    Organization save(Organization organization);
    Organization getByName(String organizationName);

    Organization create(Organization organization);
    Page<Organization> getFiltered(String name,String account,String address,Pageable pageable);
}
