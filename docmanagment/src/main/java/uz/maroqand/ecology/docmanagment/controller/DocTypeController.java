package uz.maroqand.ecology.docmanagment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
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
import uz.maroqand.ecology.docmanagment.constant.DocTemplates;
import uz.maroqand.ecology.docmanagment.constant.DocUrls;
import uz.maroqand.ecology.docmanagment.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagment.entity.DocumentType;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentTypeService;

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
    private final DocumentService documentService;
    private final DocumentTypeService documentTypeService;
    private final UserService userService;

    @Autowired
    public DocTypeController(
            DocumentService documentService,
            DocumentTypeService documentTypeService,
            UserService userService
    ) {
        this.documentService = documentService;
        this.documentTypeService = documentTypeService;
        this.userService = userService;
    }

    @RequestMapping(value = DocUrls.DocTypeList)
    public String getDocTypeList(Model model) {
        return DocTemplates.DocTypeList;
    }

    @RequestMapping(value = DocUrls.DocTypeListAjax,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTablesOutput<DocumentType> getDocTypeListAjax(DataTablesInput input) {
        return documentTypeService.getAll(input);
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
    public String deleteDocType(@RequestParam(name = "id")String id) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "";
        }
        return "redirect:" + DocUrls.DocTypeList;
    }
}
