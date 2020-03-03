package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.Library;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;
import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryCategoryService;
import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryService;

import java.util.*;

@Controller
public class LibraryController {
    private final LibraryCategoryService libraryCategoryService;
    private final UserService userService;
    private final LibraryService libraryService;
    private final FileService fileService;

    @Autowired
    public LibraryController(
            LibraryService libraryService,
            LibraryCategoryService libraryCategoryService,
            UserService userService,
            FileService fileService

    ) {
        this.libraryCategoryService = libraryCategoryService;
        this.userService = userService;
        this.libraryService=libraryService;
        this.fileService=fileService;
    }
//    All Categories
    @RequestMapping(value = DocUrls.LibraryWindow)
    public String getCategoryList(Model model,Integer id) {
        model.addAttribute("categories",libraryCategoryService.findAll());
        model.addAttribute("subcategories",libraryCategoryService.findAll());
//        model.addAttribute("asd",libraryCategoryService.updateByIdFromCache(id));
        return DocTemplates.LibraryWindow;
    }
//      Lists
    @RequestMapping(value = DocUrls.LibraryList)
    public String getLibraryList(Model model,@RequestParam(name = "id")Integer id) {
        model.addAttribute("categoryId",id);
        model.addAttribute("cat",libraryCategoryService.getById(id));
        return DocTemplates.LibraryList;
    }
    @RequestMapping(value = DocUrls.LibraryListAjax,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getLibraryListAjax(
            Pageable pageable,
            @RequestParam(name = "name")String name,
            @RequestParam(name = "id")Integer categoryId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Library> LibraryPage = libraryService.getFiltered(name,categoryId, pageable);
        List<Object[]> JSONArray = new ArrayList<>(LibraryPage.getContent().size());
        for (Library library : LibraryPage.getContent()) {
            JSONArray.add(new Object[]{
                    library.getId(),
                    library.getName(),
                    library.getNumber(),
                    library.getLdate().toString().substring(0,11)
            });
        }

        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.LibraryNew)
    public String getNewLibrary(Model model) {
        model.addAttribute("action_url", DocUrls.LibraryNew);
        model.addAttribute("library", new Library());
        model.addAttribute("librarycategory",libraryCategoryService.findAll());
        return DocTemplates.LibraryNew;
    }
    @RequestMapping(value = DocUrls.LibraryNew, method = RequestMethod.POST)
    public String createNewLibrary(
            Library library,
            @RequestParam(name = "file_ids")List<Integer> file_ids,
            @RequestParam(name = "ldateStr") String ldateStr

    ) {
        Date ldate = DateParser.TryParse(ldateStr, Common.uzbekistanDateFormat);
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.DocTypeList;
        }
        Set<File> files = new HashSet<File>();
        for(Integer id: file_ids) {
            if (id != null) files.add(fileService.findById(id));
        }
        library.setContentFiles(files);
        library.setCreated_by_id(user.getId());
        library.setLdate(ldate);
        libraryService.create(library);
        return "redirect:" + DocUrls.LibraryList;
    }
    @RequestMapping(value = DocUrls.LibraryView)
    public String getLibrary(Model model, @RequestParam(name = "id")Integer id) {
        Library library = libraryService.getById(id);
        if (library == null) {
            return "redirect:" + DocUrls.LibraryList;
        }
        model.addAttribute("library", library);
        model.addAttribute("files", library.getContentFiles());
        return DocTemplates.LibraryView;
    }
//    EditList
    @RequestMapping(value = DocUrls.LibraryEdit)
    public String getEditLibraryCategory(
            Model model,
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.LibraryList;
        }

        Library library = libraryService.getById(id);
        if (library == null) {
            return "redirect:" + DocUrls.LibraryList;
        }
        model.addAttribute("action_url", DocUrls.LibraryEdit);
        model.addAttribute("library", library);
        model.addAttribute("librarycategory",libraryCategoryService.findAll());
        return DocTemplates.LibraryEdit;
    }
    @RequestMapping(value = DocUrls.LibraryEdit, method = RequestMethod.POST)
    public String editLibrary(
            @RequestParam(name = "id")String id,
            @RequestParam(name = "createDate")String date,
            @RequestParam(name = "ldateStr") String ldateStr,
            @RequestParam(name = "file_ids")List<Integer> file_ids,
            Library library
    ) {
        library.setCreatedAt(DateParser.TryParse(date, Common.uzbekistanDateFormat));
        library.setCreated_by_id(userService.getCurrentUserFromContext().getId());
        Date ldate = DateParser.TryParse(ldateStr, Common.uzbekistanDateFormat);
        library.setLdate(ldate);

        Set<File> files = new HashSet<>();
        for(Integer id1: file_ids) {
            if (id1 != null) files.add(fileService.findById(id1));
        }
        library.setContentFiles(files);
        libraryService.update(library);
        libraryService.updateByIdFromCache(library.getId());
        return "redirect:" + DocUrls.LibraryList;
    }
    //Upload Files
    @RequestMapping(value = DocUrls.LibraryFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, File> uploadFile(@RequestParam(name = "file")MultipartFile file){

        File file_ = fileService.uploadFile(file, userService.getCurrentUserFromContext().getId(), file.getOriginalFilename(), file.getContentType());
        HashMap<String, File> res = new HashMap<>();
        res.put("data", file_);
        return res;
    }
    //Download Files
    @RequestMapping(value = DocUrls.LibraryFileDownload, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Resource> downloadAttachedDocument(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        if(file == null)
            return ResponseEntity.badRequest().body(null);
        else
            return fileService.getFileAsResourceForDownloading(file);
    }
//    FileDelete
    @RequestMapping(value = DocUrls.LibraryFileDelete, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public File deleteAttachment(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        file.setDeleted(true);
        file.setDateDeleted(new Date());
        file.setDeletedById(userService.getCurrentUserFromContext().getId());
        return fileService.save(file);
    }
//  LibraryDelete
    @RequestMapping(value = DocUrls.LibraryDelete)
    public String deleteLibrary(@RequestParam(name = "id")Integer id) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "";
        }

        Library library = libraryService.getById(id);
        if (library == null) {
            return "redirect:" + DocUrls.LibraryList;
        }
        library.setDeleted(Boolean.TRUE);
        libraryService.update(library);
        libraryService.updateByIdFromCache(library.getId());
        return "redirect:" + DocUrls.LibraryList;
    }


}
