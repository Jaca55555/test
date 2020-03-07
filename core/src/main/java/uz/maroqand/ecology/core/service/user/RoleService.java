package uz.maroqand.ecology.core.service.user;


import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.core.entity.user.Role;

import java.util.List;

public interface RoleService {

    DataTablesOutput<Role> getAll(DataTablesInput input);

    DataTablesOutput<Role> getAll(Integer organizationId, DataTablesInput input);

    Role getById(Integer id);

    List<Role> getRoleList();

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Role role);

}
