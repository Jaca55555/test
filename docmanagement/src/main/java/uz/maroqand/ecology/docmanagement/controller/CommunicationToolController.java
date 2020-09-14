package uz.maroqand.ecology.docmanagement.controller;


import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.CommunicationTool;
import uz.maroqand.ecology.docmanagement.service.interfaces.CommunicationToolService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class CommunicationToolController {

    private final UserService userService;
    private final CommunicationToolService communicationToolService;
    private Integer prevId;

    @Autowired
    public CommunicationToolController(UserService userService, CommunicationToolService communicationToolService) {
        this.userService = userService;
        this.communicationToolService = communicationToolService;
    }

    @RequestMapping(DocUrls.CommunicationToolsList)
    public String communicationToolsList(){
        return DocTemplates.CommunicationToolsList;
    }

    @RequestMapping(value = DocUrls.CommunicationToolsListAjax, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> communicationToolsListAjax(
            @RequestParam(name="id", required = false)Integer id,
            @RequestParam(name="name", required = false)String name,
            Pageable pageable
    )
    {
        name = StringUtils.trimToNull(name);
        User user= userService.getCurrentUserFromContext();
        Page<CommunicationTool> toolsPage = communicationToolService.findFiltered(id, name,user.getOrganizationId(), pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", toolsPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", toolsPage.getTotalElements());

        List<Object[]> JSONArray = new ArrayList<>(toolsPage.getContent().size());

        for(CommunicationTool tool: toolsPage){
            JSONArray.add(new Object[]{tool.getId(), tool.getName(), tool.getStatus()});
        }
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.CommunicationToolsNew, method = RequestMethod.GET)
    public String communicationToolsNew(Model model){

        CommunicationTool communicationTool = new CommunicationTool();
        model.addAttribute("tool", communicationTool);

        return DocTemplates.CommunicationToolsNew;
    }

    @RequestMapping(value = DocUrls.CommunicationToolsNew, method = RequestMethod.POST)
    public String communicationToolsNewCreate(@RequestParam(name = "name")String name, @RequestParam(name = "status", defaultValue = "false")Boolean status){
        CommunicationTool tool = new CommunicationTool();
        tool.setName(name);
        tool.setStatus(status);
        tool.setCreatedById(userService.getCurrentUserFromContext().getId());
        communicationToolService.create(tool);
        communicationToolService.updateStatusActive();
        return "redirect:" + DocUrls.CommunicationToolsList;
    }

    @RequestMapping(value = DocUrls.CommunicationToolsEdit, method = RequestMethod.GET)
    public String communicationToolsEdit(@RequestParam(name = "id")Integer id, Model model) {
        CommunicationTool tool = communicationToolService.getById(id);
        prevId = id;
        if(tool == null)
            return "redirect:" + DocUrls.CommunicationToolsList;

        model.addAttribute("tool", tool);

        return DocTemplates.CommunicationToolsEdit;
    }

    @RequestMapping(value = DocUrls.CommunicationToolsEdit, method = RequestMethod.POST)
    public String communicationToolsEditSubmit(CommunicationTool tool){
        if(prevId != tool.getId()) {
            System.out.println("attempt to write in hidden input!");
            return "redirect:" + DocUrls.CommunicationToolsList;
        }
        CommunicationTool updatedTool = communicationToolService.getById(tool.getId());

        if(updatedTool == null)
            return "redirect:" + DocUrls.CommunicationToolsList;

        updatedTool.setName(tool.getName());
        updatedTool.setStatus(tool.getStatus());
        communicationToolService.update(updatedTool);
        communicationToolService.updateStatusActive();
        communicationToolService.updateCacheableById(updatedTool.getId());
        return "redirect:" + DocUrls.CommunicationToolsList;
    }
    @RequestMapping(value = DocUrls.CommunicationToolsEditStatus, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String editStatus(@RequestParam(name = "id")Integer id){
        CommunicationTool tool = communicationToolService.getById(id);

        if(tool != null){
            tool.setStatus(!tool.getStatus());
            communicationToolService.update(tool);
            communicationToolService.updateStatusActive();
            return "success";
        }
        else return "false";
    }

}
