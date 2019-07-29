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
                .antMatchers(SysUrls.ErrorNotFound).permitAll()
                .antMatchers(SysUrls.ErrorForbidden).permitAll()
                .antMatchers(SysUrls.ErrorInternalServerError).permitAll()
                .antMatchers("/static*/**", "/map").permitAll()
                .antMatchers("/dashboard/**").authenticated()
                .antMatchers("/sys/appeal_admin/**").authenticated()
                .antMatchers("/mgmt/translations/**").authenticated()

                .antMatchers("/mgmt/expertise/object_expertise/**").authenticated()
                .antMatchers("/mgmt/expertise/activity/**").authenticated()
                .antMatchers("/mgmt/expertise/material/**").authenticated()
                .antMatchers("/mgmt/expertise/expertise_requirement/**").authenticated()
                .antMatchers("/mgmt/expertise/offer/**").authenticated()

                .antMatchers("/expertise/confirm/**").hasAuthority(Permissions.EXPERTISE_CONFIRM.name())
                .antMatchers("/expertise/forwarding/").hasAuthority(Permissions.EXPERTISE_FORWARDING.name())
                .antMatchers("/expertise/performer/**").hasAuthority(Permissions.EXPERTISE_PERFORMER.name())
                .antMatchers("/expertise/agreement/**").hasAuthority(Permissions.EXPERTISE_AGREEMENT.name())
                .antMatchers("/expertise/agreement_complete/**").hasAuthority(Permissions.EXPERTISE_AGREEMENT_COMPLETE.name())

                .antMatchers("/mgmt/**").hasAuthority(Permissions.ADMIN.name());
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout");



        http.csrf().ignoringAntMatchers("/login");

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