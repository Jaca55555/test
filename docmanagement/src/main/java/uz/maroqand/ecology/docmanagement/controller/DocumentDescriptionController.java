package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentDescriptionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 20.02.2020
 */

@Controller
public class DocumentDescriptionController
{
    private final DocumentDescriptionService descriptionService;
    private final UserService userService;

    public DocumentDescriptionController(DocumentDescriptionService descriptionService, UserService userService) {
        this.descriptionService = descriptionService;
        this.userService = userService;
    }

    @GetMapping(DocUrls.DocDescriptionList)
    public String getDescriptions() {

        return DocTemplates.DocDescriptionList;
    }

    @PostMapping(DocUrls.DocDescriptionListAjax)
    @ResponseBody
    public HashMap<String, Object> getDescriptionAjax(
            @RequestParam(name = "content")String content,
            Pageable pageable
    ) {
        HashMap<String, Object> response = new HashMap<>();
        Page<DocumentDescription> page = descriptionService.getDescriptionFilterPage(content, pageable);
        List<Object[]> JSONArray = new ArrayList<>(page.getContent().size());
        for (DocumentDescription description : page.getContent()) {
            JSONArray.add(new Object[]{
                    description.getId(),
                    description.getContent(),
                    Common.uzbekistanDateFormat.format(description.getCreatedAt())
            });
        }
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", JSONArray);

        return response;
    }

    @GetMapping(DocUrls.DocDescriptionNew)
    public String getNewDescription(Model model) {
        model.addAttribute("action_url", DocUrls.DocDescriptionNew);
        model.addAttribute("description", new DocumentDescription());

        return DocTemplates.DocDescriptionNew;
    }

    @PostMapping(DocUrls.DocDescriptionNew)
    public String createNewDescription(DocumentDescription description) {
        description.setCreatedAt(new Date());
        description.setCreatedById(userService.getCurrentUserFromContext().getId());

        descriptionService.save(description);

        return "redirect:" + DocUrls.DocDescriptionList;
    }

    @GetMapping(DocUrls.DocDescriptionEdit)
    public String getEditDescription(@RequestParam(name = "id")Integer id, Model model) {
        DocumentDescription description = descriptionService.getById(id);
        if (description == null) {
            return "redirect:" + DocUrls.DocDescriptionList;
        }
        model.addAttribute("action_url", DocUrls.DocDescriptionEdit);
        model.addAttribute("description", description);
        return DocTemplates.DocDescriptionEdit;
    }

    @PostMapping(DocUrls.DocDescriptionEdit)
    public String updateDescription(DocumentDescription description, @RequestParam(name = "createDate")String date) {
        description.setCreatedById(userService.getCurrentUserFromContext().getId());
        description.setCreatedAt(DateParser.TryParse(date, Common.uzbekistanDateFormat));
        descriptionService.save(description);
        return "redirect:" + DocUrls.DocDescriptionList;
    }

   @RequestMapping(value = DocUrls.DocDescriptionDelete)
    public String deleteDescription(@RequestParam(name = "id")Integer id) {

        DocumentDescription description = descriptionService.getById(id);
        if (description != null) {
           descriptionService.delete(description);
       }
       return "redirect:" + DocUrls.DocDescriptionList;
    }
}
