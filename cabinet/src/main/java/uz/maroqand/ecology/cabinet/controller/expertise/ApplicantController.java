package uz.maroqand.ecology.cabinet.controller.expertise;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.ConclusionStatus;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.dto.gnk.GnkResponseObject;
import uz.maroqand.ecology.core.dto.gnk.GnkRootResponseObject;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientAuditService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.gnk.GnkService;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
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
    private final RegApplicationService regApplicationService;
    private final InvoiceService invoiceService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final GnkService gnkService;
    private final ConclusionService conclusionService;
    private final DocumentRepoService documentRepoService;

    private static final Logger logger = LogManager.getLogger(ApplicantController.class);

    public ApplicantController(ClientService clientService, SoatoService soatoService, OpfService opfService, HelperService helperService, ClientAuditService clientAuditService, RegApplicationService regApplicationService, InvoiceService invoiceService, CoordinateRepository coordinateRepository, CoordinateLatLongRepository coordinateLatLongRepository, GnkService gnkService, ConclusionService conclusionService, DocumentRepoService documentRepoService) {
        this.clientService = clientService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.helperService = helperService;
        this.clientAuditService = clientAuditService;
        this.regApplicationService = regApplicationService;
        this.invoiceService = invoiceService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.gnkService = gnkService;
        this.conclusionService = conclusionService;
        this.documentRepoService = documentRepoService;
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
        tin=StringUtils.trimToNull(tin);
        name=StringUtils.trimToNull(name);
        oked=StringUtils.trimToNull(oked);
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
                    client.getType()!=null? helperService.getApplicantType(client.getType().getId(),locale):"",
                    client.getName(),
                    client.getOpfId()!=null? helperService.getOpfName(client.getOpfId(),locale):"",
                    client.getTin(),
                    client.getOked(),
                    client.getRegionId()!=null? helperService.getSoatoName(client.getRegionId(),locale):"",
                    client.getSubRegionId()!=null? helperService.getSoatoName(client.getSubRegionId(),locale):"",
                    client.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(client.getCreatedAt()):""
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ApplicantView)
    public String getApplicantViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {

        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Client client = clientService.getById(id);
        if(client==null){
            return "redirect:"+ExpertiseUrls.ApplicantView;
        }
        if(client.getType().equals(ApplicantType.Individual)){
            model.addAttribute("individual", new IndividualDto(client));
        }else {
            model.addAttribute("legalEntity", new LegalEntityDto(client));
        }
        List<RegApplication> regApplicationList = regApplicationService.getByClientId(client.getId());
        List<Object[]> contractList = new ArrayList<>();
        List<Object[]> conclusionListObjects = new ArrayList<>();
        int index = 0;
        int indexConclusion = 0;
        for (RegApplication regApplication : regApplicationList){
            Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            List<Conclusion> conclusionList = conclusionService.getByRegApplicationId(regApplication.getId());
            for (Conclusion conclusion:conclusionList) {
                RegApplication conclusionRegApplication = conclusion.getRegApplication();
                conclusionListObjects.add(new Object[]{
                    ++indexConclusion,
                    conclusion.getNumber(),
                    conclusion.getDate() != null ? Common.uzbekistanDateFormat.format(conclusion.getDate()) : "",
                     regApplication.getObjectId() != null ? helperService.getObjectExpertise(regApplication.getObjectId(),locale) : "",
                    regApplication.getCategory() != null ? helperService.getTranslation(regApplication.getCategory().getName(),locale) : "",
                    regApplication.getPerformerId()!=null ? helperService.getUserFullNameById(regApplication.getPerformerId()) : "",
                    ExpertiseUrls.ApplicantConclusionView + "?id=" + conclusion.getId()
                });
            }

            if (regApplication.getStatus() == RegApplicationStatus.Approved){
                contractList.add(new Object[]{
                    ++index,
                    regApplication.getContractNumber(),
                    regApplication.getContractDate(),
                    invoice.getQty(),
                    invoice.getAmount(),
                    invoice.getClosedDate(),
                    invoice.getInvoice(),
                    regApplication.getContractFiles()
                });
            }
        }

        /*Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }*/


        model.addAttribute("conclusionList",conclusionListObjects);
        model.addAttribute("contractList",contractList);
        //TODO Client RegApplication.status=RegApplicationStatus.Approved
        model.addAttribute("applicantAuditList", clientAuditService.getByClientId(client.getId()));
        model.addAttribute("applicant", client);
        return ExpertiseTemplates.ApplicantView;
    }

    @RequestMapping(ExpertiseUrls.ApplicantConclusionView)
    public String conclusionView(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Conclusion conclusion = conclusionService.getById(id);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        if(conclusion == null || conclusion.getStatus().equals(ConclusionStatus.Initial)){
            return "redirect:" + ExpertiseUrls.ApplicantList;
        }
        RegApplication regApplication = null;
        if(conclusion.getRegApplicationId() != null){
            regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
        }
        if(conclusion.getDocumentRepoId() != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }
        model.addAttribute("conclusion", conclusion);
        model.addAttribute("regApplication", regApplication);

        return ExpertiseTemplates.ConclusionView;
    }

    @RequestMapping(value = ExpertiseUrls.ApplicantUpdateTax)
    public String updateTax() {
        Gson gson = new Gson();
        int i = 0;
        do {
            PageRequest pageRequest = new PageRequest(i, 100, Sort.Direction.ASC, "id");
            Page<Client> clientPage = clientService.findFiltered(null,null,null,null,null,null,null,null,null, pageRequest);
            for (Client client : clientPage) {
                try {
                    if (client.getName() == null && client.getTin() != null) {

                        logger.info("tin="+client.getTin());
                        GnkResponseObject gnkResponseObject = gnkService.getLegalEntityByTin(client.getTin());
                        logger.info("gnkResponseObject="+ gson.toJson(gnkResponseObject) );
                        if(gnkResponseObject.getRoot().size()>0){
                            GnkRootResponseObject object = gnkResponseObject.getRoot().get(0);

                            client.setName(object.getName());
                            client.setRegionId(Integer.parseInt(object.getObl_code()));
                            client.setSubRegionId(object.getSubRegioId());
                            client.setOpfId(object.getOpfId());
                            client.setDirectorFullName(object.getDirector());
                            client.setOked(object.getOked());
                            client.setPhone(object.getPhone());
                            client.setEmail(object.getEmail());
                            client.setAddress(object.getAddress());

                            client.setMfo(object.getMfo());
                            client.setBankAccount(object.getAccount());
                            client.setBankName(object.getBankname());

                            clientService.updateGnk(client);
                        }
                    }
                }catch (Exception e){ e.printStackTrace(); }
            }
            if (!clientPage.hasNext()) {
                if (i > 0) {
                    i = 0;
                    continue;
                }
                break;
            }
            i++;
        }
        while (true);


        return "redirect:"+ExpertiseUrls.ApplicantList;
    }
}
