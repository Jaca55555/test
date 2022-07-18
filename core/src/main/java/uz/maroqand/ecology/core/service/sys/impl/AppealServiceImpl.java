package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.sys.AppealRepository;
import uz.maroqand.ecology.core.service.sys.AppealService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class AppealServiceImpl implements AppealService {

    private AppealRepository appealRepository;

    @Autowired
    public AppealServiceImpl(AppealRepository appealRepository) {
        this.appealRepository = appealRepository;
    }

    private Map<Integer, Integer> newAppealMap = new HashMap<>();

    public void initialize(){
        List<Object[]> objects =  appealRepository.findIdByShowUserCommentCount();
        for (Object[] object:objects){
            newAppealMap.put(Integer.parseInt(object[0].toString()) , Integer.parseInt(object[1].toString()));
        }
    }

    public void updateByUserId(Integer userId) {
        Integer count = appealRepository.findIdByShowUserCommentCountUserId(userId);
        if (count != null && count > 0) {
            newAppealMap.put(userId, count);
        } else {
            if (newAppealMap.containsKey(userId)) {
                newAppealMap.put(userId, 0);
            }
        }
    }

    public Appeal create(Appeal appeal,User user){

        appeal.setAppealStatus(AppealStatus.Open);
        appeal.setDeleted(false);
        appeal.setCreatedAt(new Date());
        appeal.setCreatedById(user.getId());
        return appealRepository.save(appeal);
    }

    public Appeal update(Appeal appeal,User user){

        appeal.setUpdateAt(new Date());
        appeal.setUpdateById(user.getId());
        return appealRepository.save(appeal);
    }

    public Appeal updateCommentCount(Appeal appeal){
        return appealRepository.save(appeal);
    }

    public Appeal delete(Appeal appeal,User user){

        appeal.setDeleted(true);
        appeal.setUpdateAt(new Date());
        appeal.setUpdateById(user.getId());
        return appealRepository.save(appeal);
    }

    public Appeal getById(Integer id,Integer createdById){
        return appealRepository.findByIdAndCreatedByIdAndDeletedFalse(id,createdById);
    }

    @Override
    public Integer getbyStatus() {
        return appealRepository.countAllByAppealStatusAndDeletedFalse(AppealStatus.Open);
    }

    public Page<Appeal> findFiltered(
            Integer appealId,
            Integer appealType,
            String title,
            Date dateBegin,
            Date dateEnd,
            Integer status,
            Integer createdBy,
            Pageable pageable
    ) {
        return appealRepository.findAll(getFilteringSpecification( appealId, appealType, title, dateBegin, dateEnd, status,createdBy), pageable);
    }

    @Override
    public Appeal findById(Integer id) {
        return appealRepository.findById(id).orElse(null);
    }

    private static Specification<Appeal> getFilteringSpecification(
            final Integer appealId,
            final Integer appealType,
            final String title,
            final Date dateBegin,
            final Date dateEnd,
            final Integer status,
            final Integer createdBy
    ) {
        return new Specification<Appeal>() {
            @Override
            public Predicate toPredicate(Root<Appeal> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new LinkedList<>();

                System.out.println("appealId=="+appealId);
                System.out.println("appealType=="+appealType);
                System.out.println("dateBegin=="+dateBegin);
                System.out.println("dateEnd=="+dateEnd);
                System.out.println("title=="+title);
                System.out.println("createdBy=="+createdBy);

                if (appealId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), appealId));
                }
                if (appealType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("appealType"), appealType));
                }
                if (dateBegin != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));
                }
                if (title != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%" + title + "%"));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("appealStatus"), status));
                }

                if (createdBy != null) {
                    predicates.add(criteriaBuilder.equal(root.get("createdById"), createdBy));
                }

                // Show only registered and non-deleted
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                return overAll;
            }
        };
    }

    @Override
    public void createAppealLanding(String fullName, String companyName, String email, String phone, String message, String theme) {
        Appeal appeal = new Appeal();
        appeal.setAppealStatus(AppealStatus.Open);
        appeal.setAppealType(AppealType.Landing_Suggestion);
        appeal.setCompanyName(companyName);
        appeal.setDescription(message);
        appeal.setPersonEmail(email);
        appeal.setPhone(phone);
        appeal.setPersonFullName(fullName);
        appeal.setTitle(theme);
        appeal.setCreatedAt(new Date());
        appealRepository.save(appeal);
    }
}
