package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 23.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class RegApplicationMonitoringController {

    private final UserService userService;
    private final RegApplicationService regApplicationService;
    private final RegApplicationLogService regApplicationLogService;
    private final HelperService helperService;
    private final ClientService clientService;
    private final ChangeDeadlineDateService changeDeadlineDateService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final CommentService commentService;
    private final InvoiceService invoiceService;
    private final ProjectDeveloperService projectDeveloperService;


    public RegApplicationMonitoringController(UserService userService, RegApplicationService regApplicationService, RegApplicationLogService regApplicationLogService, HelperService helperService, ClientService clientService, ChangeDeadlineDateService changeDeadlineDateService, CoordinateRepository coordinateRepository, CoordinateLatLongRepository coordinateLatLongRepository, CommentService commentService, InvoiceService invoiceService, ProjectDeveloperService projectDeveloperService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.regApplicationLogService = regApplicationLogService;
        this.helperService = helperService;
        this.clientService = clientService;
        this.changeDeadlineDateService = changeDeadlineDateService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.commentService = commentService;
        this.invoiceService = invoiceService;
        this.projectDeveloperService = projectDeveloperService;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringList)
    public String expertiseRegApplicationMonitoringList() {

        return ExpertiseTemplates.ExpertiseRegApplicationMonitoringList;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringListAjax, produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> expertiseRegApplicationMonitoringListAjax(
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(new FilterDto(),null,null,null,user.getId(), null,pageable);
        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    regApplication.getInputType(),
                    helperService.getObjectExpertise(regApplication.getObjectId(),locale),
                    helperService.getMaterials(regApplication.getMaterials(),locale),
                    regApplication.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    regApplication.getStatus()!=null? helperService.getRegApplicationStatus(regApplication.getStatus().getId(),locale):"",
                    regApplication.getStatus()!=null? regApplication.getStatus().getColor():""

            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringView)
    public String viewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringList;
        }
        Client applicant = clientService.getById(regApplication.getApplicantId());
        switch (applicant.getType()){
            case Individual:
                model.addAttribute("individual", new IndividualDto(applicant)); break;
            case LegalEntity:
                model.addAttribute("legalEntity", new LegalEntityDto(applicant)) ;break;
            case ForeignIndividual:
                model.addAttribute("foreignIndividual", new ForeignIndividualDto(applicant)); break;
            case IndividualEnterprise:
                model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto(applicant)); break;
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }

        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("applicant", applicant);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));

        List<RegApplicationLog> regApplicationLogList = regApplicationLogService.getByRegApplicationId(regApplication.getId());
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLogList",regApplicationLogList);

        return ExpertiseTemplates.ExpertiseRegApplicationMonitoringView;
    }
}
