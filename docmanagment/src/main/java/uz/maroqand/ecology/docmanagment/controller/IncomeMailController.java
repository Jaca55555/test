package uz.maroqand.ecology.docmanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagment.constant.DocTemplates;
import uz.maroqand.ecology.docmanagment.constant.DocUrls;
import uz.maroqand.ecology.docmanagment.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Document;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentService;

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
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public IncomeMailController(DocumentService documentService, ObjectMapper objectMapper, UserService userService) {
        this.documentService = documentService;
        this.objectMapper = objectMapper;
        this.userService = userService;
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
}
