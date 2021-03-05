package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;

import java.util.*;

@Controller
public class ReportsController {

    private final SoatoService soatoService;
    private final RegApplicationService regApplicationService;
    private final OrganizationService organizationService;

    @Autowired
    public ReportsController(SoatoService soatoService, RegApplicationService regApplicationService, OrganizationService organizationService){
       this.soatoService=soatoService;
        this.regApplicationService = regApplicationService;
        this.organizationService = organizationService;
    }

    @RequestMapping(value = ExpertiseUrls.ReportList)
    public String getReport(Model model) {
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("organizationList", organizationService.getList());
        return ExpertiseTemplates.ReportList;
    }
    @RequestMapping(value = ExpertiseUrls.ReportListAjax, produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> getReportsListAjax(
            @RequestParam(name = "regionId", required = false)Integer regionId,
            @RequestParam(name = "subRegionId[]", required = false) Set<Integer> subRegionIds,
            @RequestParam(name = "organizationId[]", required = false) Set<Integer> organizationIds,
            Pageable pageable
    ) {
        System.out.println("OrganizationIds="+organizationIds);
        System.out.println("SubRegionIds="+subRegionIds);

        if(organizationIds==null){
            Set<Integer>organizations=new HashSet<>();
            for (int i=1;i<=19;i++){
               if(i==2){
                   continue;
               }

                organizations.add(i);
            }
            System.out.println("organizations="+organizations);
            organizationIds=organizations;
        }
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
//        System.out.println("RegionId="+filterDto.getRegionId());
//        System.out.println("SubRegionId="+filterDto.getSubRegionId());
        System.out.println("organizationId="+organizationIds);
        Set<Integer> subRegions = new HashSet<>();
        Page<Soato> soatoPage = soatoService.getFiltered(regionId,subRegionIds,null,pageable);
        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", soatoPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", soatoPage.getTotalElements()); //Filtered elements

        List<Soato> soatoList = soatoPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(soatoList.size());
        for (Soato soato : soatoList){
            convenientForJSONArray.add(new Object[]{
                    soato.getNameTranslation(locale),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll,RegApplicationStatus.Modification,soato.getId(),organizationIds),

            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }
    @RequestMapping(value = ExpertiseUrls.ReportSoato, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getSoatoListAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "regionId") Integer regionId
    ){
        System.out.println("regionId="+regionId);

        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<Soato> soatoPage = soatoService.findFiltered(regionId!=null?regionId:1,null,null,pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Soato soato :soatoPage.getContent()) {
            select2DtoList.add(new Select2Dto(soato.getId(), soato.getName()));
        }

        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(soatoPage.getTotalElements());

        result.put("results", select2DtoList);
        result.put("pagination", paginationDto);
        result.put("total_count", soatoPage.getTotalElements());
        return result;
    }
    @RequestMapping(value = ExpertiseUrls.ReportOrganization, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getSoatoOrganization(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<Organization> organizationPage = organizationService.getFiltered(search,null,null,pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Organization organization :organizationPage.getContent()) {
            select2DtoList.add(new Select2Dto(organization.getId(), organization.getName()));
        }

        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(organizationPage.getTotalElements());

        result.put("results", select2DtoList);
        result.put("pagination", paginationDto);
        result.put("total_count", organizationPage.getTotalElements());
        return result;
    }
}
