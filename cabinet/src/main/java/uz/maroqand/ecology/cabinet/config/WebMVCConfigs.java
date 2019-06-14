package uz.maroqand.ecology.cabinet.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 18.09.2018.
 * (uz)
 * (ru)
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "uz.maroqand.ecology.cabinet")
public class WebMVCConfigs implements WebMvcConfigurer {

    private final DatabaseMessageSource databaseMessageSource;

    @Autowired
    public WebMVCConfigs(DatabaseMessageSource databaseMessageSource) {
        this.databaseMessageSource = databaseMessageSource;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> {
            /*container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, SysUrls.ErrorNotFound));
            container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, SysUrls.ErrorInternalServerError));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, SysUrls.ErrorInternalServerError));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, SysUrls.ErrorForbidden));*/
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**", "/static/*")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(24 * 60 * 60 * 1000);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("uz"));
        resolver.setCookieName("localeCookie");
        resolver.setCookieMaxAge(4800);
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCache(true);

        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(defaultTemplateResolver());
        Set<IDialect> dialects = new HashSet<IDialect>();
        dialects.add(new LayoutDialect());
        dialects.add(new SpringSecurityDialect());
        templateEngine.setAdditionalDialects(dialects);

        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver defaultTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        databaseMessageSource.setUseCodeAsDefaultMessage(true);
        return databaseMessageSource;
    }

}
