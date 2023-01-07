package uz.maroqand.ecology.cabinet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.SendingData;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SendingDataService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class RestEcoFondController {

    private static Logger logger = LogManager.getLogger(RestEcoFondController.class);

    private final RegApplicationService regApplicationService;
    private final ConclusionService conclusionService;
    private final SendingDataService sendingDataService;
    private final FileService fileService;
    private final RestTemplate restTemplate;
    private final RegApplicationLogService regApplicationLogService;



    public RestEcoFondController(RegApplicationService regApplicationService, ConclusionService conclusionService, SendingDataService sendingDataService, FileService fileService, RestTemplate restTemplate, RegApplicationLogService regApplicationLogService) {
        this.regApplicationService = regApplicationService;
        this.conclusionService = conclusionService;
        this.sendingDataService = sendingDataService;
        this.fileService = fileService;
        this.restTemplate = restTemplate;
        this.regApplicationLogService = regApplicationLogService;
    }


    @PostMapping("/send/eco-fond")
    public String sendEcoFond() throws IOException {
        List<RegApplication> regApplicationList = regApplicationService.findByDelivered();
        for(RegApplication regApplication:regApplicationList){
            logger.info("reg_application_id"+regApplication.getId());
            Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());

            if(conclusion!=null && conclusionService.getById(regApplication.getConclusionId()) != null) {
                logger.info("conclusionId:{}",conclusion.getId());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                // This nested HttpEntiy is important to create the correct
                // Content-Disposition entry with metadata "name" and "filename"
                File file=null;
                byte[] input_file;
                String originalFileName;
                MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
                Set<Integer> materialsInt = regApplication.getMaterials();
                int next =materialsInt.size()>0? materialsInt.iterator().next():0;
                RegApplicationLog regApplicationLog = regApplicationLogService.getByRegApplcationIdAndType(regApplication.getId(), LogType.Performer);
                logger.info("regApplicationLogStatus:{}",regApplicationLog.getStatus());
                if ((next == 8 || next == 5 || next == 6 || next == 7 ) && regApplicationLog.getStatus() == LogStatus.Approved && regApplication.getDeliveryStatus()==0) {
                    if (conclusionService.getById(conclusion.getId()).getConclusionWordFileId() != null) {
                        file = fileService.findById(conclusionService.getById(regApplication.getConclusionId()).getConclusionWordFileId());
                        String filePath = file.getPath();
                        originalFileName = file.getName();
                        input_file = Files.readAllBytes(Paths.get(filePath + originalFileName));
                    } else {
                        logger.info("buyerda text fayl yuboradi");
                        String htmlText = conclusionService.getById(regApplication.getConclusionId())!=null ? conclusionService.getById(regApplication.getConclusionId()).getHtmlText():"";
                        String XHtmlText = htmlText.replaceAll("&nbsp;", "&#160;");
                        java.io.File pdfFile = fileService.renderPdf(XHtmlText);
                        originalFileName = pdfFile.getName();
                        input_file = Files.readAllBytes(Paths.get(pdfFile.getAbsolutePath()));
                    }

                    logger.info("fileName="+originalFileName);
                    ContentDisposition contentDisposition = ContentDisposition
                            .builder("form-data")
                            .name("file")
                            .filename(originalFileName)
                            .build();
                    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, String.valueOf(contentDisposition));
                    HttpEntity<byte[]> fileEntity = new HttpEntity<>(input_file, fileMap);

                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    SendingData sendingData = new SendingData();
                    body.add("file", fileEntity);
                    body.add("data", RegApplicationDTO.fromEntity(regApplication, conclusionService, fileService));
                    sendingData.setDataSend(body.toString());
                    sendingData.setFileId(file!=null? file.getId(): null);
                    logger.info("body=="+body);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity =
                            new HttpEntity<>(body, headers);
                    try {

                        logger.info("yuborishga tayyor uje");
                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://84.54.83.68:8087/api/expertise",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        boolean value = response.getStatusCode().is2xxSuccessful();
                        System.out.println(response);
                        logger.info("data send to Fond ");
                        if(value){
                            regApplication.setDeliveryStatus((short) 1);
                            sendingData.setDeliveryStatus((short) 1);
                        }else{
                            regApplication.setDeliveryStatus((short) 0);
                            sendingData.setDeliveryStatus((short) 0);

                        }
                        regApplicationService.update(regApplication);
                        sendingData.setCreatedAt(new Date());
                        sendingData.setRegApplicationId(regApplication.getId());


                    } catch (HttpServerErrorException e) {
                        logger.error("error="+e.getMessage(), e);
                        regApplication.setDeliveryStatus((short) 0);
                        sendingData.setDeliveryStatus((short) 0);
                        sendingData.setCreatedAt(new Date());
                        sendingData.setRegApplicationId(regApplication.getId());
                        sendingData.setErrors(e.getMessage());
                        regApplicationService.update(regApplication);
                        logger.error("data not send to Fond ");
                    }
                    sendingDataService.save(sendingData);
                }
            }
        }

        return "mgmt/sending_data/list";
    }
}
