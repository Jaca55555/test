package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.maroqand.ecology.core.dto.api.ResponseDTO;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;

import static uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls.Api;
import static uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls.ApiView;

@RestController
@RequestMapping(Api)
public class FondApiController {
    @Autowired
    RegApplicationService regApplicationService;
//    @GetMapping("/{tin}")
//    ResponseDTO get(@PathVariable("tin") Integer tin) {
//        return new ResponseDTO(regApplicationService.listByTin(tin));
//    }
}
