package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.user.Permissions;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.user.UserRepository;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getEmployeeList() {
        return userRepository.findAllByDepartmentIdNotNull();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.getOne(id);
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

    public List<User> findPerformerList(){
        return userRepository.findAll();
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
}
