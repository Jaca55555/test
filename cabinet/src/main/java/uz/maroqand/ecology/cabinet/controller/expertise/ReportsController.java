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
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;

import java.util.*;

@Controller
public class ReportsController {

    private final SoatoService soatoService;
    private final RegApplicationService regApplicationService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public ReportsController(SoatoService soatoService, RegApplicationService regApplicationService, OrganizationService organizationService, UserService userService){
       this.soatoService=soatoService;
        this.regApplicationService = regApplicationService;
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @RequestMapping(value = ExpertiseUrls.ReportList)
    public String getReport(Model model) {


        User user = userService.getCurrentUserFromContext();
        List<Soato> list;
        if (user.getOrganizationId() != null){
            Organization organization = organizationService.getById(user.getOrganizationId());
            list = new ArrayList<>();
            list.add(soatoService.getById(organization.getRegionId()));
        }else {
                list = soatoService.getRegions();
        }
        model.addAttribute("regions",list);
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("organizationList", organizationService.getList());
        return ExpertiseTemplates.ReportList;
    }
    @RequestMapping(value = ExpertiseUrls.ReportListAjax, produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> getReportsListAjax(
            @RequestParam(name = "regionId", required = false)Integer regionId,
            @RequestParam(name = "dateBegin") String dateBeginStr,
            @RequestParam(name = "dateEnd") String dateEndStr,
            @RequestParam(name = "subRegionId[]", required = false) Set<Integer> subRegionIds,
            @RequestParam(name = "organizationId[]", required = false) Set<Integer> organizationIds,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user.getOrganizationId() != null){
            Organization organization = organizationService.getById(user.getOrganizationId());
            regionId = organization.getRegionId();
        }
        System.out.println("OrganizationIds="+organizationIds);
        System.out.println("SubRegionIds="+subRegionIds);
        Date dateBegin= DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormatUz);
        Date dateEnd= DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormatUz);
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
        System.out.println("dateBegin="+dateBegin);
        if(dateBegin==null && dateEnd==null){
            regionId=1;
        }
        Set<RegApplicationStatus> statuses = new HashSet<>();
        statuses.add(RegApplicationStatus.Initial);
        statuses.add(RegApplicationStatus.CheckSent);
        statuses.add(RegApplicationStatus.CheckConfirmed);
        statuses.add(RegApplicationStatus.CheckNotConfirmed);
        statuses.add(RegApplicationStatus.Process);
        statuses.add(RegApplicationStatus.Modification);
        statuses.add(RegApplicationStatus.Approved);
        statuses.add(RegApplicationStatus.NotConfirmed);
        statuses.add(RegApplicationStatus.Canceled);
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
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, dateBegin,dateEnd,null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category1, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category1, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, dateBegin,dateEnd, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category2, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category2, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, dateBegin,dateEnd, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category3, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category3, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, dateBegin,dateEnd, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.Category4, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.Category4, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),

                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, dateBegin,dateEnd, null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(Category.CategoryAll, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(Category.CategoryAll, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),


                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(null,  dateBegin,dateEnd,null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(null, dateBegin,dateEnd,null,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(null, dateBegin,dateEnd, RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(null, dateBegin,dateEnd,RegApplicationStatus.Process,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(null, dateBegin,dateEnd, RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(null, dateBegin,dateEnd,RegApplicationStatus.Approved,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(null, dateBegin,dateEnd, RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(null, dateBegin,dateEnd,RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds),
                    soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(null, dateBegin,dateEnd, RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(null, dateBegin,dateEnd,RegApplicationStatus.Modification,soato.getId(),organizationIds),

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
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<Soato> soatoPage = soatoService.findFiltered(regionId!=null?regionId:1,null,null,pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Soato soato :soatoPage.getContent()) {
            select2DtoList.add(new Select2Dto(soato.getId(), soato.getNameTranslation(locale)));
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
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<Organization> organizationPage = organizationService.getFiltered(search,null,null,pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Organization organization :organizationPage.getContent()) {
            select2DtoList.add(new Select2Dto(organization.getId(), organization.getNameTranslation(locale)));
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
