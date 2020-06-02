package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.user.Permissions;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.user.UserRepository;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private static PasswordEncoder encoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, DepartmentService departmentService) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
    }

    @Override
    public List<User> getListByDepartmentAllParent(Integer departmentId) {
        if (departmentId==null) return null;
        Set<Integer> departmentIds = new HashSet<>();
        departmentIds.add(departmentId);
        List<Department> departmentList = departmentService.findByParentId(departmentId);
        for (Department department: departmentList){
            departmentIds.add(department.getId());
        }
        return userRepository.findByDepartmentIdInAndEnabledTrue(departmentIds);
    }

    @Override
    public User getByTelegramId(Integer telegramUserId) {
        return userRepository.findByTelegramUserIdAndEnabledTrue(telegramUserId);
    }

    @Override
    public Boolean isRegistrationUser(Integer telegramUserId) {
        User user = getByTelegramId(telegramUserId);
        if (user != null){
            System.out.println(true);
            return true;
        }
        return false;
    }

    @Override
    public User login(String login, String password) {
        User user = findByUsername(login);
        if (user==null || !passwordEncoder().matches(password, user.getPassword())
                ||!user.getEnabled().equals(Boolean.TRUE)) {
            return null;
        }
        return user;
    }

    @Override
    public List<User> getAllByTelegramUsers() {
        return userRepository.findAllByTelegramUserIdNotNullAndEnabledTrue();
    }

    @Override
    public List<User> getEmployeeList() {
        return userRepository.findAllByDepartmentIdNotNull();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.getOne(id);
    }

    @Override
    public User findById(Integer id, Integer organizationId) {
        return userRepository.findByIdAndOrganizationId(id, organizationId);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> findFiltered(
            Integer userId,
            String lastName,
            String firstName,
            String middleName,
            String userName,
            Integer organizationId,
            Integer departmentId,
            Integer positionId,
            Pageable pageable
    ) {
        return userRepository.findAll(getFilteringSpecification(userId,userName,lastName,firstName,middleName,organizationId,departmentId,positionId),pageable);
    }

    @Override
    public Page<User> findFilteredForEmployee(
            Integer userId,
            String lastName,
            String firstName,
            String middleName,
            String username,
            Integer organizationId,
            Integer departmentId,
            Integer positionId,
            Pageable pageable
    ) {
        return userRepository.findAll(getFilteringForEmployeeSpecification(userId,username,lastName,firstName,middleName,organizationId,departmentId,positionId),pageable);
    }

    private static Specification<User> getFilteringSpecification(
            final Integer userId,
            final String userName,
            final String lastName,
            final String firstName,
            final String middleName,
            final Integer organizationId,
            final Integer departmentId,
            final Integer positionId
    ) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                System.out.println("userId="+userId);
                System.out.println("userName="+userName);
                if (userId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), userId));
                }
                if (userName != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("username")),
                            "%" + userName.toLowerCase() + "%"));
                }
                if (lastName != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastname")),
                            "%" + lastName.toLowerCase() + "%"));
                }
                if (firstName != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstname")),
                            "%" + firstName.toLowerCase() + "%"));
                }
                if (middleName != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("middlename")),
                            "%" + middleName.toLowerCase() + "%"));
                }
                if (organizationId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("organizationId"), organizationId));
                }
                if (departmentId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId"), departmentId));
                }
                if (positionId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("positionId"), positionId));
                }
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    private static Specification<User> getFilteringForEmployeeSpecification(
            final Integer userId,
            final String userName,
            final String lastName,
            final String firstName,
            final String middleName,
            final Integer organizationId,
            final Integer departmentId,
            final Integer positionId
    ) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                System.out.println("userId="+userId);
                System.out.println("userName="+userName);
                System.out.println("lastName="+lastName);
                System.out.println("firstName="+firstName);
                System.out.println("middleName="+middleName);
                System.out.println("organizationId="+organizationId);
                System.out.println("departmentId="+departmentId);
                System.out.println("positionId="+positionId);
                if (userId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), userId));
                }
                if (userName != null) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + userName + "%"));
                }
                if (lastName != null) {
                    predicates.add(criteriaBuilder.like(root.get("lastname"), "%" + lastName + "%"));
                }
                if (firstName != null) {
                    predicates.add(criteriaBuilder.like(root.get("firstname"), "%" + firstName + "%"));
                }
                if (middleName != null) {
                    predicates.add(criteriaBuilder.like(root.get("middlename"), "%" + middleName + "%"));
                }

                if (organizationId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("organizationId"), organizationId));
                }

                if (departmentId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId"), departmentId));
                }else{
                    predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                }

                if (positionId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("positionId"), positionId));
                }
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUserFromContext() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public List<User> getEmployeesForForwarding(Integer organizationId){
        return userRepository.findByOrganizationId(organizationId);
    }

    public LogType getUserLogType(User user){
        if(user.getRole()!=null){
            for (Permissions permission:user.getRole().getPermissions()){
                switch (permission){
                    case EXPERTISE_CONFIRM: return LogType.Confirm;
                    case EXPERTISE_FORWARDING: return LogType.Forwarding;
                    case EXPERTISE_PERFORMER: return LogType.Performer;
                    case EXPERTISE_AGREEMENT: return LogType.Agreement;
                    case EXPERTISE_AGREEMENT_COMPLETE: return LogType.AgreementComplete;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getEmployeesForDocManage(String type) {
        List<User> users = new ArrayList<>();
        if (type.equals("chief"))
            users = userRepository.findAllByDepartmentIdNotNullOrderByIsExecuteChiefDesc();
        if (type.equals("controller"))
            users = userRepository.findAllByDepartmentIdNotNullOrderByIsExecuteControllerDesc();
        return users;
    }

    @Override
    public List<User> getEmployeesForNewDoc(String type) {
        List<User> users = new ArrayList<>();
        if (type.equals("chief"))
            users = userRepository.findAllByIsExecuteChiefTrue();
        if (type.equals("controller"))
            users = userRepository.findAllByIsExecuteControllerTrue();
        return users;
    }

    @Override
    public Integer getUserDepartmentId(Integer userId) {
        if (userId==null) return null;
        User user = findById(userId);
        if (user!=null && user.getDepartmentId()!=null){
            return user.getDepartmentId();
        }
        return null;
    }

    private PasswordEncoder passwordEncoder(){
        if (encoder == null){
            encoder = new BCryptPasswordEncoder();
        }
        return encoder;
    }

}
