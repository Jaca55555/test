package uz.maroqand.ecology.docmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentSubType;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.service.interfaces.CommunicationToolService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagement.service.interfaces.JournalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Controller
public class IncomeMailController {
    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public IncomeMailController(
            DocumentService documentService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            ObjectMapper objectMapper,
            UserService userService,
            JournalService journalService
    ) {
        this.documentService = documentService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.journalService = journalService;
    }

    @RequestMapping(DocUrls.IncomeMailList)
    public String getIncomeListPage(Model model) {
        model.addAttribute("doc_type", documentTypeService.getStatusActive());

        return DocTemplates.IncomeMailList;
    }

    @RequestMapping(value = DocUrls.IncomeMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomeList(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Document> documentPage = documentService.findFiltered(filterDTO, pageable);
        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getUpdateAt()!=null? Common.uzbekistanDateFormat.format(document.getUpdateAt()):"",
                    "docStatus",
                    "Resolution and parcipiants"
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.IncomeMailView)
    public String getIncomeMail(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        model.addAttribute("doc", document);
        return DocTemplates.IncomeMailView;
    }

    @RequestMapping(value = DocUrls.IncomeMailNew, method = RequestMethod.GET)
    public String newDocument(Model model) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        model.addAttribute("doc", new Document());
        model.addAttribute("action_url", DocUrls.IncomeMailNew);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());
        return DocTemplates.IncomeMailNew;
    }

    @RequestMapping(value = DocUrls.IncomeMailNew, method = RequestMethod.POST)
    public String createDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "documentSubType") DocumentSubType type,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document.getDocumentSub().setType(type);
        documentService.createDoc(document);
        return "redirect:" + DocUrls.IncomeMailList;
    }

    @RequestMapping(DocUrls.IncomeMailEdit)
    public String editDocument(@RequestParam(name = "id")Integer id, Model model) {
        String response = DocTemplates.IncomeMailEdit;
        Document document = documentService.getById(id);
        if (document == null) {
            response = "redirect:" + DocUrls.IncomeMailList;
        }
        model.addAttribute("action_url", DocUrls.IncomeMailEdit);
        model.addAttribute("doc", document);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());

        return response;
    }

    @RequestMapping(value = DocUrls.IncomeMailEdit, method = RequestMethod.POST)
    public String updateDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "documentSubType") DocumentSubType type,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.getDocumentSub().setType(type);
        document.setUpdateById(user.getId());
        documentService.update(document);
        return "redirect:" + DocUrls.IncomeMailList;
    }
}
