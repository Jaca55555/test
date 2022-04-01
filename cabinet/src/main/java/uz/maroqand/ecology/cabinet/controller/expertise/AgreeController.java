package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationInputType;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.OfferService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Controller
public class AgreeController {
    private UserService userService;
    private RegApplicationService regApplicationService;
    private HelperService helperService;
    private SoatoService soatoService;
    private OrganizationService organizationService;
    private OfferService offerService;



    @Autowired
    public AgreeController(UserService userService, RegApplicationService regApplicationService, HelperService helperService, SoatoService soatoService, OrganizationService organizationService, OfferService offerService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.organizationService = organizationService;
        this.offerService = offerService;
    }

    @RequestMapping(ExpertiseUrls.AgreeList)
    public String appealUserListPage( Model model ) {
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("organizationList", organizationService.getList());
        return ExpertiseTemplates.AgreeList;
    }
    @RequestMapping(value = ExpertiseUrls.AgreeListAjax,produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> offerListAjax(
            FilterDto filterDto,
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();
//        filterDto.setByLeTin(user.getLeTin());
//        filterDto.setByTin(user.getTin());

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getRole().getId()!=16 ?user.getOrganizationId():null,
                null,
                null,
                null,
                null,
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
                    regApplication.getApplicant()!=null?regApplication.getApplicant().getTin():null,
                    regApplication.getApplicant()!=null?regApplication.getApplicant().getName():null,
                    regApplication.getApplicant()!=null?regApplication.getApplicant().getTin():null,
                    regApplication.getContractNumber()
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }
    @RequestMapping(value = ExpertiseUrls.AgreeView)
    public String appealUser(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        Offer offer = offerService.getById(id);
        if(offer==null){
            return "redirect:" + ExpertiseUrls.AgreeList;
        }
        model.addAttribute("offer",offer);
        return ExpertiseTemplates.AgreeView;
}
}
