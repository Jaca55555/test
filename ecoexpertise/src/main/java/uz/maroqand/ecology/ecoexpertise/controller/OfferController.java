package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationInputType;
import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.OfferService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegUrls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class OfferController {
    private UserService userService;
    private RegApplicationService regApplicationService;
    private HelperService helperService;
    private OrganizationService organizationService;
    private OfferService offerService;



    @Autowired
    public OfferController(UserService userService, RegApplicationService regApplicationService, HelperService helperService, OrganizationService organizationService, OfferService offerService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.helperService = helperService;
        this.organizationService = organizationService;
        this.offerService = offerService;
    }

    @RequestMapping(RegUrls.OfferList)
    public String appealUserListPage( Model model ) {
        return RegTemplates.OfferList;
    }
    @RequestMapping(value = RegUrls.OfferListAjax,produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> offerListAjax(
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();
        FilterDto filterDto = new FilterDto();
        filterDto.setByLeTin(user.getLeTin());
        filterDto.setByTin(user.getTin());

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                null,
                null,
                null,
                user.getId(),
                RegApplicationInputType.ecoService,
                pageable);

        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    regApplication.getOfferId(),
                    regApplication.getConfirmLogAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getConfirmLogAt()):"",
                    null,
                    organizationService.getById(regApplication.getReviewId()).getNameTranslation(locale),
                    organizationService.getById(regApplication.getReviewId()).getTin(),
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
}
    @RequestMapping(value = RegUrls.OfferView)
    public String appealUser(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        Offer offer = offerService.getById(id);
        if(offer==null){
            return "redirect:" + RegUrls.OfferList;
        }
        model.addAttribute("offer",offer);
        return RegTemplates.OfferView;
    }





}
