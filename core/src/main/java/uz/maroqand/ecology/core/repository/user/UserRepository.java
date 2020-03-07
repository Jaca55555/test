package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019..
 * (uz)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    User findByTinAndLeTinIsNull(Integer tin);

    User findByTinAndLeTin(Integer tin, Integer leTin);

    User findByIdAndOrganizationId(Integer id, Integer organizationId);

    List<User> findByTin(Integer tin);

    List<User> findAllByDepartmentIdNotNull();

    List<User> findAllByDepartmentIdNotNullOrderByIsExecuteChiefDesc();

    List<User> findAllByDepartmentIdNotNullOrderByIsExecuteControllerDesc();

    List<User> findAllByIsExecuteChiefTrue();

    List<User> findAllByIsExecuteControllerTrue();

    List<User> findByOrganizationId(Integer organizationId);

    List<User> findByDepartmentIdInAndEnabledTrue(Set<Integer> departmentIds);

}
