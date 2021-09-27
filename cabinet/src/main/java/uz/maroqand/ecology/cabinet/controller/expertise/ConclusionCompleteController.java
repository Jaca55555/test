package uz.maroqand.ecology.cabinet.controller.expertise;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.dto.api.ResponseDTO;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.DocumentEditorService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Sadullayev Akmal on 05.10.2020.
 * (uz) Oxirgi kelishishuvdan keyingi xulosaga raqam va sana qo'yish
 * (ru)
 */
@Controller
public class ConclusionCompleteController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final SoatoService soatoService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final CommentService commentService;
    private final HelperService helperService;
    private final RegApplicationLogService regApplicationLogService;
    private final InvoiceService invoiceService;
    private final ProjectDeveloperService projectDeveloperService;
    private final CoordinateService coordinateService;
    private final ConclusionService conclusionService;
    private final NotificationService notificationService;
    private final SmsSendService smsSendService;
    private final DocumentEditorService documentEditorService;
    private final FileService fileService;
    private final RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService;
    private final RestTemplate restTemplate;
    private final RegApplicationService applicationService;

    @Autowired
    public ConclusionCompleteController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            CommentService commentService,
            HelperService helperService,
            RegApplicationLogService regApplicationLogService,
            InvoiceService invoiceService,
            ProjectDeveloperService projectDeveloperService,
            CoordinateService coordinateService,
            ConclusionService conclusionService,
            NotificationService notificationService,
            SmsSendService smsSendService,
            DocumentEditorService documentEditorService, FileService fileService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService, RestTemplate restTemplate, RegApplicationService applicationService) {
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.commentService = commentService;
        this.helperService = helperService;
        this.regApplicationLogService = regApplicationLogService;
        this.invoiceService = invoiceService;
        this.projectDeveloperService = projectDeveloperService;
        this.coordinateService = coordinateService;
        this.conclusionService = conclusionService;
        this.notificationService = notificationService;
        this.smsSendService = smsSendService;
        this.documentEditorService = documentEditorService;
        this.fileService = fileService;
        this.regApplicationCategoryFourAdditionalService = regApplicationCategoryFourAdditionalService;
        this.restTemplate = restTemplate;
        this.applicationService = applicationService;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionCompleteList)
    public String getConclusionConfirmListPage(Model model) {
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Denied);
        logStatusList.add(LogStatus.Approved);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
        return ExpertiseTemplates.ConclusionCompleteList;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionCompleteListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getConclusionCompleteListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        /*Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.AgreementComplete,
                null,
                null,
                null,//todo shart kerak
                pageable
        );*/

        System.out.println(filterDto);
        Page<RegApplicationLog> regApplicationLogPage = regApplicationLogService.findFiltered(
                filterDto,
                null,
                null,
                LogType.ConclusionComplete,
                null,
                pageable
        );

        List<RegApplicationLog> regApplicationLogList = regApplicationLogPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationLogList.size());
        for (RegApplicationLog agreementCompleteLog : regApplicationLogList){
            RegApplication regApplication = null;
            if (agreementCompleteLog.getRegApplicationId()!=null){
                regApplication = regApplicationService.getById(agreementCompleteLog.getRegApplicationId());
            }
            Client client =null;
            if (regApplication!=null && regApplication.getApplicantId()!=null){
                client = clientService.getById(regApplication.getApplicantId());
            }
            RegApplicationLog performerLog = null;
            if (regApplication!=null && agreementCompleteLog.getIndex()!=null){
                performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, agreementCompleteLog.getIndex());
            }
            convenientForJSONArray.add(new Object[]{
                    regApplication!=null?regApplication.getId():"",
                    client!=null?client.getTin():"",
                    client!=null?client.getName():"",
                    regApplication!=null && regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication!=null && regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication!=null && regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication!=null && regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    (performerLog!=null && performerLog.getStatus() != null ) ? helperService.getTranslation(performerLog.getStatus().getPerformerName(), locale):"",
                    (performerLog!=null && performerLog.getStatus() != null ) ? performerLog.getStatus().getId():"",
                    agreementCompleteLog.getStatus() !=null ? helperService.getTranslation(agreementCompleteLog.getStatus().getAgreementName(), locale):"",
                    agreementCompleteLog.getStatus() !=null ? agreementCompleteLog.getStatus().getId():"",
                    agreementCompleteLog.getId(),
                    regApplication!=null?regApplicationService.beforeOrEqualsTrue(regApplication):null
            });
        }

        result.put("recordsTotal", regApplicationLogPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationLogPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConclusionCompleteView)
    public String getConclusionCompleteViewPage(
            @RequestParam(name = "id")Integer logId,
            Model model
    ) {
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        Integer regApplicationId = regApplicationLog.getRegApplicationId();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConclusionCompleteList;
        }

        clientService.clientView(regApplication.getApplicantId(), model);
        coordinateService.coordinateView(regApplicationId, model);
        model.addAttribute("invoice",invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("conclusion", conclusionService.getByRegApplicationIdLast(regApplication.getId()));
        model.addAttribute("regApplicationLog",regApplicationLog);

        RegApplicationLog performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, regApplicationLog.getIndex());
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getAllByIndex(regApplication.getId(), LogType.Agreement, regApplicationLog.getIndex());

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = null;
        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        }
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("performerLog", performerLog);
        model.addAttribute("agreementLogList", agreementLogList);
        model.addAttribute("agreementCompleteLog", regApplicationLog);
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.ConclusionCompleteView;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionCompleteAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "number")String number,
            @RequestParam(name = "date")String dateStr
    ) throws IOException, DocumentException {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null || regApplication.getConclusionCompleteLogId() == null || regApplication.getPerformerLogId() == null){
            return "redirect:" + ExpertiseUrls.ConclusionCompleteList;
        }

        RegApplicationLog conclusionLog = regApplicationLogService.getById(regApplication.getConclusionCompleteLogId());
        if (conclusionLog==null){
            return "redirect:" + ExpertiseUrls.ConclusionCompleteList;
        }

        regApplicationLogService.update(conclusionLog, LogStatus.Approved, "", user.getId());

        RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
        switch (performerLog.getStatus()){
            case Modification: regApplication.setStatus(RegApplicationStatus.Modification); break;
            case Approved: regApplication.setStatus(RegApplicationStatus.Approved); break;
            case Denied: regApplication.setStatus(RegApplicationStatus.NotConfirmed); break;
        }
        regApplicationService.update(regApplication);

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        if (conclusion!=null){
            conclusion.setNumber(number);
            conclusion.setDate(DateParser.TryParse(dateStr,Common.uzbekistanDateFormat));
            conclusionService.save(conclusion);
            conclusionService.complete(conclusion.getId());
            documentEditorService.conclusionComplete(conclusion);
        }


        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "sys_notification.new",
                regApplication.getId(),
                "sys_notification_message.finished",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );

        notificationService.create(
                regApplication.getPerformerId(),
                NotificationType.Expertise,
                "sys_notification.performerInfo",
                regApplication.getId(),
                 "sys_notification_message.performer_confirm",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );


        //sendApi
        if(conclusion!=null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // This nested HttpEntiy is important to create the correct
            // Content-Disposition entry with metadata "name" and "filename"
            File file;
            byte[] input_file;
            String originalFileName;
            RegApplicationLog regApplicationLog = regApplicationLogService.getByRegApplcationIdAndType(regApplication.getId(), LogType.Performer);
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            Set<Integer> materialsInt = regApplication.getMaterials();
            Integer next = materialsInt.iterator().next();
            if (next == 8 && regApplicationLog.getStatus() == LogStatus.Approved) {
                if (conclusionService.getById(regApplication.getConclusionId()).getConclusionWordFileId() != null) {
                    file = fileService.findById(conclusionService.getById(regApplication.getConclusionId()).getConclusionWordFileId());
                    String filePath = file.getPath();
                    originalFileName = file.getName();
                    input_file = Files.readAllBytes(Paths.get(filePath + originalFileName));
                } else {
                    String htmlText = conclusionService.getById(regApplication.getConclusionId()).getHtmlText();
                    String XHtmlText = htmlText.replaceAll("&nbsp;", "&#160;");
                    java.io.File pdfFile = fileService.renderPdf(XHtmlText);
                    originalFileName = pdfFile.getName();
                    input_file = Files.readAllBytes(Paths.get(pdfFile.getAbsolutePath()));
                }


                ContentDisposition contentDisposition = ContentDisposition
                        .builder("form-data")
                        .name("file")
                        .filename(originalFileName)
                        .build();
                fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
                HttpEntity<byte[]> fileEntity = new HttpEntity<>(input_file, fileMap);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileEntity);
                body.add("data", RegApplicationDTO.fromEntity(regApplication, conclusionService, fileService));

                HttpEntity<MultiValueMap<String, Object>> requestEntity =
                        new HttpEntity<>(body, headers);
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            "http://172.16.11.234:8087/api/expertise",
                            HttpMethod.POST,
                            requestEntity,
                            String.class);
                    Boolean value = response.getStatusCode().is2xxSuccessful();
                    System.out.println(response);
                } catch (HttpClientErrorException e) {
                    e.printStackTrace();
                }
            }
        }

        //sendApi


        Client client = clientService.getById(regApplication.getApplicantId());
        smsSendService.sendSMS(client.getPhone(), " Arizangiz ko'rib chiqildi, ariza raqami ", regApplication.getId(), client.getName());




        return "redirect:"+ExpertiseUrls.ConclusionCompleteView + "?id=" + logId + "#action";
    }

}
