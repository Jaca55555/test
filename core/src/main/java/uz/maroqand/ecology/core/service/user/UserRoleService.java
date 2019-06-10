package uz.maroqand.ecology.core.service.user;


import uz.maroqand.ecology.core.entity.user.UserRole;

import java.util.List;

public interface UserRoleService {

    List<UserRole> getByUserId(Integer id);

    UserRole createUserRole(UserRole userRole);

    UserRole updateUserRole(UserRole userRole);

    void deleteUserRole(UserRole userRole);

}
