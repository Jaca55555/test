package uz.maroqand.ecology.docmanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagment.constant.DocTemplates;
import uz.maroqand.ecology.docmanagment.constant.DocUrls;
import uz.maroqand.ecology.docmanagment.constant.DocumentSubType;
import uz.maroqand.ecology.docmanagment.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Document;
import uz.maroqand.ecology.docmanagment.service.interfaces.CommunicationToolService;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagment.service.interfaces.JournalService;

import java.util.ArrayList;
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
        return DocTemplates.IncomeMailList;
    }

    @RequestMapping(DocUrls.IncomeMailListAjax)
    @ResponseBody
    public HashMap<String, Object> getIncomeList(Pageable pageable) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Document> documentPage = documentService.findFiltered(new DocFilterDTO(), pageable);
        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate(),
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

    @RequestMapping(DocUrls.IncomeMailNew)
    public String newDocument(Model model) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        model.addAttribute("doc", new Document());
        model.addAttribute("action_url", DocUrls.IncomeMailCreate);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());
        return DocTemplates.IncomeMailNew;
    }

    @RequestMapping(value = DocUrls.IncomeMailCreate, method = RequestMethod.POST)
    public String createDoc(Document document) {
        documentService.createDoc(document);
        return "redirect: " + DocUrls.IncomeMailList;
    }

    @RequestMapping(DocUrls.IncomeMailEdit)
    public String editDocument(@RequestParam(name = "id")Integer id, Model model) {
        User user = userService.getCurrentUserFromContext();
        String response = DocTemplates.IncomeMailEdit;
        if (user == null) {
            response = "redirect: " + DocUrls.IncomeMailList;
        }
        Document document = documentService.getById(id);
        if (document == null) {
            response = "redirect: " + DocUrls.IncomeMailList;
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
    public String updateDoc(Document document) {
        return "redirect: " + DocUrls.IncomeMailList;
    }
}
