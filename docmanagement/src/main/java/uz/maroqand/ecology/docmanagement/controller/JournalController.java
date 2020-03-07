package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Journal;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagement.service.interfaces.JournalService;

import javax.transaction.Transactional;
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
                    journal.getDocumentTypeId()!=null?journal.getDocumentType().getName():"",
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

    @RequestMapping(value = DocUrls.JournalNew,method = RequestMethod.POST)
    public String createJournal(Journal journal) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        journal.setCreatedById(user.getId());
        journalService.create(journal);
        journalService.updateStatusActive(journal.getDocumentTypeId());

        return "redirect:" + DocUrls.JournalList;
    }

    @GetMapping(DocUrls.JournalEdit)
    public String getEditJournalPage(
            Model model,
            @RequestParam(name = "id")Integer id
    ) {
        Journal journal = journalService.getById(id);
        if (journal == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        model.addAttribute("docType", documentTypeService.getStatusActive());
        model.addAttribute("action_url", DocUrls.JournalEdit);
        model.addAttribute("journal", journal);
        return DocTemplates.JournalEdit;
    }

    @PostMapping(DocUrls.JournalEdit)
    public String editJournal(
            Journal journal,
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "createDate")String date
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        journal.setCreatedAt(DateParser.TryParse(date, Common.uzbekistanDateFormat));
        journal.setCreatedById(user.getId());
        journalService.update(journal);
        journalService.updateStatusActive(journal.getDocumentTypeId());
        journalService.updateByIdFromCache(journal.getId());

        return "redirect:" + DocUrls.JournalList;
    }

    @GetMapping(DocUrls.JournalView)
    public String getJournal(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        Journal journal = journalService.getById(id);
        if (journal == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        model.addAttribute("journal", journal);
        return DocTemplates.JournalView;
    }
    @RequestMapping(value = DocUrls.JournalDelete)
    public String deleteJournal(@RequestParam(name = "id")Integer id) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "";
        }
        Journal journal = journalService.getById(id);
        if (journal == null) {
            return "redirect:" + DocUrls.JournalList;
        }
        journal.setDeleted(Boolean.TRUE);
        journalService.update(journal);
        journalService.updateStatusActive(journal.getDocumentTypeId());

        return "redirect:" + DocUrls.JournalList;
    }


}
