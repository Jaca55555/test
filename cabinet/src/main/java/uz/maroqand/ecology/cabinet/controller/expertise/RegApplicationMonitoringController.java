package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.entity.user.User;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.util.Common;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 23.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class RegApplicationMonitoringController {

    private final RegApplicationService regApplicationService;
    private final UserService userService;
    private final HelperService helperService;

    @Autowired
    public RegApplicationMonitoringController(RegApplicationService regApplicationService, UserService userService, HelperService helperService) {
        this.regApplicationService = regApplicationService;
        this.userService = userService;
        this.helperService = helperService;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplications)
    public String getApplications(Model model){

        List<RegApplication> regApplications = regApplicationService.getAllByDeletedFalse();
        model.addAttribute("regApplications",regApplications);
        model.addAttribute("users",userService.findPerformerList());
        return ExpertiseTemplates.RegApplications;
    }



    @RequestMapping(value = ExpertiseUrls.RegApplicationList,method = RequestMethod.GET)
    public String getRegApplicationList(Model model){
        model.addAttribute("users",userService.findPerformerList());
        return ExpertiseTemplates.RegApplicationList;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplicationList, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getRegApplicationListAjax(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "firstname",required = false) String firstname,
            @RequestParam(name = "lastname",required = false)String lastname,
            @RequestParam(name = "middlename",required = false) String middlename,
            Pageable pageable
    ){
        System.out.println("keldi");
        firstname = StringUtils.trimToNull(firstname);
        lastname = StringUtils.trimToNull(lastname);
        middlename = StringUtils.trimToNull(middlename);
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();


        List<RegApplication> regApplicationList = regApplicationService.getAllByDeletedFalse();
        //All User by organizationId
        //All RegApplication by reviewId
        //      User.id_0,count
        HashMap<String,Integer> userPermissionCount = new HashMap<>();
        for (RegApplication regApplication : regApplicationList){
            if(regApplication.getPerformerId()!=null){
                if(userPermissionCount.containsKey(regApplication.getPerformerId().toString()+"_0")){
                    userPermissionCount.put(regApplication.getPerformerId()+"_0",userPermissionCount.get(regApplication.getPerformerId().toString()+"_0")+1);
                }else {
                    userPermissionCount.put(regApplication.getPerformerId()+"_0",1);
                }

                if(regApplication.getStatus()!=null && regApplication.getStatus().equals(RegApplicationStatus.New)){
                    if(userPermissionCount.containsKey(regApplication.getPerformerId().toString()+"_1")){
                        userPermissionCount.put(regApplication.getPerformerId()+"_1",userPermissionCount.get(regApplication.getPerformerId().toString()+"_1")+1);
                    }else {
                        userPermissionCount.put(regApplication.getPerformerId()+"_1",1);
                    }
                }

                if(regApplication.getDeadlineDate()!=null && regApplication.getDeadlineDate().before(new Date())){
                    if(userPermissionCount.containsKey(regApplication.getPerformerId().toString()+"_2")){
                        userPermissionCount.put(regApplication.getPerformerId()+"_2",userPermissionCount.get(regApplication.getPerformerId().toString()+"_2")+1);
                    }else {
                        userPermissionCount.put(regApplication.getPerformerId()+"_2",1);
                    }
                }

                if(regApplication.getStatus()!=null && regApplication.getStatus().equals(RegApplicationStatus.Approved)){
                    if(userPermissionCount.containsKey(regApplication.getPerformerId().toString()+"_3")){
                        userPermissionCount.put(regApplication.getPerformerId()+"_3",userPermissionCount.get(regApplication.getPerformerId().toString()+"_3")+1);
                    }else {
                        userPermissionCount.put(regApplication.getPerformerId()+"_3",1);
                    }
                }
            }
        }

        Page<User> userPage = userService.findFiltered(
                id,
                lastname,
                firstname,
                middlename,
                null,
                null,//TODO organizationId
                null,
                null,
                pageable
        );
        List<User> userList = userPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(userList.size());
        for (User user1 : userList) {
            convenientForJSONArray.add(new Object[]{
                    user1.getId(),
                    user1.getFullName(),
                    userPermissionCount.containsKey(user1.getId()+"_0")? userPermissionCount.get(user1.getId()+"_0"):"0",
                    userPermissionCount.containsKey(user1.getId()+"_1")? userPermissionCount.get(user1.getId()+"_1"):"0",
                    userPermissionCount.containsKey(user1.getId()+"_2")? userPermissionCount.get(user1.getId()+"_2"):"0",
                    userPermissionCount.containsKey(user1.getId()+"_3")? userPermissionCount.get(user1.getId()+"_3"):"0"
            });
        }

        result.put("recordsTotal", userPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", userPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

}
