package uz.maroqand.ecology.cabinet.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.core.dto.api.*;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationLogRepository;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.service.Bot;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    public static void sendEcoFonds(ConclusionService conclusionService,RegApplicationService regApplicationService,RegApplicationLogService regApplicationLogService,RestTemplate restTemplate,FileService fileService) throws IOException {
        List<RegApplication> regApplicationList = regApplicationService.findByDelivered();
        for(RegApplication regApplication:regApplicationList){
        logger.info("reg_application_id"+regApplication.getId());
            Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());

            if(conclusion!=null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                // This nested HttpEntiy is important to create the correct
                // Content-Disposition entry with metadata "name" and "filename"
                File file;
                byte[] input_file;
                String originalFileName;
                MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
                Set<Integer> materialsInt = regApplication.getMaterials();
                int next =materialsInt.size()>0? materialsInt.iterator().next():0;
                if (next == 8) {
                    if (conclusionService.getById(conclusion.getId()).getConclusionWordFileId() != null) {
                        file = fileService.findById(conclusionService.getById(regApplication.getConclusionId()).getConclusionWordFileId());
                        String filePath = file.getPath();
                        originalFileName = file.getName();
                        input_file = Files.readAllBytes(Paths.get(filePath + originalFileName));
                    } else {
                        logger.info("buyerda text fayl yuboradi");
                        String htmlText = conclusionService.getById(regApplication.getConclusionId()).getHtmlText();
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
                    body.add("file", fileEntity);
                    body.add("data", RegApplicationDTO.fromEntity(regApplication, conclusionService, fileService));
                    logger.info("body=="+body);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity =
                            new HttpEntity<>(body, headers);
                    try {

                        logger.info("yuborishga tayyor uje");
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


                    } catch (HttpServerErrorException e) {
                        logger.error("error="+e.getMessage(), e);
                        regApplication.setDeliveryStatus((short) 0);
                        regApplicationService.update(regApplication);
                        logger.error("data not send to Fond ");
                    }
                }
            }
        }

    }

    public static void recievePaymentFiles(PaymentFileService paymentFileService, InvoiceService invoiceService, PaymentService paymentService, RegApplicationService regApplicationService, OrganizationService organizationService){
        List<PaymentNew> paymentNewList = new LinkedList<>();
//        paymentNewList.add(new PaymentNew(new PaymentPayer(302084511, "\"MGA STAR GOLD\" Ma`suliyati cheklangan jamiyati"),new PaymentBank("20208000404932704001", "00978"), "405000000", "00978", 200934834, "\"MGA STAR GOLD\" Ma`suliyati cheklangan jamiyati", "20210000000118014007", "0000000092", "01.04.2022 16:46:27", "00668~Перечисляется 100% за проведение гос.экологич.экспертизы с-но Договор публичной оферты №3416-/22 от 08.02.2021г.", 4838421398L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(305766751, "\"Reyhan Plaza\"МЧЖ"),new PaymentBank("20208000500923501001", "00317"), "13500000", "00317", 200934834, "\"Reyhan Plaza\"МЧЖ", "20210000900118014117", "96", "01.04.2022 17:07:16", "00668 Давлат экологик экспертизасини утказиш учун тулов", 4838482650L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(305766751, "\"Reyhan Plaza\"МЧЖ"),new PaymentBank("20208000500923501001", "00317"), "13500000", "00317", 200934834, "\"Reyhan Plaza\"МЧЖ", "20210000900118014117", "96", "01.04.2022 17:07:11", "00668 Давлат экологик экспертизасини утказиш учун тулов", 4838482818L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200604817, "\"YALANG`OCH G`ISHT ZAVODI\"ООО"),new PaymentBank("20208000200416632001", "00444"), "13500000", "00444", 200934834, "\"YALANG`OCH G`ISHT ZAVODI\"ООО", "20210000100118014124", "0000000013", "01.04.2022 11:32:33", "00668Давлат экологик экспертизасини утказиш учун тулов", 4837103255L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200613118, "\"Самарканд кишлок курилиш лойихалаш институти\"  МЧ"),new PaymentBank("20208000800436336001", "00279"), "13500000", "00279", 200934834, "\"Самарканд кишлок курилиш лойихалаш институти\"  МЧ", "20210000900118014117", "0000000092", "01.04.2022 17:06:29", "00668Davlat ekologik ekspertizasini utkazish uchun tulov invoiys  77048254392376", 4838479816L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(308298153, "KAPITAL CNG"),new PaymentBank("20208000105360638001", "01074"), "202500000", "01074", 200934834, "KAPITAL CNG", "20210000400118014112", "11", "01.04.2022 16:12:22", "00668 Давлат экологик экспертизасини утказиш учун тулов Инвайс: 33694666915130", 4838325452L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(000000000, "OBIDJON KARIMOV"),new PaymentBank("29896000900001190013", "01190"), "13500000", "01190", 200934834, "OBIDJON KARIMOV", "20210000400118014112", "747728", "01.04.2022 15:14:01", "00667 инвойс 33314131764003 мансурбек рахмоновэкологик хулосаси учун", 4838071202L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(303146764, "OOO \"CARSTONE AMUSEMENT\""),new PaymentBank("20208000300419336001", "01071"), "13500000", "01071", 200934834, "OOO \"CARSTONE AMUSEMENT\"","20210000100118014120", "34", "01.04.2022 13:31:34", "00668 Инвойс 17636632824407 за проведение экологической экспертизы", 4837404600L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(201940992, "SPI AND OWT СП OOO"),new PaymentBank("20214000100673642001", "00407"), "202500000", "00407", 200934834, "SPI AND OWT СП OOO", "20210000100118014120", "343", "01.04.2022 14:49:54", "00668 Инвойс № 40374370972268 от 01.04.22г. за проведение гос. экологической экспертизы, согласно договора публичной оферты № 2993/22 от 01.04.2022г. Предоплата 100%.", 4837712998L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул"),new PaymentBank("29801000100000853599", "00853"), "13500000", "00853", 200934834, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул", "20210000100118014122", "65346156", "01.04.2022 12:34:40", "00668САМАНДАРОВ МАКСУД УРИНБАЕВИЧ 30846468878696 ХУЛОСА УЧУН", 4837259190L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул"),new PaymentBank("29801000100000853599", "00853"), "13500000", "00853", 200934834, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул", "20210000100118014122", "65346232","01.04.2022 12:34:40", "00668ЖАББОРОВ РАВШОНБЕК ИСМАЙИЛОВИЧ 98924715502484 ХУЛОСА УЧУН", 4837262121L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул"),new PaymentBank("29801000100000853599", "00853"), "13500000", "00853", 200934834, "Бошка холатларда жисмоний шахслардан (накд ёки накд пулсиз) кабул", "20210000100118014122", "65346297", "01.04.2022 12:34:40", "00668САБИРОВ ХУРМАТБЕК АМИНБАЕВИЧ 95541641047974 ХУЛОСА УЧУН", 4837263737L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "Бошка холатларда жисмоний шахслардан кабул килинган туловлар"),new PaymentBank("29801000600000854599", "00854"), "13500000", "00854", 200934834, "Бошка холатларда жисмоний шахслардан кабул килинган туловлар", "20210000100118014123", "65365250", "01.04.2022 16:44:52", "00652Ишдавлатов Сирожиддин Рахматуллаевичнинг 12705085168444 инвойс ракамга асосон экологик экспертиза учун тулови", 4838416511L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(207215726, "Накд пулда бир мартталик туловлар буйича хисоб китоблар"),new PaymentBank("29824000500000830693", "00830"), "13500000", "00830", 200934834, "Накд пулда бир мартталик туловлар буйича хисоб китоблар", "20210000100118014124", "0288226552", "01.04.2022 16:29:23", "00995ZAYNIDDINOV JAMOLIDDIN ZA EXPERTIZU INV 88026918002531", 4838376983L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(202610025, "ООО \"OGNEUPOR\""),new PaymentBank("20208000103902490001", "01144"), "405000000", "01144", 200934834, "ООО \"OGNEUPOR\"", "20210000100118014120", "2592", "01.04.2022 12:52:28", "00668 Оплата 100% за проведение экологической экспертизы согл.инвойса № 99991726191165", 4837317372L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(202610025, "ООО \"OGNEUPOR\""),new PaymentBank("20208000103902490001", "01144"), "405000000", "01144", 200934834, "ООО \"OGNEUPOR\"", "20210000100118014120", "2593", "01.04.2022 12:53:45", "00668 Оплата 100% за проведение экологической экспертизы согл.инвойса №26584841475413", 4837322550L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(308328787, "ООО \"STROY PROM TORG\" - основной депозитный счет до востребования"),new PaymentBank("20208000905365446001", "01095"), "202500000", "01095", 200934834, "ООО \"STROY PROM TORG\" - основной депозитный счет до востребования", "20210000100118014120", "17", "01.04.2022 15:44:42", "00668 Оплата 100% за проведение экологической экспертизы по инвойсу № 95364217605095 от 01.04.2022г", 4838223005L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "QIZIRIQ EKSPRESS SERVIS INVEST"),new PaymentBank("29801000200001045599", "01045"), "13500000", "01045", 200934834, "QIZIRIQ EKSPRESS SERVIS INVEST", "20210000900118014119", "65365468", "01.04.2022 17:21:32", "00599Sh Jo'rayev ekalogik ekspertizasini o'tlazish uchun to'lov 87893382281671", 4838520065L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068531703", "01.04.2022 11:39:37", "00668 59905954624878 га асосан  Саратов Элмуродга хулоса олиш учун тулов туланди ~85694813~860013***6206", 4837132727L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068531199", "01.04.2022 11:39:38", "00668 80375396296812 га асосан Элмуродова  Гулиранога хулоса олиш учун тулов туланди ~85694628~860013***6206", 4837132733L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068530749", "01.04.2022 11:39:38", "00668 61718044772584 га асосан Абдуллаева Ойгулга хулоса олиш учун тулов туланди ~85694491~860013***6206", 4837132735L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068532416", "01.04.2022 11:49:50", "00668 65314124145591 га асосан Чундилов Низомга хулоса олиш учун тулов туланди ~85695049~860013***6206", 4837171062L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068532143", "01.04.2022 11:49:50", "00668 46274452783038 га асосан Бекбудов Бахтиёрга хулоса олиш учун тулов туланди ~85694964~860013***6206", 4837171068L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014123", "0068531883", "01.04.2022 11:49:50", "00668 93302491534188 га асосан Нормуминов Хакимга хулоса олиш учун тулов туланди ~85694889~860013***6206", 4837171070L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200242936, "Разовые платежи Uzcard"),new PaymentBank("17409000100000083559", "00083"), "13500000", "00083", 200934834, "Разовые платежи Uzcard", "20210000100118014121", "0068524123", "01.04.2022 11:31:38", "00668 48158055426641 инвоисга асосан Уразаев Наилни экспертиза хулосаси учун ~85691793~860003***8396", 4837095232L));
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200829053, "ТОШКЕНТ Ш. АТ \"АЛОКАБАНК\" БОШ ОФИСИ"),new PaymentBank("17409000400000401248", "00401"), "13500000", "00401", 200934834, "ТОШКЕНТ Ш. АТ \"АЛОКАБАНК\" БОШ ОФИСИ", "20210000000118014118", "446449", "01.04.2022 11:47:56", "00634 Durdona Aziza Savdo MCHJ ning Ekologik ekspertiza xulosasi uchun to'lov 69691945880287 A.Tursunov", 4837155848L));
//
//        paymentNewList.add(new PaymentNew(new PaymentPayer(200833707, "Фергана ОАКБ Уз ПСБ - Бошка холатларда жис. шахс.дан (накд ёки накд"),new PaymentBank("29801000400000494599", "00494"), "13500000", "00494", 200934834, "Фергана ОАКБ Уз ПСБ - Бошка холатларда жис. шахс.дан (накд ёки накд", "20210000100118014121", "65367044", "01.04.2022 17:03:02", "00668Мадазимов Абдуллазим экология хулоса учун тулов 83564954603406", 4838449025L));
        for (PaymentNew paymentNew:paymentNewList){
            //create PaymentFile
            PaymentFile paymentFile = new PaymentFile();
            try {
                paymentFile.setPayerTin(paymentNew.getPayer().getTin());
                paymentFile.setPayerName(paymentNew.getPayer().getName());
                paymentFile.setBankAccount(paymentNew.getBank().getAccount());
                paymentFile.setBankMfo(paymentNew.getBank().getMfo());

                paymentFile.setReceiverInn(paymentNew.getReceiver_inn());
                paymentFile.setReceiverName(paymentNew.getReceiver_name());
                Integer accountLength= paymentNew.getReceiver_acc().length();
                if (accountLength>20){
                    paymentFile.setReceiverAccount(paymentNew.getReceiver_acc().substring(accountLength-20,accountLength));
                    paymentFile.setReceiverAdd(paymentNew.getReceiver_acc().substring(0,accountLength-20));
                }else if(accountLength==20){
                    paymentFile.setReceiverAccount(paymentNew.getReceiver_acc());
                }else{
                    paymentFile.setReceiverAdd(paymentNew.getReceiver_acc());
                }
                paymentFile.setReceiverMfo(paymentNew.getReceiver_mfo());

                paymentFile.setBankId(paymentNew.getId());
                try {
                    paymentFile.setAmount(Double.parseDouble(paymentNew.getAmount())/100);
                } catch (Exception ignored){}

                paymentFile.setAmountOriginal(paymentNew.getAmount());
                paymentFile.setDocumentNumber(paymentNew.getDocument_number());
                Date date = DateParser.TryParse(paymentNew.getPayment_date(), Common.uzbekistanDateAndTimeFormatBank);
                paymentFile.setPaymentDate(date);
                paymentFile.setDetails(paymentNew.getDetails());

                paymentFile = paymentFileService.create(paymentFile);


            }catch (Exception e){
                e.printStackTrace();
            }
            //get Invoice
            Invoice invoice = null;
            String invoiceStr = paymentFile.getDetails();
            String[] parts = invoiceStr.split(" ");
            for (String invoiceCheck : parts) {
                if(invoiceCheck.length()==14){
                    invoice = invoiceService.getInvoice(invoiceCheck);
                    if(invoice!=null) break;
                }
            }
            if(invoice!=null){
                RegApplication regApplication = regApplicationService.getTopByOneInvoiceId(invoice.getId());
                if (regApplication!=null) {
                    String account="";

                    try {
                        account = organizationService.getById(regApplication.getReviewId()).getAccount();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (!account.isEmpty() && paymentFile.getReceiverAccount().equals(account)) {
                        if(paymentFile.getPayerTin()!=null && regApplication.getApplicant()!=null && regApplication.getApplicant().getTin()!=null && regApplication.getApplicant().getTin().equals(paymentFile.getPayerTin())){
                            paymentFile.setInvoice(invoice.getInvoice());
                            Payment payment = paymentService.pay(invoice.getId(), paymentFile.getAmount(), new Date(), paymentFile.getDetails(), PaymentType.BANK);
                            paymentFile.setPaymentId(payment.getId());
                            paymentFileService.save(paymentFile);
                        }
                        invoiceService.checkInvoiceStatus(invoice);
                    }
                }
            }

        }

    }

    public static void closeModificationRegApplications( RegApplicationService regApplicationService){
                    regApplicationService.cancelModification();

    }
    public static void closeModificationTime(RegApplicationLogService regApplicationLogService,RegApplicationService regApplicationService){
//       regApplicationService.closeModificationTimer();
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
