package uz.maroqand.ecology.cabinet.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.service.Bot;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 21.06.2018.
 * (uz)
 * (ru)
 */
public class Maintenance {

    private static Logger logger = LogManager.getLogger(ScheduledTask.class);

    public static void removeInvoiceAndRemoveApplication(InvoiceService invoiceService, RegApplicationService regApplicationService){

        List<Invoice> invoiceList = invoiceService.getListByStatus(InvoiceStatus.Initial);

        for (Invoice invoice:invoiceList) {
            if (invoice.getCreatedDate()!=null){
                Date createdDate = invoice.getCreatedDate();
                Calendar c = Calendar.getInstance();
                Date date = new Date();
                c.setTime(date);
                c.add(Calendar.DATE,-31);    // shu kunning o'zi ham qo'shildi
                Date expireDate = c.getTime();
//                Invoice yaratilganiga 90 kundan oshgan
                if (createdDate.before(expireDate)){
                    invoiceService.cancelInvoice(invoice);
                    regApplicationService.cancelApplicationByInvoiceId(invoice.getId());;
                }
            }
        }
    }
    public static  void deleteDuplicates(RegApplicationLogService regApplicationLogService){
        List<RegApplicationLog> regApplicationLogList = regApplicationLogService.findAll();
        for(RegApplicationLog regApplicationLog:regApplicationLogList){


        }
    }

    public static void sendRegApplicationNotDeliver(Integer regApplicationId, RegApplicationLogService regApplicationLogService,RegApplicationService regApplicationService, ConclusionService conclusionService, FileService fileService, RestTemplate restTemplate) throws IOException {
            RegApplication regApplication = regApplicationService.getById(regApplicationId);
            Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());

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
            Integer next =materialsInt.size()>0? materialsInt.iterator().next():0;
            if (next == 8 && regApplicationLog.getStatus() == LogStatus.Approved && regApplication.getDeliveryStatus()==null) {
                if (conclusionService.getById(conclusion.getId()).getConclusionWordFileId() != null) {
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
                    boolean value = response.getStatusCode().is2xxSuccessful();
                    System.out.println(response);
                    logger.info("data send to Fond ");
                    if(value){
                        regApplication.setDeliveryStatus((short) 1);
                    }else{
                        regApplication.setDeliveryStatus((short) 0);

                    }
                    regApplicationService.update(regApplication);


                } catch (ResourceAccessException e) {
                    regApplication.setDeliveryStatus((short) 0);
                    regApplicationService.update(regApplication);
                    logger.error("data not send to Fond ");
                }
            }


    };




    public static void createInvoiceForModificationRegApplications(RegApplicationLogService regApplicationLogService, RegApplicationService regApplicationService, InvoiceService invoiceService, RequirementService requirementService){

//        List<RegApplicationLog> regApplicationLogList = regApplicationLogService.getByLogStatus(LogStatus.Modification);
//        RegApplication regApplication = regApplicationService.getById(regApplicationLogList.get(0).getRegApplicationId());
//        Requirement requirement = requirementService.getById(regApplication.getRequirementId());
//
////        RegApplicationLog regApplicationLog = regApplicationLogService.getByRegApplcationId(Reg)
//        RegApplicationLog firstRegApplicationLog = regApplicationLogList.get(0);
//        Date createdDate = firstRegApplicationLog.getCreatedAt();
//        Calendar c = Calendar.getInstance();
//        Date date = new Date();
//        c.setTime(date);
//        c.add(Calendar.DATE,-61);    // shu kunning o'zi ham qo'shildi
//        Date expireDate = c.getTime();
//        if(createdDate.before(expireDate)){
//            invoiceService.create(regApplication,requirement);
//        }
    }


    public static void sendAllDocumentCount(UserService userService, DocumentTaskSubService documentTaskSubService, Bot bot) {
        List<User> userList = userService.getAllByTelegramUsers();
        for (User user:userList) {
            bot.sendMsg(user.getTelegramUserId(),documentTaskSubService.getMessageText(user.getTelegramUserId(), SendQueryType.NewDocument));
        }
    }
        public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        c.add(Calendar.DAY_OF_MONTH, 3);
        Date date = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date date1 = c.getTime();

        System.out.println(date);
        System.out.println(date1);
    }



    
}
