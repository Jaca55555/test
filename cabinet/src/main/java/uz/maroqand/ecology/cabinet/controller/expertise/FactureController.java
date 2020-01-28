package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
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

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
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
            @RequestParam(name = "number", required = false) String number,
            @RequestParam(name = "payerName", required = false) String payerName,
            @RequestParam(name = "payerTin", required = false) String payerTin,
            @RequestParam(name = "payeeName", required = false) String payeeName,
            @RequestParam(name = "payeeTin", required = false) String payeeTin,
            Pageable pageable
    ){
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        number = StringUtils.trimToNull(number);
        payerName = StringUtils.trimToNull(payerName);
        payerTin = StringUtils.trimToNull(payerTin);
        payeeName = StringUtils.trimToNull(payeeName);
        payeeTin = StringUtils.trimToNull(payeeTin);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        HashMap<String,Object> result = new HashMap<>();

        Page<Facture> facturePage = factureService.findFiltered(
                dateBegin,
                dateEnd,
                number,
                payerName,
                payerTin,
                payeeName,
                payeeTin,
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
                    facture.getPayeeTin(),
                    facture.getAmount()
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
        model.addAttribute("facture", facture);
        model.addAttribute("factureProductList", factureService.getByFactureId(id));
        return ExpertiseTemplates.FactureView;
    }

}
