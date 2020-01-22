package uz.maroqand.ecology.docmanagment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagment.constant.DocTemplates;
import uz.maroqand.ecology.docmanagment.constant.DocUrls;
import uz.maroqand.ecology.docmanagment.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Journal;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagment.service.interfaces.JournalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 21.01.2020
 */

@Controller
public class JournalController {
    private final JournalService journalService;
    private final DocumentTypeService documentTypeService;
    private final UserService userService;

    @Autowired
    public JournalController(
            JournalService journalService,
            DocumentTypeService documentTypeService,
            UserService userService
    ) {
        this.journalService = journalService;
        this.documentTypeService = documentTypeService;
        this.userService = userService;
    }

    @GetMapping(DocUrls.JournalList)
    public String getListPage(Model model) {
        model.addAttribute("docTypes", documentTypeService.getStatusActive());
        model.addAttribute("filterDTO", new JournalFilterDTO());
        return DocTemplates.JournalList;
    }

    @PostMapping(DocUrls.JournalListAjax)
    @ResponseBody
    public HashMap getListAjax(
            JournalFilterDTO filterDTO,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Journal> journalPage = journalService.getFiltered(filterDTO, pageable);
        List<Object[]> JSONArray = new ArrayList<>(journalPage.getContent().size());
        for (Journal journal : journalPage.getContent()) {
            JSONArray.add(new Object[]{
                    journal.getId(),
                    journal.getDocumentType().getName(),
                    journal.getName(),
                    journal.getPrefix(),
                    journal.getSuffix(),
                    journal.getNumbering(),
                    journal.getRestricted(),
                    journal.getStatus()
            });
        }
        result.put("recordsTotal", journalPage.getTotalElements());
        result.put("recordsFiltered", journalPage.getTotalElements());
        result.put("data", JSONArray);
        return result;
    }

    @GetMapping(DocUrls.JournalNew)
    public String getNewJournalPage(Model model) {
        model.addAttribute("docType", documentTypeService.getStatusActive());
        model.addAttribute("action_url", DocUrls.JournalNew);
        model.addAttribute("journal", new Journal());
        return DocTemplates.JournalNew;
    }

    @PostMapping(DocUrls.JournalNew)
    public String createJournal(Journal journal) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        journal.setCreatedById(user.getId());
        journalService.create(journal);
        return "redirect:" + DocUrls.JournalList;
    }
}
