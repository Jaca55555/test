package uz.maroqand.ecology.docmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 */

@Service
public class DocumentTaskSubServiceImpl implements DocumentTaskSubService {

    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final DocumentSubService documentSubService;
    private final HelperService helperService;

    @Autowired
    public DocumentTaskSubServiceImpl(DocumentTaskSubRepository documentTaskSubRepository, DocumentSubService documentSubService, HelperService helperService) {
        this.documentTaskSubRepository = documentTaskSubRepository;
        this.documentSubService = documentSubService;
        this.helperService = helperService;
    }

    @Override
    public DocumentTaskSub getById(Integer id) {
        return documentTaskSubRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public DocumentTaskSub update(DocumentTaskSub taskSub) {
        taskSub.setUpdateAt(new Date());
        return documentTaskSubRepository.save(taskSub);
    }

    @Override
    public String buildTree(Integer level,List<DocumentTaskSub> documentTaskSubList, Map<Integer,DocumentTaskSub> taskSubMap,String locale){
        String tree="";
        for (DocumentTaskSub taskSub: documentTaskSubList) {
            if (taskSub.getLevel().equals(level) && !taskSubMap.containsKey(taskSub.getId())){
                taskSubMap.put(taskSub.getId(),taskSub);
                String getReceiverName =  helperService.getUserFullNameById(taskSub.getReceiverId()) + " (" + helperService.getPositionName(taskSub.getReceiver().getPositionId(),locale)+")";
                tree="[{text:\"" + getReceiverName + "\", tags:" + taskSub.getId();
                List<DocumentTaskSub> taskSubs = getListByTaskIdAndLevel(taskSub.getTaskId(),(level+1));
                if (taskSubs.size()>0){
                    tree+= ",nodes:" + buildTree((level+1),documentTaskSubList,taskSubMap,locale) + "}]";
                }else{
                    tree+="}]";
                }
            }
        }
        return tree;
    }

    @Override
    public List<DocumentTaskSub> getListByDocId(Integer docId) {
        return documentTaskSubRepository.findByDocumentIdAndDeletedFalseOrderByIdAsc(docId);
    }

    @Override
    public List<DocumentTaskSub> getListByTaskIdAndLevel(Integer taskId, Integer level) {
        return documentTaskSubRepository.findByTaskIdAndLevelAndDeletedFalse(taskId,level);
    }

    @Override
    public void allTaskSubCompleteGetTaskId(Integer taskId) {
        List<DocumentTaskSub> documentTaskSubList = documentTaskSubRepository.findByTaskIdAndDeletedFalse(taskId);
        for (DocumentTaskSub documentTaskSub:documentTaskSubList) {
            documentTaskSub.setStatus(TaskSubStatus.Complete.getId());
            update(documentTaskSub);
        }
    }

    @Override
    public List<DocumentTaskSub> getListByDocIdAndTaskId(Integer docId,Integer taskId) {
        return documentTaskSubRepository.findByDocumentIdAndTaskIdAndDeletedFalseOrderByIdAsc(docId,taskId);
    }

    @Override
    public DocumentTaskSub createNewSubTask(Integer level,Integer docId, Integer taskId, String content, Date dueDate, Integer type, Integer senderId, Integer receiverId, Integer departmentId) {
        DocumentTaskSub documentTaskSub = new DocumentTaskSub();
        documentTaskSub.setDocumentId(docId);
        documentTaskSub.setTaskId(taskId);
        documentTaskSub.setContent(content.trim());
        documentTaskSub.setDueDate(dueDate);
        documentTaskSub.setType(type);
        documentTaskSub.setStatus(TaskSubStatus.New.getId());
        documentTaskSub.setSenderId(senderId);
        documentTaskSub.setReceiverId(receiverId);
        documentTaskSub.setDepartmentId(departmentId);
        documentTaskSub.setDeleted(Boolean.FALSE);
        documentTaskSub.setCreatedAt(new Date());
        documentTaskSub.setCreatedById(senderId);
        documentTaskSub.setLevel(level!=null?(level+1):1);
        return documentTaskSubRepository.save(documentTaskSub);
    }

    @Override
    public Page<DocumentTaskSub> findFiltered(
            Integer organizationId,
            Integer documentTypeId,

            Integer documentOrganizationId,
            String docRegNumber,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            String taskContent,
            String content,
            Integer performerId,
            Integer taskSubType,
            Integer taskSubStatus,

            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    ) {
        return documentTaskSubRepository.findAll(getSpesification(
                organizationId, documentTypeId,
                documentOrganizationId, docRegNumber, registrationNumber, dateBegin, dateEnd, taskContent, content, performerId, taskSubType, taskSubStatus,
                deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId), pageable);
    }

    private static Specification<DocumentTaskSub> getSpesification(
            final Integer organizationId,
            final Integer documentTypeId,

            final Integer documentOrganizationId,
            final String docRegNumber,
            final String registrationNumber,
            final Date dateBegin,
            final Date dateEnd,
            final String taskContent,
            final String content,
            final Integer performerId,
            final Integer taskSubType,
            final Integer taskSubStatus,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId
    ) {
        return (Specification<DocumentTaskSub>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
                if (organizationId != null) {
                    //ushbu tashkilotga tegishli hujjatlar chiqishi uchun, user boshqa organizationga o'tsa eskisi ko'rinmaydi
                    predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
                }
                if (documentTypeId != null) {
                    //kiruvchi va chiquvchi hujjatlar ajratish uchun
                    predicates.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
                }

                if (StringUtils.trimToNull(docRegNumber) != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + docRegNumber.toLowerCase() + "%"));
                }
                if (StringUtils.trimToNull(registrationNumber) != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + registrationNumber.toLowerCase() + "%"));
                }

                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), dateBegin, dateEnd));
                }
                if (StringUtils.trimToNull(content) != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + content.toLowerCase() + "%"));
                }

                if (StringUtils.trimToNull(taskContent) != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + taskContent.toLowerCase() + "%"));
                }
                if (performerId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), performerId));
                }
                if (taskSubType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                if (taskSubStatus != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
                }


                if (deadlineDateBegin != null && deadlineDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));
                }
                if (deadlineDateEnd != null && deadlineDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));
                }
                if (deadlineDateBegin != null && deadlineDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("dueDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
                }

                if (type != null) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                if (statuses != null) {
                    predicates.add(criteriaBuilder.in(root.get("status")).value(statuses));
                }
                if (departmentId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId"), departmentId));
                }
                if (receiverId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("receiverId"), receiverId));
                }

                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    //statistics
    @Override
    public Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateGreaterThanEqual(receiverId, date);
    }

    @Override
    public Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateLessThanEqual(receiverId, date);
    }

    @Override
    public Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses){
        return documentTaskSubRepository.countByReceiverIdAndStatusIn(receiverId, statuses);
    }

    @Override
    public Integer countByReceiverId(Integer receiverId){
        return documentTaskSubRepository.countByReceiverId(receiverId);
    }

}
