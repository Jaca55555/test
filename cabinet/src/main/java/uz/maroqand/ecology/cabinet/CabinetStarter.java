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
import uz.maroqand.ecology.cabinet.config.WebMVCConfigs;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Configuration
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ComponentScan("uz.maroqand.ecology")
@EnableJpaRepositories(
        basePackages = {"uz.maroqand.ecology.core.repository","uz.maroqand.ecology.docmanagment.repository"},
        repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EntityScan(basePackages = {"uz.maroqand.ecology.core.entity","uz.maroqand.ecology.docmanagment.entity"})
@EnableCaching(proxyTargetClass = true)
public class CabinetStarter {

    private static final Logger logger = LogManager.getLogger(CabinetStarter.class);

    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CabinetStarter.class, args);
        CabinetStarter applicationStarter = applicationContext.getBean(CabinetStarter.class);
        System.out.println("======================== INITIALIZATION COMPLETED ========================");

        WebMVCConfigs webMVCConfigs = applicationContext.getBean(WebMVCConfigs.class);
        DatabaseMessageSource messageSource = (DatabaseMessageSource) webMVCConfigs.messageSource();
        HelperService.setTranslationsSource(messageSource);

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
