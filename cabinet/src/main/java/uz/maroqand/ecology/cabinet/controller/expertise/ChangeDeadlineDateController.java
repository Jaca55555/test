package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.ChangeDeadlineDateStatus;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.ChangeDeadlineDate;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ChangeDeadlineDateService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.*;

@Controller
public class ChangeDeadlineDateController {

    private final ChangeDeadlineDateService changeDeadlineDateService;
    private final RegApplicationService regApplicationService;
    private final UserService userService;
    private final SmsSendService smsSendService;
    private final ClientService clientService;

    public ChangeDeadlineDateController(ChangeDeadlineDateService changeDeadlineDateService, RegApplicationService regApplicationService, UserService userService, SmsSendService smsSendService, ClientService clientService) {
        this.changeDeadlineDateService = changeDeadlineDateService;
        this.regApplicationService = regApplicationService;
        this.userService = userService;
        this.smsSendService = smsSendService;
        this.clientService = clientService;
    }

    @RequestMapping(value = ExpertiseUrls.ChangeDeadlineDateList)
    public String changeDeadlineDateList(Model model){
        model.addAttribute("changeDeadlineDateStatusList" ,ChangeDeadlineDateStatus.getChangeDeadlineDateStatusList());
        return ExpertiseTemplates.ChangeDeadlineDateList;
    }

    @RequestMapping(value = ExpertiseUrls.ChangeDeadlineDateListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getActivityListAjax(
            @RequestParam(name = "id",required = false)Integer id,
            @RequestParam(name = "changeDeadlineDateStatus",required = false) ChangeDeadlineDateStatus changeDeadlineDateStatus,
            Pageable pageable
    ){
        HashMap<String,Object> result = new HashMap<>();
        Page<ChangeDeadlineDate> changeDeadlineDatePage = changeDeadlineDateService.findFiltered(id,changeDeadlineDateStatus,pageable);

        result.put("recordsTotal", changeDeadlineDatePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", changeDeadlineDatePage.getTotalElements()); //Filtered elements

        List<ChangeDeadlineDate> changeDeadlineDateList = changeDeadlineDatePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(changeDeadlineDateList.size());
        for (ChangeDeadlineDate changeDeadlineDate : changeDeadlineDateList){
            convenientForJSONArray.add(new Object[]{
                    changeDeadlineDate.getId(),
                    changeDeadlineDate.getRegApplicationId(),
                    changeDeadlineDate.getReason(),
                    changeDeadlineDate.getAfterDeadlineDate()!=null? Common.uzbekistanDateFormat.format(changeDeadlineDate.getAfterDeadlineDate()):"",
                    changeDeadlineDate.getBeforeDeadlineDate()!=null? Common.uzbekistanDateFormat.format(changeDeadlineDate.getBeforeDeadlineDate()):"",
                    changeDeadlineDate.getStatus(),
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ChangeDeadlineDateView,method = RequestMethod.GET)
    public String getViewPage(
            Model model,
            @RequestParam(name = "id") Integer id
    ){
        ChangeDeadlineDate changeDeadlineDate = changeDeadlineDateService.getById(id);
        if (changeDeadlineDate==null){
            return "redirect:" + ExpertiseUrls.ChangeDeadlineDateList;
        }

        model.addAttribute("changeDeadlineDate",changeDeadlineDate);

        return ExpertiseTemplates.ChangeDeadlineDateView;
    }

    @RequestMapping(value = ExpertiseUrls.ChangeDeadlineDateConfig,method = RequestMethod.POST)
    public String getChangeDeadlineDateConfigPage(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "status") Integer statusChange,
            @RequestParam(name = "afterDeadlineDate",required = false) String dateStr

    ){
        User user = userService.getCurrentUserFromContext();
        ChangeDeadlineDate changeDeadlineDate = changeDeadlineDateService.getById(id);
        if (changeDeadlineDate==null){
            return "redirect:" + ExpertiseUrls.ChangeDeadlineDateList;
        }

        changeDeadlineDate.setCreatedById(user.getId());

        if (statusChange==1){
            Date afterDate = DateParser.TryParse(dateStr,Common.uzbekistanDateFormat);
            changeDeadlineDate.setAfterDeadlineDate(afterDate);
            changeDeadlineDate.setStatus(ChangeDeadlineDateStatus.Approved);
            changeDeadlineDate = changeDeadlineDateService.save(changeDeadlineDate);
            RegApplication regApplication = regApplicationService.getById(changeDeadlineDate.getRegApplicationId());
            regApplication.setDeadlineDate(changeDeadlineDate.getAfterDeadlineDate());
            regApplicationService.update(regApplication);

            Client client = clientService.getById(regApplication.getApplicantId());
            smsSendService.sendSMS(client.getPhone(), " Arizangiz muddati uzaytirildi, ariza raqami ", regApplication.getId(), client.getName());

            return "redirect:" + ExpertiseUrls.ChangeDeadlineDateView + "?id=" + changeDeadlineDate.getId();
        }

        changeDeadlineDate.setStatus(ChangeDeadlineDateStatus.Denied);
        changeDeadlineDate = changeDeadlineDateService.save(changeDeadlineDate);

        return "redirect:" + ExpertiseUrls.ChangeDeadlineDateView + "?id=" + changeDeadlineDate.getId();

    }

}
