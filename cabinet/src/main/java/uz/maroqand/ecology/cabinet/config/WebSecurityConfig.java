package uz.maroqand.ecology.cabinet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.component.CustomSuccessHandler;
import uz.maroqand.ecology.core.component.UserDetailsServiceImpl;
import uz.maroqand.ecology.core.constant.user.Permissions;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static PasswordEncoder encoder;

    private final CustomSuccessHandler customSuccessHandler;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public WebSecurityConfig(CustomSuccessHandler customSuccessHandler, UserDetailsServiceImpl userDetailsService) {
        this.customSuccessHandler = customSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(customSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/static*/**").permitAll()
                .antMatchers("/docEditor/**").permitAll()
                .antMatchers("/onlyoffice/fixationCallback").permitAll()
                .antMatchers("/doc/file_upload").permitAll()
                .antMatchers("/doc/file/download").permitAll()
                .antMatchers("/expertise/conclusion/file_download_for_view").permitAll()
                .antMatchers("/expertise/api/**").permitAll()
                .antMatchers("/expertise/conclusion/reg_file_download_for_view").permitAll()
                .antMatchers("/expertise/conclusion/reg_word_file_download_for_view").permitAll()
                .antMatchers(SysUrls.SelectLang).permitAll()
                .antMatchers(SysUrls.GetDocument +"/**").permitAll()
                .antMatchers(SysUrls.GetQRImage +"/**").permitAll()
                .antMatchers(SysUrls.ErrorNotFound).permitAll()
                .antMatchers(SysUrls.ErrorForbidden).permitAll()
                .antMatchers(SysUrls.ErrorInternalServerError).permitAll()
                .antMatchers("/static*/**", "/map").permitAll()
                .antMatchers("/admin/dashboard/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/billing/payment_file_all/get_invoice").hasAuthority(Permissions.ADMIN_ROLE.name())
                .antMatchers("/expertise/dashboard/**").hasAuthority(Permissions.EXPERTISE.name())
                .antMatchers("/doc/dashboard/**").hasAuthority(Permissions.DOC_MANAGEMENT.name())
                .antMatchers("/dashboard/**").authenticated()

                .antMatchers("/mgmt/translations/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/mgmt/expertise/object_expertise/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/mgmt/expertise/activity/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/mgmt/expertise/material/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/mgmt/expertise/expertise_requirement/**").hasAuthority(Permissions.ADMIN.name())
                .antMatchers("/mgmt/expertise/offer/**").hasAuthority(Permissions.ADMIN.name())

                .antMatchers("/expertise/confirm/**").hasAuthority(Permissions.EXPERTISE_CONFIRM.name())
                .antMatchers("/expertise/forwarding/").hasAuthority(Permissions.EXPERTISE_FORWARDING.name())
                .antMatchers("/expertise/performer/**").hasAuthority(Permissions.EXPERTISE_PERFORMER.name())
                .antMatchers("/expertise/agreement/**").hasAuthority(Permissions.EXPERTISE_AGREEMENT.name())
                .antMatchers("/expertise/agreement_complete/**").hasAuthority(Permissions.EXPERTISE_AGREEMENT_COMPLETE.name())

                .antMatchers("/expertise/applicant/**").hasAuthority(Permissions.ENTERPRISE_REGISTER.name())
                .antMatchers("/expertise/coordinate/**").hasAuthority(Permissions.COORDINATE_REGISTER.name())
                .antMatchers("/expertise/billing/**").hasAuthority(Permissions.BILLING.name())
                .antMatchers("/billing/payment_file/**").hasAuthority(Permissions.PAYMENT_FILE.name())
                .antMatchers("/expertise/facture/**").hasAuthority(Permissions.FACTURE_MONITORING.name())
                .antMatchers("/expertise/agree/**").hasAuthority(Permissions.EXPERTISE_AGREE.name())

                .antMatchers("/sys/appeal_admin/**").hasAuthority(Permissions.APPEAL_ADMIN.name())

                .antMatchers("/doc/registration/incoming/**").hasAuthority(Permissions.DOC_MANAGEMENT_REGISTER.name())
                .antMatchers("/doc/outgoing_mail/**").hasAuthority(Permissions.DOC_MANAGEMENT_REGISTER.name())
                .antMatchers("/doc/registration/inner/**").hasAuthority(Permissions.DOC_MANAGEMENT_REGISTER.name())

                .antMatchers("/mgmt/**").hasAuthority(Permissions.ADMIN.name());
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout");



        http.csrf().ignoringAntMatchers("/login","/docEditor/","/onlyoffice/fixationCallback");

        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .rememberMe()
                .key("remember-me")
                .tokenValiditySeconds(86400);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        if (encoder == null)
            encoder = new BCryptPasswordEncoder();
        return encoder;
    }
}
