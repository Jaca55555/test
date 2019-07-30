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
import uz.maroqand.ecology.core.entity.expertise.ChangeDeadlineDate;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.expertise.ChangeDeadlineDateService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.*;

@Controller
public class ChangeDeadlineDateController {

    private final ChangeDeadlineDateService changeDeadlineDateService;
    private final RegApplicationService regApplicationService;

    public ChangeDeadlineDateController(ChangeDeadlineDateService changeDeadlineDateService, RegApplicationService regApplicationService) {
        this.changeDeadlineDateService = changeDeadlineDateService;
        this.regApplicationService = regApplicationService;
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
            @RequestParam(name = "beforeDeadlineDate",required = false) String dateStr

    ){
        ChangeDeadlineDate changeDeadlineDate = changeDeadlineDateService.getById(id);
        if (changeDeadlineDate==null){
            return "redirect:" + ExpertiseUrls.ChangeDeadlineDateList;
        }

        if (statusChange==1){
            Date beforeDate = DateParser.TryParse(dateStr,Common.uzbekistanDateFormat);
            changeDeadlineDate.setBeforeDeadlineDate(beforeDate);
            changeDeadlineDate.setStatus(ChangeDeadlineDateStatus.Approved);
            changeDeadlineDate = changeDeadlineDateService.save(changeDeadlineDate);
            RegApplication regApplication = regApplicationService.getById(changeDeadlineDate.getRegApplicationId());
            regApplication.setDeadlineDate(changeDeadlineDate.getBeforeDeadlineDate());
            regApplicationService.update(regApplication);
            return "redirect:" + ExpertiseUrls.ChangeDeadlineDateView + "?id=" + changeDeadlineDate.getId();
        }

            changeDeadlineDate.setStatus(ChangeDeadlineDateStatus.Denied);
        changeDeadlineDate = changeDeadlineDateService.save(changeDeadlineDate);

        return "redirect:" + ExpertiseUrls.ChangeDeadlineDateView + "?id=" + changeDeadlineDate.getId();

    }

}
