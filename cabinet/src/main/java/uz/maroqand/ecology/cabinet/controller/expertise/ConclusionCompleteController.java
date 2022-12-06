package uz.maroqand.ecology.cabinet.controller.expertise;

import com.lowagie.text.DocumentException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.dto.didox.*;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.SendingData;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.*;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private final DocumentRepoService documentRepoService;


    private final SendingDataService sendingDataService;

    private static final Logger logger = LogManager.getLogger(ConclusionCompleteController.class);

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
            DocumentEditorService documentEditorService, FileService fileService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService, RestTemplate restTemplate, RegApplicationService applicationService, DocumentRepoService documentRepoService, SendingDataService sendingDataService) {
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
        this.documentRepoService = documentRepoService;
        this.sendingDataService = sendingDataService;
    }

    @RequestMapping(value = ExpertiseUrls.ConclusionCompleteList)
    public String getConclusionConfirmListPage(Model model) {
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Denied);
        logStatusList.add(LogStatus.Approved);
        logStatusList.add(LogStatus.Resend);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
        model.addAttribute("regApplicationCategoryType", Category.getCategoryList());
        return ExpertiseTemplates.ConclusionCompleteList;
    }

    @RequestMapping(value = ExpertiseUrls.CheckConclusionNumber, produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getUsernameCheck(
            @RequestParam(name = "docRegNumber", defaultValue = "", required = false) String regNumber,
            @RequestParam(name = "id", required = false) Integer documentId
    ) {
        System.out.println("username="+regNumber + " userId=" + documentId);
        Conclusion conclusion1=null;
        if (documentId!=null){
            conclusion1 = conclusionService.getById(documentId);
        }
        HashMap<String, Object> result = new HashMap<>();
        Integer nameStatus ;

        Conclusion document = conclusionService.findByConclusionNumber(regNumber);
        System.out.println("document1"+conclusion1);
        if(document==null){
            nameStatus = 0;
        }else {
            nameStatus = 1;
        }


        result.put("nameStatus", nameStatus);
        result.put("username", regNumber);
        return result;
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
        FilterDto filterDtoCount = new FilterDto();
        filterDto.setStatusing(1);
        result.put("initials", regApplicationLogService.findFilteredNumber(LogType.ConclusionComplete, null));
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
    ) throws IOException, DocumentException, ParserConfigurationException, SAXException {
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


//        notificationService.create(
//                regApplication.getCreatedById(),
//                NotificationType.Expertise,
//                "sys_notification.new",
//                regApplication.getId(),
//                "sys_notification_message.finished",
//                "/reg/application/resume?id=" + regApplication.getId(),
//                user.getId()
//        );
//
//        notificationService.create(
//                regApplication.getPerformerId(),
//                NotificationType.Expertise,
//                "sys_notification.performerInfo",
//                regApplication.getId(),
//                 "sys_notification_message.performer_confirm",
//                "/reg/application/resume?id=" + regApplication.getId(),
//                user.getId()
//        );

        //for didox //todo bu yerda didox qilinyapti
        //product
        Product product = new Product();
        product.setOrdno(1);
        product.setLgotaid("");
        product.setCommittentname("");
        product.setCommittenttin("");
        product.setCommittentvatregcode("");
        product.setName("qwrty");
        product.setCatalogcode("04902999");
        product.setCatalogname("Газеты, журналы и прочие периодические издания, иллюстрированные или неиллюстрированные, содержащие или не содержащие рекламный материал : ДРУГИЕ");
        product.setBarcode("");
        product.setMeasureid("1");
        product.setCount(0);
        product.setSumma("1000");
        product.setDeliverysum("100");
        product.setWithoutvat(true);
        //product end
        //Factura document
        Facturadoc facturadoc = new Facturadoc();
        facturadoc.setFacturano("DEV TEST issues 2");
        facturadoc.setFacturadate("2021-05-07");
        //Factura document end

        //ContractDoc
        Contractdoc contractdoc = new Contractdoc();
        contractdoc.setContractdate("2021-05-07");
        contractdoc.setContractno("DEV TEST issues 2");
        //ContractDoc end
        //Seller
        Seller seller = new Seller();
        seller.setName("WEBMEDIA INFORMATION");
        seller.setVatregcode("326020089828");
        seller.setAccount("29802000000001088001");
        seller.setBankid("01075");
        seller.setAddress("Яккасарайский рн, ул.Шото-Руставели , 53-Б ");
        seller.setDirector("РУководитель");
        seller.setAccountant("Гл бух");
        //Seller end
        //Buyer
        Buyer buyer = new Buyer();
        buyer.setName("WEBMEDIA INFORMATION");
        buyer.setBranchcode("");
        buyer.setBranchname("");
        buyer.setVatregcode("326020089828");
        buyer.setAccount("20208000400308125001");
        buyer.setBankid("00974");
        buyer.setAddress("Toshkent shahri, Yakkasaroy tumani, Sh.Rustaveli ko'chasi, 53b-uy");
        //Buyer end
        Productlist productList = new Productlist();
        ArrayList products = new ArrayList();
        productList.setProducts(products);
        productList.setTin("207119963");
        DocumentJson json  = new DocumentJson();
        json.setProductlist(productList);
        json.setBuyertin("302936161");
        json.setBuyer(buyer);
        json.setSeller(seller);
        json.setContractdoc(contractdoc);
        json.setFacturadoc(facturadoc);
        json.setContractdoc(contractdoc);

        HttpHeaders headersDidox = new HttpHeaders();
        headersDidox.setContentType(MediaType.APPLICATION_JSON);
        headersDidox.add("user_key", regApplicationService.getUserKey());
        MultiValueMap<String, Object> bodyDidox = new LinkedMultiValueMap<>();
        bodyDidox.add("Version", 1);
        bodyDidox.add("didoxcontractid", "");
        bodyDidox.add("FacturaType", 0);
        bodyDidox.add("ProductList", productList);
        bodyDidox.add("FacturaDoc",facturadoc);
        bodyDidox.add("ContractDoc",contractdoc);
        bodyDidox.add("Buyer", buyer);
        bodyDidox.add("Seller", seller);
        bodyDidox.add("SellerTin",207119963);
        HttpEntity<MultiValueMap<String, Object>> requestEntityDidox =
                new HttpEntity<>(bodyDidox, headersDidox);
        logger.info("requestEntityDidox"+requestEntityDidox);

        RestTemplate restTemplateDidox = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(bodyDidox,headersDidox);
        ResponseEntity<Object> responseDidox = null;

        try {
//            response = restTemplate.exchange("https://register.soliq.uz/reestr-api/create", HttpMethod.POST, request, Object.class);
            responseDidox = restTemplateDidox.exchange("https://stage.didox.uz/v1/documents/001/create", HttpMethod.POST, request, Object.class);
        } catch (Exception e) {
        }
        System.out.println(responseDidox);

        //for didox //todo bu yerda didox qilinyapti

        //sendApi
//        if(conclusion!=null) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            // This nested HttpEntiy is important to create the correct
//            // Content-Disposition entry with metadata "name" and "filename"
//            File file = null;
//            byte[] input_file;
//            String originalFileName;
//            RegApplicationLog regApplicationLog = regApplicationLogService.getByRegApplcationIdAndType(regApplication.getId(), LogType.Performer);
//            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
//            Set<Integer> materialsInt = regApplication.getMaterials();
//            Integer next =materialsInt.size()>0? materialsInt.iterator().next():0;
//            if ((next == 8 || next == 5 || next == 6 || next == 7 ) && regApplicationLog.getStatus() == LogStatus.Approved && regApplication.getDeliveryStatus()==null) {
//                if (conclusionService.getById(conclusion.getId()).getConclusionWordFileId() != null) {
//                    file = fileService.findById(conclusionService.getById(regApplication.getConclusionId()).getConclusionWordFileId());
//                    String filePath = file.getPath();
//                    originalFileName = file.getName();
//                    input_file = Files.readAllBytes(Paths.get(filePath + originalFileName));
//                } else {
//                    StringBuilder sb = new StringBuilder(conclusionService.getById(regApplication.getConclusionId()).getHtmlText());
//
//                    byte[] byteImage = documentRepoService.getQRImage(conclusion.getDocumentRepoId());
//                    sb.append("<img src=\"data:image/png;base64,");
//                    sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(byteImage, false)));
//                    sb.append("\">");
//
//                    String XHtmlText = sb.toString().replaceAll("&nbsp;", "&#160;");
//                    java.io.File pdfFile = fileService.renderPdf(XHtmlText);
//                    originalFileName = pdfFile.getName();
//                    input_file = Files.readAllBytes(Paths.get(pdfFile.getAbsolutePath()));
//                    file = fileService.filesave(pdfFile, user);
//
//                }
//                ContentDisposition contentDisposition = ContentDisposition
//                        .builder("form-data")
//                        .name("file")
//                        .filename(originalFileName)
//                        .build();
//                fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
//                HttpEntity<byte[]> fileEntity = new HttpEntity<>(input_file, fileMap);
//                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
////                Set<File> documentFiles = regApplication.getDocumentFiles();
////                for(File file1:documentFiles){
////                    HttpEntity<byte[]> documentFile = new HttpEntity<>(Files.readAllBytes(Paths.get(file1.getPath())), fileMap);
////                    body.add("document",documentFile);
////                }
//
//
//                SendingData sendingData = new SendingData();
//                body.add("file", fileEntity);
//                body.add("data", RegApplicationDTO.fromEntity(regApplication, conclusionService, fileService));
//                sendingData.setDataSend(body.toString());
//                sendingData.setFileId(file!=null? file.getId(): null);
//                HttpEntity<MultiValueMap<String, Object>> requestEntity =
//                        new HttpEntity<>(body, headers);
//                logger.info("response_entity"+requestEntity);
//                try {
//                    ResponseEntity<String> response = restTemplate.exchange(
//                            "http://84.54.83.68:8087/api/expertise",
//                            HttpMethod.POST,
//                            requestEntity,
//                            String.class);
//                    boolean value = response.getStatusCode().is2xxSuccessful();
//                    System.out.println(response);
//                    logger.info("data send to Fond ");
//                    if(value){
//                        regApplication.setDeliveryStatus((short) 1);
//                        sendingData.setDeliveryStatus((short) 1);
//                    }else{
//                        regApplication.setDeliveryStatus((short) 0);
//                        sendingData.setDeliveryStatus((short) 0);
//                    }
//                    regApplicationService.update(regApplication);
//                    sendingData.setCreatedAt(new Date());
//                    sendingData.setRegApplicationId(regApplication.getId());
//                } catch (ResourceAccessException e) {
//                    regApplication.setDeliveryStatus((short) 0);
//                    sendingData.setDeliveryStatus((short) 0);
//                    sendingData.setCreatedAt(new Date());
//                    sendingData.setRegApplicationId(regApplication.getId());
//                    sendingData.setErrors(e.getMessage());
//                    regApplicationService.update(regApplication);
//                    logger.error("data not send to Fond ");
//                }
//                sendingDataService.save(sendingData);
//            }
//        }

        //sendApi


        Client client = clientService.getById(regApplication.getApplicantId());
        smsSendService.sendSMS(client.getPhone(), " Arizangiz ko'rib chiqildi, ariza raqami ", regApplication.getId(), client.getName());




        return "redirect:"+ExpertiseUrls.ConclusionCompleteView + "?id=" + logId + "#action";
    }

}
