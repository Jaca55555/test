package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationLogRepository;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class RegApplicationLogServiceImpl implements RegApplicationLogService {

    private final RegApplicationLogRepository regApplicationLogRepository;

    @Autowired
    public RegApplicationLogServiceImpl(RegApplicationLogRepository regApplicationLogRepository) {
        this.regApplicationLogRepository = regApplicationLogRepository;
    }

    public List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId){
        return regApplicationLogRepository.findByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplicationId);
    }

    public List<RegApplicationLog> getByIds(Set<Integer> ids){
        return regApplicationLogRepository.findByIdInOrderByIdDesc(ids);
    }

    public RegApplicationLog getUserAgreementByIds(Set<Integer> ids){
        List<RegApplicationLog> regApplicationLogList = getByIds(ids);
        for (RegApplicationLog regApplicationLog:regApplicationLogList){

        }
        return null;
    }

    public RegApplicationLog getById(Integer id){
        if(id==null){
            return null;
        }
        return regApplicationLogRepository.getOne(id);
    }

    public RegApplicationLog create(
            RegApplication regApplication,
            LogType logType,
            String comment,
            User createdBy
    ){
        RegApplicationLog regApplicationLog = new RegApplicationLog();
        regApplicationLog.setRegApplicationId(regApplication.getId());
        regApplicationLog.setComment(comment);

        regApplicationLog.setType(logType);
        regApplicationLog.setStatus(LogStatus.Initial);
        regApplicationLog.setCreatedAt(new Date());
        regApplicationLog.setCreatedById(createdBy.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog update(
            RegApplicationLog regApplicationLog,
            LogStatus logStatus,
            String comment,
            User updateBy
    ){

        regApplicationLog.setStatus(logStatus);
        regApplicationLog.setComment(comment);

        regApplicationLog.setUpdateAt(new Date());
        regApplicationLog.setUpdateById(updateBy.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog updateDocument(RegApplicationLog regApplicationLog, User user){
        regApplicationLog.setUpdateAt(new Date());
        regApplicationLog.setUpdateById(user.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public Date getDeadlineDate(Integer deadline,Date beginDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DAY_OF_MONTH, deadline);
        return calendar.getTime();
    }


    @Override
    public Page<RegApplicationLog> findFiltered(
            FilterDto filterDto,
            Integer createdById,
            Integer updateById,
            LogType type,
            LogStatus status,
            Pageable pageable
    ) {
        return regApplicationLogRepository.findAll(getFilteringSpecification(filterDto, createdById, updateById, type, status),pageable);
    }

    private static Specification<RegApplicationLog> getFilteringSpecification(
            final FilterDto filterDto,
            final Integer createdById,
            final Integer updateById,
            final LogType type,
            final LogStatus status
    ) {
        return new Specification<RegApplicationLog>() {
            @Override
            public Predicate toPredicate(Root<RegApplicationLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                System.out.println("activityId="+filterDto.getActivityId());
                System.out.println("tin="+filterDto.getTin());
                System.out.println("name="+filterDto.getName());
                System.out.println("status1="+filterDto.getStatus());
                System.out.println("type="+type);
                System.out.println("status="+status);

                if(filterDto.getTin() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("tin"), filterDto.getTin()));
                }
                if(StringUtils.trimToNull(filterDto.getName()) != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").<String>get("name"), "%" + StringUtils.trimToNull(filterDto.getName()) + "%"));
                }
                if(filterDto.getApplicationId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("id"), filterDto.getApplicationId()));
                }

                if(filterDto.getRegionId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("regionId"), filterDto.getRegionId()));
                }
                if(filterDto.getSubRegionId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("subRegionId"), filterDto.getSubRegionId()));
                }

                Date dateBegin = DateParser.TryParse(filterDto.getRegDateBegin(), Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(filterDto.getRegDateEnd(), Common.uzbekistanDateFormat);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.join("regApplication").get("createdAt").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.join("regApplication").get("createdAt").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.join("regApplication").get("createdAt").as(Date.class), dateBegin, dateEnd));
                }

                Date deadlineDateBegin = DateParser.TryParse(filterDto.getDeadlineDateBegin(), Common.uzbekistanDateFormat);
                Date deadlineDateEnd = DateParser.TryParse(filterDto.getDeadlineDateEnd(), Common.uzbekistanDateFormat);
                if (deadlineDateBegin != null && deadlineDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.join("regApplication").get("deadlineDate").as(Date.class), deadlineDateBegin));
                }
                if (deadlineDateEnd != null && deadlineDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.join("regApplication").get("deadlineDate").as(Date.class), deadlineDateEnd));
                }
                if (deadlineDateBegin != null && deadlineDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.join("regApplication").get("deadlineDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
                }

                if(filterDto.getActivityId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("activityId"), filterDto.getActivityId()));
                }
                if(filterDto.getObjectId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("objectId"), filterDto.getObjectId()));
                }



                if(filterDto.getStatus()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), LogStatus.getLogStatus(filterDto.getStatus())));
                }
                if(createdById!=null){
                    criteriaBuilder.equal(root.get("createdById"), createdById);
                }
                if(updateById!=null){
                    criteriaBuilder.equal(root.get("updateById"), updateById);
                }

                if(type!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), type.getId()));
                }
                if(status!=null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.getId()));
                }

                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}