package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.dto.user.Toastr;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegUrls;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 08.04.2019.
 * (uz)
 */
@Controller
public class ToastrController {

    private final UserService userService;
    private final ToastrService toastrService;

    @Autowired
    public ToastrController(UserService userService, ToastrService toastrService) {
        this.userService = userService;
        this.toastrService = toastrService;
    }

    @RequestMapping(value = SysUrls.Toastr, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getToastr(
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();

        List<Toastr> toastrList = toastrService.getByUserId(user.getId());
        result.put("toastrSize", toastrList.size());
        result.put("toastrList", toastrList);
        return result;
    }

}
