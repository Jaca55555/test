package uz.maroqand.ecology.cabinet.controller.expertise_mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise_mgmt.ExpertiseMgmtUrls;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.ActivityService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ActivityController(ActivityService activityService, TableHistoryService tableHistoryService, UserService userService, ObjectMapper objectMapper) {
        this.activityService = activityService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(ExpertiseMgmtUrls.ActivityList)
    public String getActivityList(Model model){
        model.addAttribute("categoryList", Category.getCategoryList());
        return ExpertiseMgmtTemplates.ActivityList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ActivityListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getActivityListAjax(
            @RequestParam(name = "id",required = false)Integer id,
            @RequestParam(name = "category",required = false)Category category,
            @RequestParam(name = "name",required = false)String name,
            Pageable pageable
    ){
        HashMap<String,Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Page<Activity> activityPage = activityService.findFiltered(id,category,name,locale,pageable);

        result.put("recordsTotal", activityPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", activityPage.getTotalElements()); //Filtered elements

        List<Activity> activityList = activityPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(activityList.size());
        for (Activity activity : activityList){
            convenientForJSONArray.add(new Object[]{
                   activity.getId()!=null?activity.getId():"",
                   activity.getCategory()!=null?activity.getCategory().getName():"",
                   activity.getName()!=null?activity.getNameTranslation(locale):""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseMgmtUrls.ActivityNew)
    public String getActivityNewPage(Model model){
        model.addAttribute("action_url",ExpertiseMgmtUrls.ActivityCreate);
        model.addAttribute("categoryList",Category.getCategoryList());
        model.addAttribute("activity",new Activity());
        return ExpertiseMgmtTemplates.ActivityNew;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ActivityCreate,method = RequestMethod.POST)
    public String createActivity(
            @RequestParam(name = "category",required = false)Category category,
            Activity activity
    ){
        User user = userService.getCurrentUserFromContext();
        activity.setCategory(category);
        activityService.createActivity(activity);
        String after = "";
        try {
            after = objectMapper.writeValueAsString(activity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.Activity,
                activity.getId(),
                "",
                after,
                "Activity successfully created!!!",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + ExpertiseMgmtUrls.ActivityList;
    }

    @RequestMapping(value = ExpertiseMgmtUrls.ActivityEdit)
    public String getActivityEditPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ){
        Activity oldActivity = activityService.getById(id);
        if (oldActivity == null){
            return "redirect:" + ExpertiseMgmtUrls.ActivityList;
        }
        model.addAttribute("action_url",ExpertiseMgmtUrls.ActivityUpdate);
        model.addAttribute("categoryList",Category.getCategoryList());
        model.addAttribute("activity",oldActivity);
        return ExpertiseMgmtTemplates.ActivityNew;
    }


    @RequestMapping(value = ExpertiseMgmtUrls.ActivityUpdate,method = RequestMethod.POST)
    public String updateEditPage(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "category",required = false)Category category,
            Activity activity
    ){
        String before = "";
        String after = "";
        User user = userService.getCurrentUserFromContext();
        Activity oldActivity = activityService.getById(id);
        if (oldActivity == null){
            return "redirect:" + ExpertiseMgmtUrls.ActivityList;
        }
        try {
            before = objectMapper.writeValueAsString(oldActivity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        activity.setCategory(category);
        activityService.updateActivity(activity);
        try {
            after = objectMapper.writeValueAsString(activity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Activity,
                activity.getId(),
                before,
                after,
                "Activity successfully created!!!",
                user.getId(),
                user.getUserAdditionalId()
        );
        return "redirect:" + ExpertiseMgmtUrls.ActivityList;
    }
}
