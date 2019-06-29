package uz.maroqand.ecology.cabinet.controller.expertise;

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
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.service.client.ClientAuditService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 26.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class ApplicantController {

    private final ClientService clientService;
    private final SoatoService soatoService;
    private final OpfService opfService;
    private final HelperService helperService;
    private final ClientAuditService clientAuditService;

    public ApplicantController(ClientService clientService, SoatoService soatoService, OpfService opfService, HelperService helperService, ClientAuditService clientAuditService) {
        this.clientService = clientService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.helperService = helperService;
        this.clientAuditService = clientAuditService;
    }

    @RequestMapping(value = ExpertiseUrls.ApplicantList)
    public String getApplicantListPage(Model model) {

        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("subRegions", soatoService.getSubRegions());
        model.addAttribute("applicantTypeList", ApplicantType.getApplicantTypeList());
        model.addAttribute("opfList", opfService.getOpfList());
        return ExpertiseTemplates.ApplicantList;
    }

    @RequestMapping(value = ExpertiseUrls.ApplicantListAjax,produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> appealUserListAjax(
            @RequestParam(name = "type", required = false) ApplicantType type,
            @RequestParam(name = "tin", required = false) String tin,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "opfId", required = false) Integer opfId,
            @RequestParam(name = "oked", required = false) String oked,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "subRegionId", required = false) Integer subRegionId,
            @RequestParam(name = "registrationDateBegin", required = false) String registrationDateBegin,
            @RequestParam(name = "registrationDateEnd", required = false) String registrationDateEnd,
            Pageable pageable
    ) {
        Date dateBegin = DateParser.TryParse(registrationDateBegin, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(registrationDateEnd,Common.uzbekistanDateFormat);

        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Page<Client> clientPage = clientService.findFiltered(type,tin,name,opfId,oked,regionId,subRegionId,dateBegin,dateEnd, pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", clientPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", clientPage.getTotalElements()); //Filtered elements

        List<Client> clientList = clientPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(clientList.size());

        for(Client client: clientList) {
            convenientForJSONArray.add(new Object[]{
                    client.getId(),//hidden
                    client.getType()!=null? helperService.getAppealType(client.getType().getId(),locale):"",
                    client.getName(),
                    client.getOpfId()!=null? helperService.getOpfName(client.getOpfId(),locale):"",
                    client.getTin(),
                    client.getOked(),
                    client.getRegionId()!=null? helperService.getSoatoName(client.getRegionId(),locale):"",
                    client.getSubRegionId()!=null? helperService.getSoatoName(client.getSubRegionId(),locale):"",
                    client.getCreatedAt()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ApplicantView)
    public String getApplicantViewPage(
            @RequestParam(name = "type") Integer id,
            Model model
    ) {
        Client client = clientService.getById(id);
        if(client==null){
            return "redirect:"+ExpertiseUrls.ApplicantView;
        }

        //TODO Client RegApplication.status=RegApplicationStatus.Approved
        model.addAttribute("applicantAuditList", clientAuditService.getByClientId(client.getId()));
        model.addAttribute("applicant", client);
        return ExpertiseTemplates.ApplicantView;
    }

}
