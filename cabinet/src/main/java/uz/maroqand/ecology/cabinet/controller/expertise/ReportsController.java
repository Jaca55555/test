package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ReportsController {

    private final SoatoService soatoService;
    private final RegApplicationService regApplicationService;

    @Autowired
    public ReportsController(SoatoService soatoService, RegApplicationService regApplicationService){
       this.soatoService=soatoService;
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping(value = ExpertiseUrls.ReportList)
    public String getReport(Model model) {
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        return ExpertiseTemplates.ReportList;
    }
    @RequestMapping(value = ExpertiseUrls.ReportListAjax, produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> getReportsListAjax(
            FilterDto filterDto,
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        System.out.println("RegionId="+filterDto.getRegionId());
        System.out.println("SubRegionId="+filterDto.getSubRegionId());
        Page<Soato> soatoPage = soatoService.getFiltered(pageable);
        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", soatoPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", soatoPage.getTotalElements()); //Filtered elements

        List<Soato> soatoList = soatoPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(soatoList.size());
        for (Soato soato : soatoList){
            convenientForJSONArray.add(new Object[]{
                    soato.getNameTranslation(locale),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, null,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,null,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Process,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Process,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Approved,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Approved,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.NotConfirmed,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.NotConfirmed,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Modification,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Modification,soato.getId()),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, null,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,null,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Process,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Process,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Approved,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Approved,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.NotConfirmed,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.NotConfirmed,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Modification,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Modification,soato.getId()),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, null,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,null,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Process,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Process,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Approved,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Approved,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.NotConfirmed,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.NotConfirmed,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Modification,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Modification,soato.getId()),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, null,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,null,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Process,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Process,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Approved,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Approved,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.NotConfirmed,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.NotConfirmed,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Modification,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Modification,soato.getId()),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, null,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,null,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Process,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Process,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Approved,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Approved,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.NotConfirmed,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.NotConfirmed,soato.getId()),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Modification,soato.getId()):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Modification,soato.getId()),

            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }
}
