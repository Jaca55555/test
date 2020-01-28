package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
@Controller
public class DocController {

    private final UserService userService;

    @Autowired
    public DocController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(DocUrls.Dashboard)
    public String getDepartmentList(Model model) {

        return DocTemplates.Dashboard;
    }

}
