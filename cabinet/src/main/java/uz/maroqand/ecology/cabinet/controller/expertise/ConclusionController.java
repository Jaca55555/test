package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.core.util.TinParser;

import java.util.*;

@Controller
public class ConclusionController {

    private final UserService userService;
    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final ConclusionService conclusionService;

    @Autowired
    public ConclusionController(UserService userService, RegApplicationService regApplicationService, ClientService clientService, ConclusionService conclusionService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.conclusionService = conclusionService;
    }


    @RequestMapping(ExpertiseUrls.ConclusionList)
    public String conclusionList(Model model){
        return ExpertiseTemplates.ConclusionList;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> conclusionListAjax(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "tin", required = false) String tinStr,
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ){

        Integer tin = TinParser.trimIndividualsTinToNull(tinStr);
        name = StringUtils.trimToNull(name);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        HashMap<String, Object> result = new HashMap<>();
        Page<Conclusion> conclusionPage = conclusionService.findFiltered(id, dateBegin, dateEnd,tin,name, pageable);
        Calendar c = Calendar.getInstance();
        c.set(c.getTime().getYear(),c.getTime().getMonth(),c.getTime().getDate(),0,0,0);
        List<Conclusion> conclusionList = conclusionPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(conclusionList.size());
        for (Conclusion conclusion : conclusionList){
            RegApplication regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
            convenientForJSONArray.add(new Object[]{
                    conclusion.getId(),
                    conclusion.getNumber(),
                    conclusion.getDate()!= null ? Common.uzbekistanDateAndTimeFormat.format(conclusion.getDate()) : "",
                    regApplication.getApplicant().getTin(),
                    regApplication.getName(),
                    regApplication.getMaterials(),
                    regApplication.getCategory() !=null ? regApplication.getCategory().getName() : "",
                    conclusion.getDeadlineDate() != null ? Common.uzbekistanDateAndTimeFormat.format(conclusion.getDeadlineDate()) : "",
                    conclusion.getDeadlineDate() != null ? (conclusion.getDeadlineDate().compareTo(c.getTime())>=0?Boolean.TRUE:Boolean.FALSE): Boolean.TRUE,
                    regApplication.getApplicant().getName()
            });
        }

        result.put("recordsTotal", conclusionPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", conclusionPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConclusionView)
    public String conclusionView(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Conclusion conclusion = conclusionService.getById(id);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        if(conclusion == null){
            return "redirect:" + ExpertiseUrls.ConclusionList;
        }
        RegApplication regApplication = null;
        if(conclusion.getRegApplicationId() != null){
            regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
        }

        model.addAttribute("conclusion", conclusion);
        model.addAttribute("regApplication", regApplication);

        return ExpertiseTemplates.ConclusionView;
    }
}
