package uz.maroqand.ecology.ecoexpertise.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

/**
 * Created by Utkirbek Boltaev on 17.07.2019.
 * (uz)
 * (ru)
 */
@Controller
public class TestLoginController {

    private UserService userService;
    private UserAdditionalService userAdditionalService;

    private Logger logger = LogManager.getLogger(TestLoginController.class);
    private static PasswordEncoder encoder;

    @Autowired
    public TestLoginController(UserService userService, UserAdditionalService userAdditionalService) {
        this.userService = userService;
        this.userAdditionalService = userAdditionalService;
    }

    @RequestMapping("/test/login")
    public String getTestLoginPage() {
        System.out.println("--test/login");
        return "test_login";
    }


    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    public String postTestLoginPage(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    ) {
        logger.debug("Test USER username: {}, password: {}",username,password);
        User user = userService.findByUsername(username);
        if (user==null || !passwordEncoder().matches(password, user.getPassword())) {
            return "redirect:" + "/test/login?error";
        }
        if(!user.getEnabled().equals(Boolean.TRUE)){
            return "redirect:" + "/403";
        }

        logger.debug("Test USER enter successful:" + user.getUsername());
        user.setUserAdditionalId(userAdditionalService.createUserAdditional(user));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:"+"/dashboard";
    }

    private PasswordEncoder passwordEncoder(){
        if (encoder == null){
            encoder = new BCryptPasswordEncoder();
        }
        return encoder;
    }

}
