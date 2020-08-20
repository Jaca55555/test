package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.CommunicationTool;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentOrganizationService;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
public class DocumentOrganizationController {
    private DocumentOrganizationService documentOrganizationService;
    private UserService userService;
    Integer prevId;
    @Autowired
    public DocumentOrganizationController(DocumentOrganizationService documentOrganizationService, UserService userService){
        this.documentOrganizationService = documentOrganizationService;
        this.userService = userService;
    }

    @RequestMapping(DocUrls.DocumentOrganizationList)
    public String getDocumentOrganizationListPage(){
        return DocTemplates.DocumentOrganizationList;
    }

    @RequestMapping(value = DocUrls.DocumentOrganizationListAjax)
    @ResponseBody
    public HashMap<String, Object> getDocumentOrganizationListAjax(
        @RequestParam(name = "id")Integer id,
        @RequestParam(name = "name")String name,
        @RequestParam(name = "status")Integer status,
        Pageable pageable
    ){
        name = StringUtils.trimToNull(name);
        User user = userService.getCurrentUserFromContext();
        Page<DocumentOrganization> docOrganizationPage = documentOrganizationService.findFiltered(id,user.getOrganizationId(), name, status == 0 ? null : status, pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", docOrganizationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", docOrganizationPage.getTotalElements());

        List<Object[]> JSONArray = new ArrayList<>(docOrganizationPage.getContent().size());

        for(DocumentOrganization docOrganization: docOrganizationPage){
            JSONArray.add(new Object[]{docOrganization.getId(),
                    docOrganization.getName(),
                    docOrganization.getParent()!=null ? documentOrganizationService.getById(docOrganization.getParent()).getName():"",
                    docOrganization.getStatus()});
        }

        result.put("data", JSONArray);

        return result;
    }

    @RequestMapping(DocUrls.DocumentOrganizationNew)
    public String documentOrganizationNew(Model model){

        DocumentOrganization documentOrganization = new DocumentOrganization();
        model.addAttribute("docOrganization", documentOrganization);
        model.addAttribute("organizationGroup",documentOrganizationService.getLevel(1));
        return DocTemplates.DocumentOrganizationNew;
    }

    @RequestMapping(value = DocUrls.DocumentOrganizationNew, method = RequestMethod.POST)
    public String documentOrganizationNewCreate(@RequestParam(name = "name")String name, @RequestParam(name = "status", defaultValue = "false")Boolean status, @RequestParam(name = "parent", required = false)Integer parent){
        DocumentOrganization docOrganization = new DocumentOrganization();

        docOrganization.setName(name);
        docOrganization.setStatus(status);
        if(parent!=null){
            docOrganization.setParent(parent);
            docOrganization.setLevel(0);
        }else{
            docOrganization.setParent(null);
            docOrganization.setLevel(1);
        }
        docOrganization.setParent(parent);

        docOrganization.setCreatedById(userService.getCurrentUserFromContext().getId());
        documentOrganizationService.create(docOrganization);

        return "redirect:" + DocUrls.DocumentOrganizationList;
    }

    @RequestMapping(DocUrls.DocumentOrganizationEdit)
    public String documentOrganizationEdit(@RequestParam(name = "id")Integer id, Model model) {
        DocumentOrganization docOrganization = documentOrganizationService.getById(id);
        prevId = id;
        if(docOrganization == null)
            return "redirect:" + DocUrls.DocumentOrganizationList;

        model.addAttribute("docOrganization", docOrganization);
        model.addAttribute("organizationGroup",documentOrganizationService.getLevel(1));
        return DocTemplates.DocumentOrganizationNew;
    }

    @RequestMapping(value = DocUrls.DocumentOrganizationEdit, method = RequestMethod.POST)
    public String documentOrganizationEditSubmit(DocumentOrganization docOrganization){

        if(prevId != docOrganization.getId()) {
            System.out.println("attempt to write in hidden input!");
            return "redirect:" + DocUrls.DocumentOrganizationList;
        }

        DocumentOrganization updatedDocOrganization = documentOrganizationService.getById(docOrganization.getId());

        if(docOrganization == null)
            return "redirect:" + DocUrls.DocumentOrganizationList;

        updatedDocOrganization.setName(docOrganization.getName());
        updatedDocOrganization.setStatus(docOrganization.getStatus());
        updatedDocOrganization.setParent(docOrganization.getParent());
        documentOrganizationService.update(updatedDocOrganization);

        return "redirect:" + DocUrls.DocumentOrganizationList;
    }

    @RequestMapping(value = DocUrls.DocumentOrganizationEditStatus, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String editStatus(@RequestParam(name = "id")Integer id){
        DocumentOrganization docOrganization =  documentOrganizationService.getById(id);

        if(docOrganization != null){
            docOrganization.setStatus(!docOrganization.getStatus());
            documentOrganizationService.update(docOrganization);
            return "success";
        }
        else return "false";
    }



}


