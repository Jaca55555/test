package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.entity.expertise.Facture;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.FactureService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Controller
public class FactureController {

    private final FactureService factureService;
    private final UserService userService;

    public FactureController(FactureService factureService, UserService userService) {
        this.factureService = factureService;
        this.userService = userService;
    }

    @RequestMapping(value = ExpertiseUrls.FactureList, method = RequestMethod.GET)
    public String getFactureListPage(Model model){
        return ExpertiseTemplates.FactureList;
    }

    @RequestMapping(value = ExpertiseUrls.FactureList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getFactureListAjax(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "dateToday", required = false, defaultValue = "false") Boolean dateToday,
            @RequestParam(name = "dateThisMonth", required = false, defaultValue = "false") Boolean dateThisMonth,
            @RequestParam(name = "invoice", required = false) String invoiceNumber,
            @RequestParam(name = "service", required = false) String service,
            @RequestParam(name = "detail", required = false) String detail,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "subRegionId", required = false) Integer subRegionId,
            Pageable pageable
    ){
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        invoiceNumber = StringUtils.trimToNull(invoiceNumber);
        service = StringUtils.trimToNull(service);
        detail = StringUtils.trimToNull(detail);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<Facture> facturePage = factureService.findFiltered(
                dateBegin,
                dateEnd,
                dateToday,
                dateThisMonth,
                invoiceNumber,
                service,
                detail,
                regionId,
                subRegionId,
                user.getOrganizationId(),
                pageable
        );

        List<Facture> factureList = facturePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(factureList.size());
        for (Facture facture : factureList){
            convenientForJSONArray.add(new Object[]{
                    facture.getId(),
                    facture.getNumber(),
                    facture.getDate()!=null? Common.uzbekistanDateAndTimeFormat.format(facture.getDate()):"",
                    facture.getPayerName(),
                    facture.getPayerTin(),
                    facture.getPayeeName(),
                    facture.getPayeeTin()
            });
        }

        result.put("recordsTotal", facturePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", facturePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.FactureView, method = RequestMethod.GET)
    public String getFactureViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Facture facture = factureService.getById(id);
        if(facture == null){
            return "redirect:" + ExpertiseUrls.FactureList;
        }
        model.addAttribute("facture",facture);
        return ExpertiseTemplates.FactureView;
    }

}
