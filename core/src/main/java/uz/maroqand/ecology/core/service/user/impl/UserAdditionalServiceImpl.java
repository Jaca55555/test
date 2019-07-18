package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.LoginType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.user.UserAdditional;
import uz.maroqand.ecology.core.repository.user.UserAdditionalRepository;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
@Service
public class UserAdditionalServiceImpl implements UserAdditionalService {

    private final UserAdditionalRepository userAdditionalRepository;

    @Autowired
    public UserAdditionalServiceImpl(UserAdditionalRepository userAdditionalRepository) {
        this.userAdditionalRepository = userAdditionalRepository;
    }

    @Override
    public Integer createUserAdditional(User user){
        UserAdditional userAdditional = new UserAdditional();
        userAdditional.setRegisteredDate(new Date());
        userAdditional.setUserId(user.getId());
        userAdditionalRepository.save(userAdditional);
        return userAdditional.getId();
    }

    @Override
    public Integer updateUserAdditional(User user, LoginType loginType, HttpServletRequest request){
        String keyValue = "";
        String ipClient = "";
        String userAgent = "";
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            keyValue = keyValue + (key+" :"+value+"<br/>");
            if(key.equals("x-forwarded-for")){
                ipClient = value;
            }
            if(key.equals("user-agent")){
                userAgent = value;
            }
        }

        UserAdditional userAdditional = new UserAdditional();
        userAdditional.setInfo(keyValue);
        userAdditional.setUserAgent(userAgent);
        userAdditional.setIpClient(ipClient);

        userAdditional.setId(user.getUserAdditionalId());
        userAdditional.setUserId(user.getId());
        userAdditional.setRegisteredDate(new Date());
        userAdditional.setLoginType(loginType);
        userAdditionalRepository.save(userAdditional);
        return userAdditional.getId();
    }

    @Override
    public UserAdditional getById(Integer id) {
        return userAdditionalRepository.findById(id).get();
    }

}