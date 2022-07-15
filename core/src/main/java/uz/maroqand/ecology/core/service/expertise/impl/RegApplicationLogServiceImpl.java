package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationLogRepository;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.user.UserService;
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
    private final UserService userService;

    @Autowired
    public RegApplicationLogServiceImpl(RegApplicationLogRepository regApplicationLogRepository, UserService userService) {
        this.regApplicationLogRepository = regApplicationLogRepository;
        this.userService = userService;
    }

    @Override
    public List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId){
        return regApplicationLogRepository.findByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplicationId);
    }

    @Override
    public List<RegApplicationLog> findAll() {
        return regApplicationLogRepository.findAll();
    }

    @Override
    public List<RegApplicationLog> getByRegApplicationIdAndType(Integer regApplicationId, LogType type){
        return regApplicationLogRepository.findByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(regApplicationId, type);
    }

    @Override
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
        return regApplicationLogRepository.findById(id).get();
    }

    @Override
    public RegApplicationLog getByRegApplcationId(Integer id) {
        return regApplicationLogRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(id);
    }

    @Override
    public RegApplicationLog getByRegApplcationIdAndType(Integer id, LogType type) {
        return regApplicationLogRepository.findTop1ByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(id,type);
    }

    @Override
    public RegApplicationLog findTop1ByStatusAndType(Integer regApplicationId,LogStatus status, LogType type) {
        return regApplicationLogRepository.findTop1ByStatusAndRegApplicationIdAndTypeAndDeletedFalseOrderByIdAsc(status,regApplicationId,type);
    }

    @Override
    public List<RegApplicationLog> findByStatusAndType(Integer regApplicationId,LogStatus status, LogType type) {
        return regApplicationLogRepository.findByRegApplicationIdAndStatusAndTypeAndDeletedFalse(regApplicationId,status,type);
    }


    @Override
    public List<RegApplicationLog> getByLogStatus(LogStatus status,Integer regApplicationId) {
        return regApplicationLogRepository.findAllByStatusAndRegApplicationIdAndDeletedFalseOrderByIdAsc(status,regApplicationId);
    }

    public RegApplicationLog create(
            RegApplication regApplication,
            LogType logType,
            String comment,
            User createdBy
    ){
        RegApplicationLog regApplicationLog = new RegApplicationLog();
        regApplicationLog.setRegApplicationId(regApplication.getId());
        regApplicationLog.setIndex(regApplication.getLogIndex());
        regApplicationLog.setComment(comment);

        regApplicationLog.setType(logType);
        regApplicationLog.setStatus(LogStatus.Initial);
        regApplicationLog.setCreatedAt(new Date());
        regApplicationLog.setCreatedById(createdBy.getId());

        if(logType.equals(LogType.AgreementComplete)){
            List<RegApplicationLog> regApplicationLogList = regApplicationLogRepository.findByRegApplicationIdAndTypeAndShowTrueAndDeletedFalseOrderByIdDesc(regApplication.getId(), logType);
            for (RegApplicationLog olgAgreementComplete: regApplicationLogList){
                olgAgreementComplete.setShow(false);
                regApplicationLogRepository.save(olgAgreementComplete);
            }
            regApplicationLog.setShow(true);
        }
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog update(
            RegApplicationLog regApplicationLog,
            LogStatus logStatus,
            String comment,
            Integer updateById
    ){

        regApplicationLog.setStatus(logStatus);
        regApplicationLog.setComment(comment);

        regApplicationLog.setUpdateAt(new Date());
        regApplicationLog.setUpdateById(updateById);
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog updateDocument(RegApplicationLog regApplicationLog){
        regApplicationLog.setUpdateAt(new Date());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public Date getDeadlineDate(Integer deadline,Date beginDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DAY_OF_MONTH, deadline);
        return calendar.getTime();
    }

    public RegApplicationLog getByIndex(Integer regApplicationId, LogType type, Integer index){
        return regApplicationLogRepository.findTopByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(regApplicationId, type, index);
    }

    public List<RegApplicationLog> getAllByIndex(Integer regApplicationId, LogType type, Integer index){
        return regApplicationLogRepository.findByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(regApplicationId, type, index);
    }

    @Override
    public List<RegApplicationLog> getAllByLogType(LogType logType) {
        return regApplicationLogRepository.findByTypeAndDeletedFalseOrderByIdDesc(logType);
    }

    @Override
    public Integer getLogCount(Integer id) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        User user = userService.getCurrentUserFromContext();
        if (id == null) return 0;
        LogType logType;
        switch (id) {
            case 0:
                logType = LogType.Confirm;
                break;
            case 1:
                logType = LogType.Forwarding;
                break;
            case 2:
                logType = LogType.Performer;
                break;
            case 3:
                logType = LogType.Agreement;
                break;
            case 4:
                logType = LogType.AgreementComplete;
                break;
            case 5:
                logType = LogType.ConclusionComplete;
                break;
            default:
                return 0;
        }
        List<RegApplicationLog> regApplicationLogList = getAllByLogType(logType);
        System.out.println("size==" + regApplicationLogList.size() + "   type=" + logType + "  id==" + id);
        Integer result = 0;

        if (logType.equals(LogType.ConclusionComplete)){
            for (RegApplicationLog regApplicationLog : regApplicationLogList) {
                if (regApplicationLog.getStatus().equals(LogStatus.Initial)
                        && regApplicationLog.getRegApplication().getReviewId().equals(user.getOrganizationId())
                ){
                    result++;
                }
            }
            return result;
        }

        for (RegApplicationLog regApplicationLog : regApplicationLogList) {
            if ( !regApplicationLog.getRegApplication().getDeleted()
                    && !hashMap.containsKey(regApplicationLog.getRegApplicationId())
                    && regApplicationLog.getRegApplication().getReviewId().equals(user.getOrganizationId())
                    && (regApplicationLog.getStatus().equals(LogStatus.Initial)|| regApplicationLog.getStatus().equals(LogStatus.Resend)
                    || (logType.equals(LogType.Agreement) && regApplicationLog.getStatus().equals(LogStatus.New)))
            ) {
                if (logType.equals(LogType.Agreement) || logType.equals(LogType.Performer)) {
                    if (regApplicationLog.getUpdateById() != null && regApplicationLog.getUpdateById().equals(user.getId())) {
                        result++;
                        hashMap.put(regApplicationLog.getRegApplicationId(), regApplicationLog.getRegApplicationId());
                    }
                }else{
                    result++;
                    hashMap.put(regApplicationLog.getRegApplicationId(), regApplicationLog.getRegApplicationId());
                }

            }
        }
        return result;
    }

    @Override
    public Integer countbyLogType0AndDeletedFalseOrganizationId() {
        Set<RegApplicationStatus> statuses=new HashSet<>();
        statuses.add(RegApplicationStatus.Initial);
        LogType type=LogType.Confirm;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndDeletedFalseAndStatus(type,organizationId,statuses);
    }
    @Override
    public Integer countbyLogType1AndDeletedFalseOrganizationId() {
        Set<LogStatus> statuses=new HashSet<>();
        statuses.add(LogStatus.Initial);
        statuses.add(LogStatus.Resend);
        LogType type=LogType.Forwarding;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndDeletedFalse(type,organizationId,statuses,false);

    }
    @Override
    public Integer countbyLogType2AndDeletedFalseOrganizationId() {
        Set<LogStatus> statuses=new HashSet<>();
        statuses.add(LogStatus.Initial);
        statuses.add(LogStatus.Resend);
        LogType type=LogType.Performer;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndUpdatedByIdAndDeletedFalse(type,organizationId,statuses,user.getId(),false);
    }
    @Override
    public Integer countbyLogType3AndDeletedFalseOrganizationId() {
        Set<LogStatus> statuses=new HashSet<>();
        statuses.add(LogStatus.New);
        statuses.add(LogStatus.Initial);
        LogType type=LogType.Agreement;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndUpdatedByIdAndDeletedFalse(type,organizationId,statuses,user.getId(),true);
    }
    @Override
    public Integer countbyLogType4AndDeletedFalseOrganizationId() {
        Set<LogStatus> statuses=new HashSet<>();
        statuses.add(LogStatus.Initial);
        statuses.add(LogStatus.Resend);
        LogType type=LogType.AgreementComplete;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndDeletedFalse(type,organizationId,statuses,true);
    }
    @Override
    public Integer countbyLogType5AndDeletedFalseOrganizationId() {
        Set<LogStatus> statuses=new HashSet<>();
        statuses.add(LogStatus.Initial);
        statuses.add(LogStatus.Resend);
        LogType type=LogType.ConclusionComplete;
        User user = userService.getCurrentUserFromContext();
        Integer organizationId=user.getOrganizationId();
        return regApplicationLogRepository.countByTypeAndOrganizationIdAndDeletedFalse(type,organizationId,statuses,true);
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
        User user = userService.getCurrentUserFromContext();
        Integer orgId = userService.isAdmin()||user.getRole().getId()==16?null:user.getOrganizationId();
        return regApplicationLogRepository.findAll(getFilteringSpecification(filterDto, createdById, updateById, type, status,orgId),pageable);
    }

    @Override
    public long findFilteredNumber(LogType agreement, Integer integer) {
        FilterDto filterDto = new FilterDto();
        filterDto.setStatusing(1);
        Pageable pageable = new PageRequest(0,10);
        long returnValue = regApplicationLogRepository.findAll(getFilteringSpecification(filterDto, null, integer, agreement, null,null),pageable).getTotalElements();
        return returnValue;
    }

    private static Specification<RegApplicationLog> getFilteringSpecification(
            final FilterDto filterDto,
            final Integer createdById,
            final Integer updateById,
            final LogType type,
            final LogStatus status,
            final Integer orgId
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
                System.out.println("updateById="+updateById);

                if(filterDto.getTin() != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("tin"), filterDto.getTin()));
                }
                if(orgId != null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("reviewId"), orgId));
                }
                if(StringUtils.trimToNull(filterDto.getName()) != null){
                    predicates.add(criteriaBuilder.like(root.join("regApplication").get("applicant").<String>get("name"), "%" + StringUtils.trimToNull(filterDto.getName()) + "%"));
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

                if (filterDto.getStatusing() != null){
                    List<LogStatus> list = new ArrayList<>();
                    list.add(LogStatus.Initial);
                    list.add(LogStatus.New);
                    list.add(LogStatus.Resend);
                    predicates.add(criteriaBuilder.in(root.get("status")).value(list));
                }else if (filterDto.getStatus() != null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), LogStatus.getLogStatus(filterDto.getStatus())));
                }


                if(createdById!=null){
                    predicates.add(criteriaBuilder.equal(root.get("createdById"), createdById));
                }
                if(updateById!=null){
                    predicates.add(criteriaBuilder.equal(root.get("updateById"), updateById));
                }

                if(type!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), type.getId()));
                    if(type==LogType.Agreement){
                        predicates.add(criteriaBuilder.notEqual(root.get("status"), 0));
                    }
                }
                if(status!=null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.getId()));
                }

                predicates.add(criteriaBuilder.equal(root.get("show"), true));
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}