package uz.maroqand.ecology.core.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
public interface UserService {

    List<User> getEmployeeList();

    User findById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    Page<User> findFiltered(
            Integer userId,
            String lastName,
            String firstName,
            String middleName,
            String username,
            Integer organizationId,
            Integer departmentId,
            Integer positionId,
            Pageable pageable
    );

    Page<User> findFilteredForEmployee(
            Integer userId,
            String lastName,
            String firstName,
            String middleName,
            String username,
            Integer organizationId,
            Integer departmentId,
            Integer positionId,
            Pageable pageable
    );

    public User findByUsername(String username);

    public User getCurrentUserFromContext();

    List<User> getEmployeesForForwarding(Integer organizationId);

    LogType getUserLogType(User user);

    List<User> getEmployeesForDocManage(String type);

    Integer getUserDepartmentId(Integer userId);
}
