package uz.maroqand.ecology.ecoexpertise;

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
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.ecoexpertise.config.WebMVCConfigs;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Configuration
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ComponentScan("uz.maroqand.ecology")
@EnableJpaRepositories(basePackages = "uz.maroqand.ecology.core.repository", repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EntityScan(basePackages = {"uz.maroqand.ecology.core.entity"})
@EnableCaching(proxyTargetClass = true)
public class EcoExpertiseStarter {

    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EcoExpertiseStarter.class, args);
        EcoExpertiseStarter applicationStarter = applicationContext.getBean(EcoExpertiseStarter.class);
        System.out.println("======================== INITIALIZATION COMPLETED ========================");

        WebMVCConfigs webMVCConfigs = applicationContext.getBean(WebMVCConfigs.class);
        DatabaseMessageSource messageSource = (DatabaseMessageSource) webMVCConfigs.messageSource();
        HelperService.setTranslationsSource(messageSource);

        ToastrService toastrService = applicationContext.getBean(ToastrService.class);
        toastrService.initialization();

    }

}
