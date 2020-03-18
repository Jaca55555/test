package uz.maroqand.ecology.cabinet.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.admin.AdminTemplates;
import uz.maroqand.ecology.cabinet.constant.admin.AdminUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class OrganizationController {

    private final DepartmentService departmentService;
    private final UserService userService;
    private final HelperService helperService;
    private final OrganizationService organizationService;
    private final ObjectMapper objectMapper;
    private final TableHistoryService tableHistoryService;
    private final UserAdditionalService userAdditionalService;
    private final SoatoService soatoService;
    @Autowired
    public OrganizationController(DepartmentService departmentService,SoatoService soatoService, UserService userService, HelperService helperService, OrganizationService organizationService, ObjectMapper objectMapper, TableHistoryService tableHistoryService, UserAdditionalService userAdditionalService) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.helperService = helperService;
        this.organizationService = organizationService;
        this.objectMapper = objectMapper;
        this.tableHistoryService = tableHistoryService;
        this.userAdditionalService = userAdditionalService;
        this.soatoService=soatoService;
    }
    @RequestMapping(AdminUrls.OrganizationList)
    public String getDepartmentList(Model model) {
        model.addAttribute("add_url", AdminUrls.OrganizationNew);
        return AdminTemplates.OrganizationList;
    }

    @RequestMapping(value = AdminUrls.OrganizationListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getOrganizationListAjax(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "address", required = false) String address,


            Pageable pageable
    ) {
        name = StringUtils.trimToNull(name);

        Page<Organization>  OrganizationPage = organizationService.getFiltered(name, account, address, pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", OrganizationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", OrganizationPage.getTotalElements()); //Filtered elements

        List<Organization> organizations = OrganizationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(organizations.size());

        for(Organization organization : organizations) {
            convenientForJSONArray.add(new Object[]{
                    organization.getId(),
                    organization.getAccount(),
                    organization.getName(),
                    organization.getNameRu(),
                    organization.getDirector(),
                    organization.getAddress(),
                    organization.getLastNumber()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(AdminUrls.OrganizationNew)
    public String organizationNew(Model model) {
        Organization organization=new Organization();
        model.addAttribute("organization",organization);
        model.addAttribute("soato",soatoService.getRegions());
        model.addAttribute("subRegionId",null);
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("action_url", AdminUrls.OrganizationCreate);
        model.addAttribute("back_url", AdminUrls.OrganizationList);
        return AdminTemplates.OrganizationNew;
    }

    @RequestMapping(AdminUrls.OrganizationCreate)
    public String departmentCreate(Organization organization) {
        organizationService.create(organization);
        return "redirect:" + AdminUrls.OrganizationList;
    }
    @PostMapping(value = AdminUrls.OrganizationtGetByRegion)
    @ResponseBody
    public List<Soato> getList(
    ){
        int regionId = 1;
        Soato soato=soatoService.getById(regionId);
        if (soato==null) return null;

       List<Soato> SoatoList = soatoService.getSubregionsbyregionId(soato.getParentId());
        return SoatoList;
    }

    @RequestMapping(AdminUrls.OrganizationEdit)
    public String organizationEdit(
            Model model,
            @RequestParam(name = "id") Integer organizationId
    ) {
        Organization organization = organizationService.getById(organizationId);
        if (organization == null) {
            return "redirect:" + AdminUrls.OrganizationList;
        }

        model.addAttribute("organization", organization);
        model.addAttribute("action_url", AdminUrls.OrganizationUpdate);
        model.addAttribute("back_url", AdminUrls.OrganizationList);
        return AdminTemplates.OrganizationNew;
    }
    @RequestMapping(AdminUrls.OrganizationUpdate)
    public String organizationUpdate(
            @RequestParam(name = "id") Integer organizationId,
            Organization organization
    ) {
        User user = userService.getCurrentUserFromContext();
        Organization oldOrganization= organizationService.getById(organizationId);
        if (oldOrganization == null) {
            return "redirect:" + AdminUrls.DepartmentList;
        }



        oldOrganization.setAccount(organization.getAccount());
        oldOrganization.setAddress(organization.getAddress());
        oldOrganization.setDirector(organization.getDirector());
        oldOrganization.setLastNumber(organization.getLastNumber());
        oldOrganization.setManager(organization.getManager());
        oldOrganization.setMfo(organization.getMfo());
        oldOrganization.setName(organization.getName());
        oldOrganization.setNameRu(organization.getNameRu());
        oldOrganization.setRegionId(organization.getRegionId());
        oldOrganization.setSubRegionId(organization.getSubRegionId());

        oldOrganization = organizationService.save(oldOrganization);

        return "redirect:" + AdminUrls.OrganizationList;
    }
    @RequestMapping(AdminUrls.OrganizationView)
    public String getOrganizationViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Organization organization = organizationService.getById(id);
        if (organization==null){
            return "redirect:" + AdminUrls.OrganizationList;
        }
        Type type = new TypeToken<List<Organization>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Organization,id);

        model.addAttribute("organization",organization);
        model.addAttribute("soato",soatoService.getById(organization.getRegionId()));
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return AdminTemplates.OrganizationView;
    }
}
