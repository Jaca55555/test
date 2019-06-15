package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class AgreementCompleteController {

    private RegApplicationService regApplicationService;

    @Autowired
    public void setRegApplicationService(RegApplicationService regApplicationService) {
        this.regApplicationService = regApplicationService;
    }
}
