package uz.maroqand.ecology.cabinet.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.util.Maintenance;

/**
 * Created by Sadullayev Akmal on 04.05.2020.
 * (uz)
 * (ru)
 */
@Component
public class ScheduledTasks {

    private final InvoiceService invoiceService;
    private final RegApplicationService regApplicationService;

    private Logger logger = LogManager.getLogger(ScheduledTasks.class);

    public ScheduledTasks(InvoiceService invoiceService, RegApplicationService regApplicationService) {
        this.invoiceService = invoiceService;
        this.regApplicationService = regApplicationService;
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



}
