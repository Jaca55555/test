package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 17.01.2020
 */

@Controller
public class DocTypeController {
    private final DocumentTypeService documentTypeService;
    private final UserService userService;

    @Autowired
    public DocTypeController(
            DocumentTypeService documentTypeService,
            UserService userService
    ) {
        this.documentTypeService = documentTypeService;
        this.userService = userService;
    }

    @RequestMapping(value = DocUrls.DocTypeList)
    public String getDocTypeList(Model model) {
        model.addAttribute("doc_type", DocumentTypeEnum.getList());
        return DocTemplates.DocTypeList;
    }

    @RequestMapping(value = DocUrls.DocTypeListAjax,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getDocTypeListAjax(
            Pageable pageable,
            @RequestParam(name = "type")DocumentTypeEnum typeEnum,
            @RequestParam(name = "name")String name,
            @RequestParam(name = "status")Boolean status
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentType> documentTypePage = documentTypeService.getFiltered(typeEnum, name, status, pageable);
        List<Object[]> JSONArray = new ArrayList<>(documentTypePage.getContent().size());
        for (DocumentType docType : documentTypePage.getContent()) {
            JSONArray.add(new Object[]{
                    docType.getId(),
                    docType.getType(),
                    docType.getName(),
                    docType.getStatus()
            });
        }
        result.put("recordsTotal", documentTypePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTypePage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.DocTypeNew)
    public String getNewDocType(Model model) {
        model.addAttribute("action_url", DocUrls.DocTypeNew);
        model.addAttribute("docType", new DocumentType());
        model.addAttribute("doc_type", DocumentTypeEnum.getList());
        return DocTemplates.DocTypeNew;
    }

    @RequestMapping(value = DocUrls.DocTypeNew, method = RequestMethod.POST)
    public String createNewDocType(DocumentType docType) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        docType.setCreatedById(user.getId());
        documentTypeService.create(docType);
        documentTypeService.updateStatusActive();
        return "redirect:" + DocUrls.DocTypeList;
    }

    @RequestMapping(value = DocUrls.DocTypeEdit)
    public String getEditDocType(
            Model model,
            @RequestParam(name = "id")Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        DocumentType docType = documentTypeService.getById(id);
        if (docType == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        model.addAttribute("action_url", DocUrls.DocTypeEdit);
        model.addAttribute("docType", docType);
        model.addAttribute("doc_type", DocumentTypeEnum.getList());

        return DocTemplates.DocTypeEdit;
    }

    @RequestMapping(value = DocUrls.DocTypeEdit, method = RequestMethod.POST)
    public String editDocType(
            @RequestParam(name = "id")String id,
            @RequestParam(name = "createDate")String date,
            DocumentType docType
    ) {
        docType.setCreatedAt(DateParser.TryParse(date, Common.uzbekistanDateFormat));
        docType.setCreatedById(userService.getCurrentUserFromContext().getId());
        documentTypeService.update(docType);
        documentTypeService.updateStatusActive();
        return "redirect:" + DocUrls.DocTypeList;
    }

    @RequestMapping(value = DocUrls.DocTypeView)
    public String getDocType(Model model, @RequestParam(name = "id")Integer id) {
        DocumentType documentType = documentTypeService.getById(id);
        if (documentType == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        model.addAttribute("docType", documentType);
        return DocTemplates.DocTypeView;
    }

    @RequestMapping(value = DocUrls.DocTypeDelete)
    public String deleteDocType(@RequestParam(name = "id")Integer id) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "";
        }
        DocumentType documentType = documentTypeService.getById(id);
        if (documentType == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        documentType.setDeleted(Boolean.TRUE);
        documentTypeService.update(documentType);
        return "redirect:" + DocUrls.DocTypeList;
    }
}
