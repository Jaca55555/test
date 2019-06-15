package uz.maroqand.ecology.core.service.user;


import uz.maroqand.ecology.core.entity.user.Role;

import java.util.List;

public interface RoleService {

    List<Role> getRoleList();

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Role role);

}
