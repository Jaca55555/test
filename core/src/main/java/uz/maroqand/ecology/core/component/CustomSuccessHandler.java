package uz.maroqand.ecology.core.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import uz.maroqand.ecology.core.constant.user.LoginType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final UserAdditionalService userAdditionalService;
    private final UserService userService;

    public CustomSuccessHandler(UserAdditionalService userAdditionalService, UserService userService) {
        this.userAdditionalService = userAdditionalService;
        this.userService = userService;
    }

    @Override
    protected void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        String targetUrl = determineTargetUrl(request, authentication);
        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        UserDetailsImpl userDetails = null;
        User user = null;
        if (principal instanceof UserDetailsImpl){
            userDetails = (UserDetailsImpl) principal;
            user = userDetails.getUser();
        }

        if(user==null){
            return "/sys/403";
        }

        userAdditionalService.updateUserAdditional(user, LoginType.EcoExpertiseLogin, request);
        user.setLastEvent(new Date());
        userService.updateUser(user);
        return "/dashboard";
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}