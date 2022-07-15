package uz.maroqand.ecology.cabinet.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
import uz.maroqand.ecology.cabinet.util.Maintenance;
import uz.maroqand.ecology.docmanagement.service.Bot;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import java.io.IOException;

/**
 * Created by Sadullayev Akmal on 04.05.2020.
 * (uz)
 * (ru)
 */
@Component
public class ScheduledTasks {

    private final InvoiceService invoiceService;
    private final RegApplicationService regApplicationService;
    private final UserService userService;
    private final DocumentTaskSubService documentTaskSubService;
    private final Bot bot;
    private final PaymentService paymentService;
    private final OrganizationService organizationService;
    private final RegApplicationRepository regApplicationRepository;
    private final PaymentFileService paymentFileService;
    private final RegApplicationLogService regApplicationLogService;
    private final RequirementService requirementService;
    private final ConclusionService conclusionService;
    private final FileService fileService;
    private final RestTemplate restTemplate;

    private Logger logger = LogManager.getLogger(ScheduledTasks.class);

    public ScheduledTasks(InvoiceService invoiceService, RegApplicationService regApplicationService, UserService userService, DocumentTaskSubService documentTaskSubService, Bot bot, PaymentService paymentService, OrganizationService organizationService, RegApplicationRepository regApplicationRepository, PaymentFileService paymentFileService, RegApplicationLogService regApplicationLogService, RequirementService requirementService, ConclusionService conclusionService, FileService fileService, RestTemplate restTemplate) {
        this.invoiceService = invoiceService;
        this.regApplicationService = regApplicationService;
        this.userService = userService;
        this.documentTaskSubService = documentTaskSubService;
        this.bot = bot;
        this.paymentService = paymentService;
        this.organizationService = organizationService;
        this.regApplicationRepository = regApplicationRepository;
        this.paymentFileService = paymentFileService;
        this.regApplicationLogService = regApplicationLogService;
        this.requirementService = requirementService;
        this.conclusionService = conclusionService;
        this.fileService = fileService;
        this.restTemplate = restTemplate;
    }


    @Scheduled(cron = "0 1 18 * * *")
    public void removeInvoice() {
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK STARTED      /\n" +
                "/*****************************/\n"
        );
        System.out.println("--removeInvoice--");
        Maintenance.removeInvoiceAndRemoveApplication(invoiceService,regApplicationService);

        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK FINISHED     /\n" +
                "/*****************************/\n"
        );

    }

    @Scheduled(cron = "0 25 15 * * *")
    public void sendEcoFonds() throws IOException {
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK STARTED      /\n" +
                "/*****************************/\n"
        );

        Maintenance.sendEcoFonds(conclusionService,regApplicationService,regApplicationLogService,restTemplate, fileService);

        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK FINISHED     /\n" +
                "/*****************************/\n"
        );

    }


    @Scheduled(cron = "0 50 18 * * *")
    public void cancelModification() {
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK STARTED      /\n" +
                "/*****************************/\n"
        );
        Maintenance.closeModificationRegApplications(regApplicationService);
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK FINISHED     /\n" +
                "/*****************************/\n"
        );

    }

    @Scheduled(cron = "0 0 20 * * *")
    public void cancelModificationTimer() {
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK STARTED      /\n" +
                "/*****************************/\n"
        );

        Maintenance.closeModificationTime(regApplicationLogService,regApplicationService);
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK FINISHED     /\n" +
                "/*****************************/\n"
        );

    }

//    @Scheduled(cron = "0 40 13 * * *")
//    public void sendRegApplicationNotDeliver() throws IOException {
//        logger.info("\n" +
//                "/*****************************/\n" +
//                "/       DELIVER TASK STARTED      /\n" +
//                "/*****************************/\n"
//        );
//        System.out.println("--removeInvoice--");
//        Maintenance.sendRegApplicationNotDeliver(20993,regApplicationLogService,regApplicationService,conclusionService,fileService,restTemplate);
//
//        logger.info("\n" +
//                "/*****************************/\n" +
//                "/       DELIVER TASK FINISHED     /\n" +
//                "/*****************************/\n"
//        );P
//
//    }
//
//    @Scheduled(cron = "0 1 20 * * *")
//    public void createInvoiceForModificationRegApplications() {
//        logger.info("\n" +
//                "/*****************************/\n" +
//                "/       CRON TASK STARTED      /\n" +
//                "/*****************************/\n"
//        );
//        Maintenance.createInvoiceForModificationRegApplications(regApplicationLogService,regApplicationService,invoiceService,requirementService);
//
//        logger.info("\n" +
//                "/*****************************/\n" +
//                "/       CRON TASK FINISHED     /\n" +
//                "/*****************************/\n"
//        );
//
//    }

    @Scheduled(cron = "0 0 14 * * *")
    public void sendDocumentCount() {
        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK STARTED      /\n" +
                "/*****************************/\n"
        );
        System.out.println("--sendTelegramMessage--");
        Maintenance.sendAllDocumentCount(userService,documentTaskSubService,bot);

        logger.info("\n" +
                "/*****************************/\n" +
                "/       CRON TASK FINISHED     /\n" +
                "/*****************************/\n"
        );

    }



}
