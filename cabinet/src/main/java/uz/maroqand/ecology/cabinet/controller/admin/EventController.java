package uz.maroqand.ecology.cabinet.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.sys.EventNews;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.EventNewsService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.*;

@Controller
public class EventController {

    private final EventNewsService eventNewsService;
    private final FileService fileService;
    private final UserService userService;


    public EventController(EventNewsService eventNewsService, FileService fileService, UserService userService) {
        this.eventNewsService = eventNewsService;
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping(value = MgmtUrls.EventNewsList)
    public String getEventNewsList() {
        return MgmtTemplates.EventNewsList;
    }

    @GetMapping(value = MgmtUrls.EventNewsAjaxLIst)
    @ResponseBody
    public Map<String, Object> getEventNewsAjaxList(Pageable pageable) {
        Page<EventNews> list = eventNewsService.getAllEvent(pageable);
        Map<String, Object> returns = new HashMap<>();
        returns.put("recordsTotal", list.getTotalElements()); //Total elements
        returns.put("recordsFiltered", list.getTotalElements()); //Filtered elements
        List<EventNews> eventNewsList = list.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(eventNewsList.size());
        for(EventNews eventNews: eventNewsList) {
            convenientForJSONArray.add(new Object[]{
                    eventNews.getId(),
                    eventNews.getTitle(),
                    eventNews.getDescription(),
                    eventNews.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(eventNews.getCreatedAt()):"",
                    eventNews.isStatus()
            });
        }

        returns.put("data", convenientForJSONArray);

        return returns;
    }

    @GetMapping(value = MgmtUrls.EventNewsCreate)
    public String createEventNews(Model model) {
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = new EventNews();
        eventNews.setStatus(false);
        eventNews.setCreatedAt(new Date());
        eventNews.setCreatedById(user.getId());
        eventNewsService.save(eventNews);

        File file = fileService.findByIdAndUploadUserId(eventNews.getFileId(), user.getId());

        model.addAttribute("file", file);
        model.addAttribute("eventNews", eventNews);
        model.addAttribute("actionUrl",MgmtUrls.EventNewsCreate );
        return MgmtTemplates.EventNewsCreate;
    }

    @PostMapping(value = MgmtUrls.EventNewsCreate)
    public String createEvent(
            EventNews eventNews
    ){
        User user = userService.getCurrentUserFromContext();
        EventNews oldEvent = eventNewsService.getById(eventNews.getId(), user.getId());
        if (oldEvent == null) return "redirect:" + MgmtUrls.EventNewsList;
        oldEvent.setStatus(eventNews.isStatus());
        oldEvent.setDescription(eventNews.getDescription());
        oldEvent.setTitle(eventNews.getTitle());
        oldEvent.setTheme(eventNews.getTheme());
        eventNewsService.update(oldEvent);
        return "redirect:" + MgmtUrls.EventNewsList;
    }

    @GetMapping(value = MgmtUrls.EventNewsEdit)
    public String editEventNews(@RequestParam(name = "id") Integer id, Model model) {
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = eventNewsService.getById(id, user.getId());
        eventNews.setUpdateAt(new Date());
        eventNews.setUpdateById(user.getId());
        eventNewsService.save(eventNews);

        File file = fileService.findByIdAndUploadUserId(eventNews.getFileId(), user.getId());

        model.addAttribute("file", file);
        model.addAttribute("eventNews", eventNews);
        model.addAttribute("actionUrl",MgmtUrls.EventNewsCreate );
        return MgmtTemplates.EventNewsCreate;
    }

    @GetMapping(value = MgmtUrls.EventNewsView)
    public String viewEventNews(@RequestParam(name = "id") Integer id, Model model) {
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = eventNewsService.getById(id, user.getId());
        if (eventNews == null) return MgmtTemplates.EventNewsList;
        File file = fileService.findByIdAndUploadUserId(eventNews.getFileId(), user.getId());

        model.addAttribute("img_source","https://eco-service.uz/show-image-on-web?file_id="+eventNews.getFileId()); /*bu yerga saytning aniq urlini yozish kerak*/
        model.addAttribute("news", eventNews);
        model.addAttribute("date", eventNews.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(eventNews.getCreatedAt()):"");
        return MgmtTemplates.EventNewsView;
    }


    @GetMapping(value = MgmtUrls.EventNewsDelete)
    public String deleteEventNews(@RequestParam(name = "id") Integer id){
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = eventNewsService.getById(id, user.getId());
        eventNews.setDelete(true);
        eventNewsService.update(eventNews);

        return "redirect:" + MgmtUrls.EventNewsList;
    }

    @RequestMapping(value = MgmtUrls.EventNewsFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileNname,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = eventNewsService.getById(id, user.getId());

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (eventNews == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplication="+eventNews.getId(),fileNname);
        if (file != null) {
            eventNews.setFileId(file.getId());
            eventNewsService.update(eventNews);
            responseMap.put("name", file.getName());
//            responseMap.put("link", RegUrls.RegApplicationFileDownload+ "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(value = MgmtUrls.EventNewsFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        EventNews eventNews = eventNewsService.getById(id, user.getId());

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (eventNews == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            if(eventNews.getFileId() != null) {
                eventNews.setFileId(null);
                eventNewsService.update(eventNews);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }









}
