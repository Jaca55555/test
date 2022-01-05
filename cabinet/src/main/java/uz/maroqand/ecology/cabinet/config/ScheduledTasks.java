package uz.maroqand.ecology.cabinet.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.cabinet.util.Maintenance;
import uz.maroqand.ecology.docmanagement.service.Bot;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

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
    private final RegApplicationLogService regApplicationLogService;
    private final RequirementService requirementService;

    private Logger logger = LogManager.getLogger(ScheduledTasks.class);

    public ScheduledTasks(InvoiceService invoiceService, RegApplicationService regApplicationService, UserService userService, DocumentTaskSubService documentTaskSubService, Bot bot, RegApplicationLogService regApplicationLogService, RequirementService requirementService) {
        this.invoiceService = invoiceService;
        this.regApplicationService = regApplicationService;
        this.userService = userService;
        this.documentTaskSubService = documentTaskSubService;
        this.bot = bot;
        this.regApplicationLogService = regApplicationLogService;
        this.requirementService = requirementService;
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

    @Scheduled(cron = "0 0 9 * * *")
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
