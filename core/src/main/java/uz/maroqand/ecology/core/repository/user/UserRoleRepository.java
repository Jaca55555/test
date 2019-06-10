package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.core.constant.user.Role;
import uz.maroqand.ecology.core.entity.user.UserRole;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    UserRole findByRole(Role role);

    List<UserRole> findByUserId(Integer id);
}
