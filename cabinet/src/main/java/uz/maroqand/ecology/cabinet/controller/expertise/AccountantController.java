package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class AccountantController {

    private final RegApplicationService regApplicationService;

    @Autowired
    public AccountantController(RegApplicationService regApplicationService) {
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantList)
    public String getAccountantListPage() {

        return ExpertiseTemplates.AccountantList;
    }

}
