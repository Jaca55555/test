package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentViewService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Chiquvchi xatlar
 */
@Controller
public class OutgoingController {

    private final DocumentService documentService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final DocumentViewService documentViewService;

    public OutgoingController(DocumentService documentService, UserService userService, DepartmentService departmentService, DocumentViewService documentViewService){
        this.documentService = documentService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.documentViewService = documentViewService;
    }


    @RequestMapping(DocUrls.OutgoingList)
    public String getOutgoingListPage(Model model) {
        User user = userService.getCurrentUserFromContext();

        Integer departmentId = user.getDepartmentId();
        Integer organizationId = user.getOrganizationId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();

        model.addAttribute("totalOutgoing", documentService.countAll(outgoingMailType, organizationId, departmentId));
        model.addAttribute("inProgress", documentService.countAllByStatus(outgoingMailType, DocumentStatus.InProgress, organizationId, departmentId));
        model.addAttribute("haveAdditionalDocument", documentService.countAllWhichHaveAdditionalDocuments(outgoingMailType, organizationId, departmentId));

        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("todayDocuments", documentService.countAllTodaySDocuments(outgoingMailType, organizationId,departmentId));

        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(DocUrls.OutgoingView)
    public String getOutgoingViewPage(Model model) {

        return DocTemplates.OutgoingView;
    }

}