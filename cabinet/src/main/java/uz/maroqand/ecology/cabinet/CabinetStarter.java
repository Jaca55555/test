package uz.maroqand.ecology.cabinet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import uz.maroqand.ecology.cabinet.config.WebMVCConfigs;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.service.DocumentOrdersService;
import uz.maroqand.ecology.core.service.RegApplicationExcelService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Configuration
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ComponentScan("uz.maroqand.ecology")
@EnableJpaRepositories(
        basePackages = {"uz.maroqand.ecology.core.repository","uz.maroqand.ecology.docmanagement.repository"},
        repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EntityScan(basePackages = {"uz.maroqand.ecology.core.entity","uz.maroqand.ecology.docmanagement.entity"})
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
public class CabinetStarter { 

    private static final Logger logger = LogManager.getLogger(CabinetStarter.class);

    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CabinetStarter.class, args);
        CabinetStarter applicationStarter = applicationContext.getBean(CabinetStarter.class);
        System.out.println("======================== INITIALIZATION COMPLETED ========================");

        WebMVCConfigs webMVCConfigs = applicationContext.getBean(WebMVCConfigs.class);
        DatabaseMessageSource messageSource = (DatabaseMessageSource) webMVCConfigs.messageSource();
        HelperService.setTranslationsSource(messageSource);
        DocumentHelperService.setTranslationsSource(messageSource);

        DocumentOrdersService documentOrdersService = applicationContext.getBean(DocumentOrdersService.class);

        RegApplicationExcelService reportOnActivitiesService  = applicationContext.getBean(RegApplicationExcelService.class);
        documentOrdersService.registerPerformer(DocumentOrderType.RegApplication, reportOnActivitiesService);
        documentOrdersService.registerPerformer(DocumentOrderType.NonprofitType, reportOnActivitiesService);
        documentOrdersService.registerPerformer(DocumentOrderType.NonprofitName, reportOnActivitiesService);
        documentOrdersService.startWorkingOnOrders();



        ToastrService toastrService = applicationContext.getBean(ToastrService.class);
        toastrService.initialization();

//        NotificationService notificationService = applicationContext.getBean(NotificationService.class);
//        notificationService.initialization();

        logger.info("" +
                "\n" +
                "           _     _            _                                                 _          \n" +
                "  ___ __ _| |__ (_)_ __   ___| |_       ___  ___ ___        ___  ___ _ ____   _(_) ___ ___ \n" +
                " / __/ _` | '_ \\| | '_ \\ / _ \\ __|____ / _ \\/ __/ _ \\ _____/ __|/ _ \\ '__\\ \\ / / |/ __/ _ \\\n" +
                "| (_| (_| | |_) | | | | |  __/ ||_____|  __/ (_| (_) |_____\\__ \\  __/ |   \\ V /| | (_|  __/\n" +
                " \\___\\__,_|_.__/|_|_| |_|\\___|\\__|     \\___|\\___\\___/      |___/\\___|_|    \\_/ |_|\\___\\___|\n" +
                "                                                                                           \n" +
                " \n" +
                " Developed by O'tkirbek Boltayev, Akmal Sadullayev \n" +
                "");
    }

}
