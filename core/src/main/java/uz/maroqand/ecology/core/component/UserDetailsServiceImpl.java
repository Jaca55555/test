package uz.maroqand.ecology.core.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserAdditionalService userAdditionalService;
    private UserService userService;

    private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    public UserDetailsServiceImpl(UserService userService, UserAdditionalService userAdditionalService){
        this.userService = userService;
        this.userAdditionalService = userAdditionalService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("-----------");
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("User [" + username + "] not found.");
        }

        User user = userService.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("User [" + username + "] not found.");
        }

        if(!user.getEnabled().equals(Boolean.TRUE)){
            throw new UsernameNotFoundException("Your account have been blocked");
        }

        System.out.println("userAdditional - create");
        user.setUserAdditionalId(userAdditionalService.createUserAdditional(user));

        return new UserDetailsImpl(user);
    }

}
