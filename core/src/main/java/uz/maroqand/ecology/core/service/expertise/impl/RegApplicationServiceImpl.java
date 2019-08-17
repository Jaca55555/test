package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStep;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationRepository;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class RegApplicationServiceImpl implements RegApplicationService {

    private RegApplicationRepository regApplicationRepository;

    @Autowired
    public RegApplicationServiceImpl(RegApplicationRepository regApplicationRepository) {
        this.regApplicationRepository = regApplicationRepository;
    }

    public RegApplication create(User user){
        RegApplication regApplication = new RegApplication();
        regApplication.setCreatedAt(new Date());
        regApplication.setCreatedById(user.getId());
        regApplication.setStatus(RegApplicationStatus.Initial);
        regApplication.setStep(RegApplicationStep.APPLICANT);

        regApplicationRepository.save(regApplication);
        return regApplication;
    }

    @Override
    public List<RegApplication> getByClientId(Integer id) {
        return regApplicationRepository.findByApplicantId(id);
    }

    @Override
    public List<RegApplication> getAllByDeletedFalse() {
        return regApplicationRepository.findAllByDeletedFalse();
    }

    @Override
    public List<RegApplication> getAllByPerfomerIdNotNullDeletedFalse() {
        return regApplicationRepository.findAllByPerformerIdNotNullAndDeletedFalseOrderByIdDesc();
    }

    @Override
    public Boolean sendSMSCode(String mobilePhone) {
        String code = createSMSCode();
        System.out.println(code);
        return true;
    }

    public void update(RegApplication regApplication){
        regApplication.setUpdateAt(new Date());
        regApplicationRepository.save(regApplication);
    }

    public RegApplication getById(Integer id) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndDeletedFalse(id);
    }

    public RegApplication getById(Integer id, Integer createdBy) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndCreatedByIdAndDeletedFalse(id, createdBy);
    }

    @Override
    public Page<RegApplication> findFiltered(
            FilterDto filterDto,
            Integer reviewId,
            LogType logType,
            Integer performerId,
            Integer userId,
            Pageable pageable
    ) {
        return regApplicationRepository.findAll(getFilteringSpecification(filterDto, reviewId, logType, performerId, userId),pageable);
    }

    private static Specification<RegApplication> getFilteringSpecification(
            final FilterDto filterDto,
            final Integer reviewId,
            final LogType logType,
            final Integer performerId,
            final Integer userId
    ) {
        return new Specification<RegApplication>() {
            @Override
            public Predicate toPredicate(Root<RegApplication> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                //Ko'rib chiquvchi organization
                if(reviewId!=null){
                    criteriaBuilder.equal(root.get("reviewId"),reviewId);
                }

                System.out.println("logType="+logType);
                if(logType!=null){
                    switch (logType){
                        case Confirm:
                            predicates.add(criteriaBuilder.isNotNull(root.get("confirmLogId")));break;
                        case Forwarding:
                            predicates.add(criteriaBuilder.isNotNull(root.get("forwardingLogId")));break;
                        case Performer:
                            predicates.add(criteriaBuilder.isNotNull(root.get("performerLogId")));break;
                        /*case Agreement:
                            predicates.add(criteriaBuilder.isNotNull(root.get("agreementLogId")));break;*/
                        case AgreementComplete:
                            predicates.add(criteriaBuilder.isNotNull(root.get("agreementCompleteLogId")));break;
                    }
                }


                if(filterDto.getTin() != null){
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getTin()));
                }
                if(StringUtils.trimToNull(filterDto.getName()) != null){
                    predicates.add(criteriaBuilder.equal(root.join("applicant").<String>get("name"), "%" + StringUtils.trimToNull(filterDto.getName()) + "%"));
                }
                if(filterDto.getApplicationId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), filterDto.getApplicationId()));
                }

                if(filterDto.getRegionId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("regionId"), filterDto.getRegionId()));
                }
                if(filterDto.getSubRegionId() != null){
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("subRegionId"), filterDto.getSubRegionId()));
                }

                Date dateBegin = DateParser.TryParse(filterDto.getRegDateBegin(), Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(filterDto.getRegDateEnd(), Common.uzbekistanDateFormat);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));
                }

                Date deadlineDateBegin = DateParser.TryParse(filterDto.getDeadlineDateBegin(), Common.uzbekistanDateFormat);
                Date deadlineDateEnd = DateParser.TryParse(filterDto.getDeadlineDateEnd(), Common.uzbekistanDateFormat);
                if (deadlineDateBegin != null && deadlineDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deadlineDate").as(Date.class), deadlineDateBegin));
                }
                if (deadlineDateEnd != null && deadlineDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deadlineDate").as(Date.class), deadlineDateEnd));
                }
                if (deadlineDateBegin != null && deadlineDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("deadlineDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
                }

                if(filterDto.getActivityId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("activityId"), filterDto.getActivityId()));
                }
                if(filterDto.getObjectId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("objectId"), filterDto.getObjectId()));
                }

                if(performerId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("performerId"), performerId));
                }

                if(userId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("createdById"), userId));
                }

                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    private String createSMSCode() {
        return String.valueOf(100000L + (long) (new Random().nextDouble() * 899999L));
    }
}
