package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;

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


    public OutgoingController(DocumentService documentService, UserService userService){
        this.documentService = documentService;
        this.userService = userService;
    }


    @RequestMapping(DocUrls.OutgoingList)
    public String getOutgoingListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        Integer departmentId = user.getDepartmentId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();
        model.addAttribute("totalOutgoing", documentService.countTotalByDocumentTypeAndDepartmentId(outgoingMailType, departmentId));
        model.addAttribute("inProgress", documentService.countTotalByTypeAndStatusAndDepartmentId(outgoingMailType, DocumentStatus.InProgress, departmentId));
        model.addAttribute("haveAdditionalDocument", documentService.countAllByDocumentTypeAndHasAdditionalDocumentAndDepartmentId(outgoingMailType, departmentId));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        System.out.println("Updated Date = " + calendar.getTime());
        model.addAttribute("todayDocuments", documentService.countAllByCreatedAtAfterAndDocumentTypeIdAndDepartmentId(calendar.getTime(), outgoingMailType, departmentId));

        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(DocUrls.OutgoingView)
    public String getOutgoingViewPage(Model model) {

        return DocTemplates.OutgoingView;
    }

}