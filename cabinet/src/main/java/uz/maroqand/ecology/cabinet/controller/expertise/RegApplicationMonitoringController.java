package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @RequestMapping(value = ExpertiseUrls.RegApplications,method = RequestMethod.GET)
    public String getApplications(Model model){
        model.addAttribute("proccess",getProccess());
        model.addAttribute("users",userService.findPerformerList());
        return ExpertiseTemplates.RegApplications;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplications,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getFilter(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "firstname",required = false) String firstname,
            @RequestParam(name = "lastname",required = false)String lastname,
            @RequestParam(name = "middlename",required = false) String middlename
    ){

        firstname = StringUtils.trimToNull(firstname);
        lastname = StringUtils.trimToNull(lastname);
        middlename = StringUtils.trimToNull(middlename);

        System.out.println("id==" + id);
        System.out.println("firstname==" + firstname);
        System.out.println("lastname==" + lastname);
        System.out.println("middlename==" + middlename);

        List<RegApplication> regApplicationList = regApplicationService.getAllByPerfomerIdNotNullDeletedFalse();
        PageRequest pageRequest = new PageRequest(0, regApplicationList.size(), Sort.Direction.ASC, "id");
        Page<User> userPage = userService.findFiltered(id,lastname,firstname,middlename,null,null,null,null,pageRequest );
        List<User> userList = userPage.getContent();
        System.out.println(userList.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<Object[]> convenientForJSONArray = new ArrayList<>(userList.size());
        HashMap<String,Object> result = new HashMap<>();

        for (RegApplication regApplication: regApplicationList) {
            for (User user: userList) {
                if (user.getId().equals(regApplication.getPerformerId())){
                    convenientForJSONArray.add(new Object[]{
                            regApplication.getId(),
                            regApplication.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                            regApplication.getMaterialId()!=null?helperService.getMaterial(regApplication.getMaterialId(),locale):"",
                            regApplication.getDeadlineDate()!=null? Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                            user.getFullName()
                    });
                }
            }
        }

        result.put("data",convenientForJSONArray);

        return result;
    }




    @RequestMapping(value = ExpertiseUrls.RegApplicationList,method = RequestMethod.GET)
    public String getRegApplicationList(Model model){
        model.addAttribute("proccess",getProccess());
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


        List<RegApplication> regApplicationList = regApplicationService.getAllByPerfomerIdNotNullDeletedFalse();
        //All User by organizationId
        //All RegApplication by reviewId
        //      User.id_0,count
        HashMap<String,Integer> userPermissionCount = new HashMap<>();
        for (RegApplication regApplication : regApplicationList){
//            if(regApplication.getPerformerId()!=null){
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
//            }
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

    public List<Integer> getProccess(){

        List<RegApplication> regApplicationList = regApplicationService.getAllByPerfomerIdNotNullDeletedFalse();
        Integer count = regApplicationList.size();
        Integer inProgress = 0;
        Integer deelineNow = 0;//3 kun colganlar
        Integer deeline = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-3);
        Date deedlineThree = calendar.getTime();
        if (count>0){
            for (RegApplication regApplication: regApplicationList) {
                    if (regApplication.getStatus()!=null && regApplication.getStatus().equals(RegApplicationStatus.New))inProgress++;
                    if (regApplication.getDeadlineDate()!=null && (regApplication.getDeadlineDate().after(deedlineThree) && regApplication.getDeadlineDate().before(new Date())))deelineNow++;
                    if (regApplication.getDeadlineDate()!=null && regApplication.getDeadlineDate().before(new Date()))deeline++;
            }
        }
        List<Integer> result = new ArrayList<>();
        result.add(inProgress);//sonda
        inProgress = (inProgress*100)/count;
        result.add(inProgress);//foizda
        result.add(deelineNow);//sonda
        deelineNow = (deelineNow*100)/count;
        result.add(deelineNow);//foizda
        result.add(deeline);//sonda
        deeline = (deeline*100)/count;
        result.add(deeline);//foizda
        return result;
    }

}
