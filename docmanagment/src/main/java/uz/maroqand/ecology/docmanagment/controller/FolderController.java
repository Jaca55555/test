package uz.maroqand.ecology.docmanagment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.commons.lang3.StringUtils;

import uz.maroqand.ecology.docmanagment.constant.DocumentTypeEnum;

import uz.maroqand.ecology.docmanagment.entity.Folder;
import uz.maroqand.ecology.docmanagment.service.interfaces.FolderService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagment.constant.DocTemplates;
import uz.maroqand.ecology.docmanagment.constant.DocUrls;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;


import lombok.Data;

@Data
class FolderForm {
    Integer id;
    String name;
    Integer docTypeId;
    Integer parentId;
}


@Controller
public class FolderController {

    private UserService userService;
    private FolderService folderService;

    @Autowired
    public FolderController(UserService userService, FolderService folderService){
        this.userService = userService;
        this.folderService = folderService;
    }


    @RequestMapping(DocUrls.FolderList)
    public String getFolderList(){
        return DocTemplates.FolderList;
    }

    @RequestMapping(DocUrls.FolderListAjax)
    @ResponseBody
    public HashMap<String, Object> getFolderListAjax(
            @RequestParam(name = "id", required = false)Integer id,
            @RequestParam(name = "name", required = false)String name,
            @RequestParam(name = "date_begin", required = false)String dateBegin,
            @RequestParam(name = "date_end", required = false)String dateEnd,
            Pageable pageable
    ){
        name = StringUtils.trimToNull(name);
        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);

        String[]date_begin;
        if(dateBegin != null) {
            date_begin = dateBegin.split("-");
            dateBegin = date_begin[2] + "." + date_begin[1] +"." + date_begin[0];
        }

        String[]date_end;
        if(dateEnd != null) {
            date_end = dateEnd.split("-");
            dateEnd = date_end[2] + "." + date_end[1] +"." + date_end[0];
        }

        Page<Folder> folderPage = folderService.findFiltered(id, name, dateBegin, dateEnd, pageable);



        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", folderPage.getTotalElements());
        result.put("recordsFiltered", folderPage.getTotalElements());

        List<Object[]> JSONArray = new ArrayList<>(folderPage.getContent().size());

        for(Folder folder: folderPage){
            JSONArray.add(new Object[]{folder.getId(), folder.getName(), Common.uzbekistanDateFormat.format(folder.getDate()), folder.getParentId() == null ? "none" : folder.getParent().getName()});
        }
        result.put("data", JSONArray);

        return result;
    }

    @RequestMapping(value = DocUrls.FolderNew, method = RequestMethod.GET)
    public String folderNew(Model model){

        model.addAttribute("folder", new FolderForm());
        model.addAttribute("folderList", folderService.getFolderList());
        return DocTemplates.FolderNew;
    }

    @RequestMapping(value = DocUrls.FolderNew, method = RequestMethod.POST)
    public String folderNewCreate(
           FolderForm folderForm
    ){
        Folder folder = new Folder();
        folder.setName(folderForm.getName());
        folder.setParentId(folderForm.getParentId());
        folder.setType(folderForm.getDocTypeId() == 1 ? DocumentTypeEnum.IncomingDocuments : DocumentTypeEnum.OutgoingDocuments);
        folder.setCreatedById(userService.getCurrentUserFromContext().getId());
        folder.setDate(new Date());
        folderService.create(folder, userService.getCurrentUserFromContext().getId());

        return "redirect:" + DocUrls.FolderList;
    }

    @RequestMapping(value = DocUrls.FolderEdit, method = RequestMethod.GET)
    public String folderEdit(@RequestParam(name = "id")Integer id, Model model){
        Folder folder = folderService.get(id);
        if(folder == null)
            return "redirect:" + DocUrls.FolderList;

        FolderForm form = new FolderForm();
        form.setId(folder.getId());
        form.setName(folder.getName());
        form.setDocTypeId(folder.getType() == DocumentTypeEnum.IncomingDocuments ? 1 : 2);
        form.setParentId(folder.getParentId());
        model.addAttribute("folder", form);

        model.addAttribute("folderList", folderService.getFolderList());


        return DocTemplates.FolderEdit;
    }
    @RequestMapping(value = DocUrls.FolderEdit, method = RequestMethod.POST)
    public String folderEditSubmit(FolderForm form){
        Folder folder = folderService.get(form.getId());
        if(folder == null)
            return "redirect:" + DocUrls.FolderList;
        folder.setName(form.getName());
        folder.setParentId(form.getParentId());
        folder.setType(form.getDocTypeId() == 1 ? DocumentTypeEnum.IncomingDocuments : DocumentTypeEnum.OutgoingDocuments);
        folderService.update(folder);
        return "redirect:" + DocUrls.FolderList;
    }
}
