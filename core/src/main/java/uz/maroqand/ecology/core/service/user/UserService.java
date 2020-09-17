package uz.maroqand.ecology.core.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
public interface UserService {

    List<User> getListByDepartmentAllParent(Integer departmentId);
    List<User> getByDepartmentId(Integer departmentId);
    User getByTelegramId(Integer telegramUserId);

    Boolean isRegistrationUser(Integer telegramUserId);

    User login(String login, String password);

    List<User> getAllByTelegramUsers();

    List<User> getEmployeeList();
    List<User> getAll();
    User findById(Integer id);

    User findById(Integer id, Integer organizationId);

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
            Integer controls,//for 1=performer or 2=agreement
            Pageable pageable
    );

    public User findByUsername(String username);

    public User getCurrentUserFromContext();

    List<User> getEmployeesForForwarding(Integer organizationId);

    List<User> getEmployeesPerformerForForwarding(Integer organizationId);

    List<User> getEmployeesAgreementForForwarding(Integer organizationId);

    LogType getUserLogType(User user);

    List<User> getEmployeesForDocManage(String type);
    List<User> getEmployeesForDocManageOrganization(String type,Integer organizationId);
    List<User> getEmployeesForDocManageAndIsExecutive(String type,Integer organizationId);
    List<User> getEmployeesForNewDoc(String type);

    Integer getUserDepartmentId(Integer userId);
}
