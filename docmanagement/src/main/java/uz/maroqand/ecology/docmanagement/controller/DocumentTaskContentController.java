package uz.maroqand.ecology.docmanagement.controller;

import javafx.concurrent.Task;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskContentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class DocumentTaskContentController
{
    private final DocumentTaskContentService taskContentService;
    private final UserService userService;

    public DocumentTaskContentController(DocumentTaskContentService taskContentService, UserService userService) {
        this.taskContentService = taskContentService;
        this.userService = userService;
    }

    @RequestMapping(value = DocUrls.DocTaskContentList,method = RequestMethod.GET)
    public String getTaskContents() {
        return DocTemplates.DocTaskContentList;
    }

    @RequestMapping(value = DocUrls.DocTaskContentListAjax,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getTaskContentAjax(
            @RequestParam(name = "content")String content,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> response = new HashMap<>();
        Page<DocumentTaskContent> page = taskContentService.getTaskContentFilterPage(content,user.getOrganizationId(), pageable);
        List<Object[]> JSONArray = new ArrayList<>(page.getContent().size());
        for (DocumentTaskContent taskContent : page.getContent()) {
            JSONArray.add(new Object[]{
                    taskContent.getId(),
                    taskContent.getNameTranslation(locale),
                    taskContent.getCreatedAt()!=null?Common.uzbekistanDateFormat.format(taskContent.getCreatedAt()):""
            });
        }
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", JSONArray);

        return response;
    }

    @GetMapping(DocUrls.DocTaskContentNew)
    public String getNewTaskContent(Model model) {
        model.addAttribute("action_url", DocUrls.DocTaskContentNew);
        model.addAttribute("taskContent", new DocumentTaskContent());

        return DocTemplates.DocTaskContentNew;
    }

    @PostMapping(DocUrls.DocTaskContentNew)
    public String createNewTaskContent(DocumentTaskContent taskContent) {
        taskContent.setCreatedAt(new Date());
        taskContent.setCreatedById(userService.getCurrentUserFromContext().getId());

        taskContentService.save(taskContent);

        return "redirect:" + DocUrls.DocTaskContentList;
    }

    @GetMapping(DocUrls.DocTaskContentEdit)
    public String getEditTaskContent(@RequestParam(name = "id")Integer id, Model model) {
        DocumentTaskContent taskContent = taskContentService.getById(id);
        if (taskContent == null) {
            return "redirect:" + DocUrls.DocTaskContentList;
        }
        model.addAttribute("action_url", DocUrls.DocTaskContentEdit);
        model.addAttribute("taskContent", taskContent);
        return DocTemplates.DocTaskContentEdit;
    }

    @PostMapping(DocUrls.DocTaskContentEdit)
    public String updateTaskContent(DocumentTaskContent taskContent) {
        taskContent.setCreatedById(userService.getCurrentUserFromContext().getId());
        taskContent.setCreatedAt(new Date());
        taskContentService.save(taskContent);
        return "redirect:" + DocUrls.DocTaskContentList;
    }

    @RequestMapping(value = DocUrls.DocTaskContentDelete)
    public String deleteTaskContent(@RequestParam(name = "id")Integer id) {

        DocumentTaskContent taskContent = taskContentService.getById(id);
        if (taskContent != null) {
            taskContentService.delete(taskContent);
        }
        return "redirect:" + DocUrls.DocTaskContentList;
    }
}

