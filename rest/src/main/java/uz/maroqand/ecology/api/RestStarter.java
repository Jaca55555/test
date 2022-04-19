package uz.maroqand.ecology.api;

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


/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Configuration
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ComponentScan("uz.maroqand.ecology")
@EnableJpaRepositories(basePackages = {"uz.maroqand.ecology.core.repository"}, repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EntityScan(basePackages = {"uz.maroqand.ecology.core.entity"})
@EnableCaching(proxyTargetClass = true)
public class RestStarter {


    public static void main(String[] args) throws Throwable {
//        ApiContextInitializer.init();

        ConfigurableApplicationContext applicationContext = SpringApplication.run(RestStarter.class, args);
        RestStarter applicationStarter = applicationContext.getBean(RestStarter.class);
       /* UserService userService =applicationContext.getBean(UserService.class);
        DocumentTaskSubService documentTaskSubService = applicationContext.getBean(DocumentTaskSubService.class);*/
        /*TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot(userService,documentTaskSubService));
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }*/
        System.out.println("======================== INITIALIZATION COMPLETED ========================");

    }

}
