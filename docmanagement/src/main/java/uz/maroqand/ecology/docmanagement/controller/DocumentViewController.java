package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.entity.DocumentView;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentViewService;

@Controller
public class DocumentViewController {

    private final DocumentViewService documentViewService;
    private final UserService userService;
    private final DocumentTypeService documentTypeService;
    int prevId;
    @Autowired
    public DocumentViewController(DocumentViewService documentViewService, UserService userService,DocumentTypeService documentTypeService){
        this.documentViewService = documentViewService;
        this.userService = userService;
        this.documentTypeService=documentTypeService;
    }

    @RequestMapping(DocUrls.DocumentViewList)
    public String docTypeList(){
        return DocTemplates.DocumentViewList;
    }

    @RequestMapping(DocUrls.DocumentViewListAjax)
    @ResponseBody
    public HashMap<String, Object> docTypeListAjax(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) Integer status,
            Pageable pageable
    ){
        name = StringUtils.trimToNull(name);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();
        System.out.println("name: " + name + ", status: " + status + ", locale: " + locale);
        Page<DocumentView> docViewPage = documentViewService.findFiltered(name,user.getOrganizationId(), status == 0 ? null : status, locale, pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", docViewPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", docViewPage.getTotalElements());

        List<Object[]> JSONArray = new ArrayList<>(docViewPage.getContent().size());

        for(DocumentView documentView: docViewPage){
            JSONArray.add(new Object[]{
                    documentView.getId(),
                    documentView.getName(),
                    documentView.getStatus(),
                    documentView.getType()!=null ? documentView.getType():" ",
                    Common.uzbekistanDateFormat.format(documentView.getCreatedAt())});
        }
        result.put("data", JSONArray);

        return result;
    }

    @RequestMapping(value = DocUrls.DocumentViewNew, method = RequestMethod.GET)
    public String documentViewNew(Model model){

        DocumentView docView = new DocumentView();
        model.addAttribute("action_url", DocUrls.DocumentViewNew);
        model.addAttribute("docView", docView);
        model.addAttribute("doc_type", DocumentTypeEnum.getList());
        return DocTemplates.DocumentViewNew;
    }

    @RequestMapping(value = DocUrls.DocumentViewNew, method = RequestMethod.POST)
    public String documentViewNewCreate(@RequestParam(name = "name")String name, @RequestParam(name = "status", defaultValue = "false")Boolean status,@RequestParam(name = "type")String type){
        DocumentView docView = new DocumentView();
        docView.setType(type);
        docView.setName(name);
        docView.setStatus(status);
        docView.setCreatedById(userService.getCurrentUserFromContext().getId());
        documentViewService.create(docView);

        return "redirect:" + DocUrls.DocumentViewList;
    }


    @RequestMapping(value = DocUrls.DocumentViewEdit, method = RequestMethod.GET)
    public String documentViewEdit(@RequestParam(name = "id")Integer id, Model model) {
        DocumentView docView = documentViewService.getById(id);
        prevId = id;
        if(docView == null)
            return "redirect:" + DocUrls.DocumentViewList;
        model.addAttribute("action_url", DocUrls.DocumentViewEdit);
        model.addAttribute("docView", docView);
        model.addAttribute("doc_type", DocumentTypeEnum.getList());
        return DocTemplates.DocumentViewNew;
    }


    @RequestMapping(value = DocUrls.DocumentViewEdit, method = RequestMethod.POST)
    public String documentViewEditSubmit(DocumentView docView){
        if(prevId != docView.getId()) {
            System.out.println("attempt to write in hidden input!");
            return "redirect:" + DocUrls.DocumentViewList;
        }
        DocumentView updatedView = documentViewService.getById(docView.getId());

        if(updatedView == null)
            return "redirect:" + DocUrls.DocumentViewList;

        updatedView.setName(docView.getName());
        updatedView.setStatus(docView.getStatus());
        updatedView.setType(docView.getType());
        documentViewService.update(updatedView);
        return "redirect:" + DocUrls.DocumentViewList;
    }

    @RequestMapping(value = DocUrls.DocumentViewEditStatus, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String editStatus(@RequestParam(name = "id")Integer id){
        DocumentView docView = documentViewService.getById(id);
        if(docView != null){
            docView.setStatus(!docView.getStatus());
            documentViewService.update(docView);
            return "success";
        }
        return "false";
    }


}
