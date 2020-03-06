package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
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
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;

import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class LibraryCategoryController {
    private final LibraryCategoryService libraryCategoryService;
    private final UserService userService;
    @Autowired
    public LibraryCategoryController(
            LibraryCategoryService libraryCategoryService,
            UserService userService
    ) {
        this.libraryCategoryService = libraryCategoryService;
        this.userService = userService;
    }
    @RequestMapping(value = DocUrls.LibraryCategoryList)
    public String getLibraryCategoryList(Model model) {

        return DocTemplates.LibraryCategoryList;

    }
    @RequestMapping(value = DocUrls.LibraryCategoryListAjax,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getDocTypeListAjax(
            Pageable pageable,

            @RequestParam(name = "name")String name,
             @RequestParam(name = "parent")String parent_name

    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<LibraryCategory> LibraryCategoryPage = libraryCategoryService.getFiltered(name,parent_name, pageable);
        List<Object[]> JSONArray = new ArrayList<>(LibraryCategoryPage.getContent().size());
        for (LibraryCategory libraryCategory : LibraryCategoryPage.getContent()) {
            JSONArray.add(new Object[]{
                    libraryCategory.getId(),
                    libraryCategory.getName(),
                    libraryCategory.getParent()!=null ? libraryCategoryService.getById(libraryCategory.getParent()).getName():""
            });
        }

        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.LibraryCategoryNew)
    public String getNewLibraryCategory(Model model) {
        model.addAttribute("action_url", DocUrls.LibraryCategoryNew);
        model.addAttribute("libraryCategory", new LibraryCategory());
       model.addAttribute("librarycategory",libraryCategoryService.findAll());
        return DocTemplates.LibraryCategoryNew;
    }
    @RequestMapping(value = DocUrls.LibraryCategoryNew, method = RequestMethod.POST)
    public String createNewLibraryCategory(LibraryCategory libraryCategory) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        libraryCategory.setCreated_by_id(user.getId());

        libraryCategoryService.create(libraryCategory);

//        libraryCategoryService.updateStatusActive();
        return "redirect:" + DocUrls.LibraryCategoryList;
    }

    @RequestMapping(value = DocUrls.LibraryCategoryEdit)
    public String getEditLibraryCategory(
            Model model,
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.LibraryCategoryList;
        }

        LibraryCategory libraryCategory = libraryCategoryService.getById(id);
        if (libraryCategory == null) {
            return "redirect:" + DocUrls.LibraryCategoryList;
        }
        model.addAttribute("action_url", DocUrls.LibraryCategoryEdit);
        model.addAttribute("libraryCategory", libraryCategory);
        model.addAttribute("librarycategory",libraryCategoryService.findAll());

        return DocTemplates.LibraryCategoryEdit;
    }

    @RequestMapping(value = DocUrls.LibraryCategoryEdit, method = RequestMethod.POST)
    public String editLibraryCat(
            @RequestParam(name = "id")String id,
            @RequestParam(name = "createDate")String date,
            LibraryCategory libraryCategory
    ) {
        libraryCategory.setCreatedAt(DateParser.TryParse(date, Common.uzbekistanDateFormat));
        libraryCategory.setCreated_by_id(userService.getCurrentUserFromContext().getId());
        libraryCategoryService.update(libraryCategory);
       // libraryCategoryService.updateStatusActive();
        libraryCategoryService.updateByIdFromCache(libraryCategory.getId());

        return "redirect:" + DocUrls.LibraryCategoryList;
    }
    @RequestMapping(value = DocUrls.LibraryCategoryView)
    public String getLibraryCategory(Model model, @RequestParam(name = "id")Integer id) {
        LibraryCategory libraryCategory = libraryCategoryService.getById(id);
        if (libraryCategory == null) {
            return "redirect:" + DocUrls.LibraryCategoryList;
        }
        model.addAttribute("libraryCategory", libraryCategory);
        return DocTemplates.LibraryCategoryView;
    }
    @RequestMapping(value = DocUrls.LibraryCategoryDelete)
    public String deleteLibraryCategory(@RequestParam(name = "id")Integer id) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "";
        }

        LibraryCategory libraryCategory = libraryCategoryService.getById(id);
        if (libraryCategory == null) {
            return "redirect:" + DocUrls.LibraryCategoryList;
        }
        libraryCategory.setDeleted(Boolean.TRUE);
        libraryCategoryService.update(libraryCategory);
      //  libraryCategoryService.updateStatusActive();
        libraryCategoryService.updateByIdFromCache(libraryCategory.getId());
        return "redirect:" + DocUrls.LibraryCategoryList;
    }






}
