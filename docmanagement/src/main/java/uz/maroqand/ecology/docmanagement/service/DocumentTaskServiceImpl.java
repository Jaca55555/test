package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Service
public class DocumentTaskServiceImpl implements DocumentTaskService{

    private final DocumentTaskRepository taskRepository;
    private final DocumentService documentService;
    private final HelperService helperService;
    private final DocumentTaskSubService taskSubService;

    @Autowired
    public DocumentTaskServiceImpl(DocumentTaskRepository taskRepository, DocumentService documentService, HelperService helperService, DocumentTaskSubService taskSubService) {
        this.taskRepository = taskRepository;
        this.documentService = documentService;
        this.helperService = helperService;
        this.taskSubService = taskSubService;
    }

    @Override
    public DocumentTask getById(Integer id) {
        Optional<DocumentTask> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    @Override
    public DocumentTask getByIdAndDocumentId(Integer id, Integer docId) {
        return taskRepository.findByIdAndDocumentIdAndDeletedFalse(id,docId);
    }

    @Override
    public List<DocumentTask> getByDocumetId(Integer docId) {
        return taskRepository.findByDocumentId(docId);
    }

    @Override
    public List<DocumentTask> getByStatusNotInactive() {
        return null;
    }

    @Override
    public DocumentTask create(DocumentTask task) {
        return taskRepository.save(task);
    }

    public Integer countNew() {
        Set<Integer> status = new HashSet<>();
        status.add(TaskStatus.Initial.getId());
        status.add(TaskStatus.New.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countInProcess() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.InProgress.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countNearDate() {
        Calendar calendar = Calendar.getInstance();
        Date begin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        Date end = calendar.getTime();
        return taskRepository.countByDueDateBetweenAndStatusNotAndDeletedFalse(begin, end, TaskStatus.Complete.getId());
    }

    public Integer countExpired() {
        return taskRepository.countByDueDateBeforeAndStatusNotAndDeletedFalse(new Date(), TaskStatus.Complete.getId());
    }

    public Integer countExecuted() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.Complete.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countTotal() {
        return taskRepository.countByDeletedFalse();
    }

    @Override
    public Integer getCountTaskStatus(Set<Integer> statusSet) {
        return taskRepository.countByStatusInAndDeletedFalse(statusSet);
    }

    @Override
    public DocumentTask createNewTask(Document doc, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById) {
        DocumentTask documentTask = new DocumentTask();
        documentTask.setDocumentId(doc.getId());
        documentTask.setStatus(status);
        documentTask.setContent(context.trim());
        documentTask.setDueDate(dueDate);
        documentTask.setChiefId(chiefId);
        documentTask.setCreatedAt(new Date());
        documentTask.setCreatedById(createdById);
        documentTask.setDeleted(Boolean.FALSE);

        if (doc.getStatus().equals(DocumentStatus.New)){
            doc.setStatus(DocumentStatus.InProgress);
            documentService.update(doc);
        }
        return taskRepository.save(documentTask);
    }

    @Override
    public DocumentTask update(DocumentTask task) {
        return taskRepository.save(task);
    }

    public DocumentTask getTaskByUser(Integer docId, Integer userId) {
        return taskRepository.findByDocumentIdAndChiefId(docId, userId);
    }

    @Override
    public String getTreeByDocumentId(List<DocumentTask> documentTasks) {
        String tree = "";
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask: documentTasks){
            String getReceiverName =  helperService.getUserFullNameById(documentTask.getChiefId()) +" ("+ helperService.getPositionName(documentTask.getChief().getPositionId(),locale)+")";
            tree+= "{ text :\"" + getReceiverName + " \",tags:" + documentTask.getId();
            List<DocumentTaskSub> taskSubs = taskSubService.getListByDocIdAndTaskId(documentTask.getDocumentId(),documentTask.getId());
            if (taskSubs.size()>0){
                tree+=" , nodes:" + taskSubService.buildTree(1,taskSubs,new HashMap<>(),locale) + "},";
            }else{
                tree+="},";
            }
        }
        return tree;
    }

    @Override
    public Page<DocumentTask> findFiltered(
            Integer organizationId,
            Integer documentTypeId,
            IncomingRegFilter incomingRegFilter,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Boolean specialControll,
            Pageable pageable
    ) {
        return taskRepository.findAll(getSpesification(organizationId, documentTypeId, incomingRegFilter, deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId,specialControll), pageable);
    }

    //taskOrsubTask==true  task
    //taskOrsubTask==false  taskSub
    @Override
    public List<String> getDueColor(Date date, boolean taskOrTaskSub, Integer statusId,String locale) {
        List<String> result = new ArrayList<>();
        if (date == null || statusId == null){
            result.add("");
            result.add("");
            return result;
        }
        Date nowDate = new Date();
        String colorText="";
        String dueText="";
        date.setHours(23);
        date.setMinutes(59);
        Long intervalHours = (date.getTime()-nowDate.getTime())/3600000;
        if (statusId==1 || statusId==0){ colorText+="text-primary" ;}
        if (statusId==2){ colorText+="text-primary " ;}
        if ((taskOrTaskSub && statusId == 3) || (!taskOrTaskSub && statusId == 5)) {
            colorText+="text-success ";
        }
        if ((taskOrTaskSub && statusId == 4) || (!taskOrTaskSub && statusId == 6)) {
            colorText+="text-secondary ";
        }
        if ((taskOrTaskSub && (statusId != 3 && statusId !=4)) || (!taskOrTaskSub && (statusId != 5 && statusId != 6))) {
            List<String> getName = getColorAndText(intervalHours,locale);
            colorText += getName.get(0);
            dueText = getName.get(1);
        }

        result.add(colorText);
        result.add(dueText);
        return result;
    }

    @Override
    public String getDueTranslateNameOrColor(Integer id, boolean taskOrsubTask,String nameOrColor, String locale) {
        if (id==null){
            if (nameOrColor.equals("name")){
                return "";
            }else{
                return "danger";
            }
        }
        Date dueDate;
        Date nowDate = new Date();
        if (taskOrsubTask){
            DocumentTask task = getById(id);
            if (task==null || task.getDueDate()==null){
                if (nameOrColor.equals("name")){
                    return "";
                }else{
                    return "danger";
                }
            }
            dueDate=task.getDueDate();
        }else{
            DocumentTaskSub taskSub = taskSubService.getById(id);
            if (taskSub==null || taskSub.getDueDate()==null){
                if (nameOrColor.equals("name")){
                    return "";
                }else{
                    return "danger";
                }
            }
            dueDate=taskSub.getDueDate();
        }

        Long interval = (dueDate.getTime()-nowDate.getTime())/3600000;
        System.out.println("dueDate= " + Common.uzbekistanDateFormat.format(dueDate));
        System.out.println("nowDate= " + Common.uzbekistanDateFormat.format(nowDate));

        if (interval<=0){
            if (nameOrColor.equals("name")){
                return helperService.getTranslation("sys_due.passed",locale);
            }else{
                return "danger";
            }
        }else if (interval<=72){
            if (nameOrColor.equals("name")){
                return helperService.getTranslation("sys_due.left",locale);
            }else{
                return "warning";
            }
        }

        if (nameOrColor.equals("name")){
            return "";
        }else{
            return "danger";
        }
    }

    private static Specification<DocumentTask> getSpesification(
            final Integer organizationId,
            final Integer documentTypeId,

            final IncomingRegFilter incomingRegFilter,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId,
            final Boolean specialControll
    ) {
        return (Specification<DocumentTask>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            System.out.println(incomingRegFilter.getRegistrationNumber() + "  -");
            if (organizationId != null) {
                //tashkilotga tegishli xatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentTypeId != null) {
                //kiruvchi, chiquvchi, ichki hujjatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
            }


            if (incomingRegFilter.getDocRegNumber() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + incomingRegFilter.getDocRegNumber().toLowerCase() + "%"));
            }
            if (incomingRegFilter.getRegistrationNumber() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + incomingRegFilter.getRegistrationNumber().toLowerCase() + "%"));
            }

            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin()));
            }
            if (incomingRegFilter.getDateEnd() != null && incomingRegFilter.getDateBegin() == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateEnd()));
            }
            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin(), incomingRegFilter.getDateEnd()));
            }
            if (incomingRegFilter.getContent() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + incomingRegFilter.getContent().toLowerCase() + "%"));
            }
            if (incomingRegFilter.getTaskContent() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + incomingRegFilter.getTaskContent() + "%"));
            }
            if (incomingRegFilter.getPerformerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), incomingRegFilter.getPerformerId()));
            }
            if (incomingRegFilter.getInsidePurpose() != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("insidePurpose"), incomingRegFilter.getInsidePurpose()));
            }
            /*if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }*/


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

            if (specialControll != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("specialControll"),specialControll));
            }

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> getColorAndText(Long intervalHours,String locale){
        Integer intervalDate = (int) Math.abs(intervalHours/24);
        String colorText="";
        String dueText="";
        String soatName = helperService.getTranslation("sys_hours",locale);
        String kunName = helperService.getTranslation("sys_day",locale);
        List<String> result = new ArrayList<>();

        if (intervalHours<48){
            colorText+=" text-danger ";
            if (intervalHours<0){
                dueText= helperService.getTranslation("due_text.passed",locale)+ ": ";
                dueText+=intervalDate!=0?Math.abs(intervalDate)+" "+kunName:Math.abs(intervalHours)+" "+soatName;
            }else{
                dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
                dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
            }
        }else if (intervalHours <=72){
            dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
            dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
            colorText+=" text-warning ";
        }

        if (intervalHours>72 && intervalHours<=96){
            dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
            dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
        }
        result.add(colorText);
        result.add(dueText);

        return result;
    }
}
