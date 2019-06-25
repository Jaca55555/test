package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.core.entity.user.Role;


public interface RoleRepository extends DataTablesRepository<Role, Integer>,JpaRepository<Role,Integer> {


}
