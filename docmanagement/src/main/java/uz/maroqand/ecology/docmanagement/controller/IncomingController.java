package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class IncomingController {

    @RequestMapping(DocUrls.IncomingList)
    public String getIncomingListPage(Model model) {

        return DocTemplates.IncomeMailList;
    }

}
