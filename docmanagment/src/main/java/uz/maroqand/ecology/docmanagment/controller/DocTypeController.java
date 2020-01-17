package uz.maroqand.ecology.docmanagment.controller;

import org.springframework.stereotype.Controller;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentTypeService;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 17.01.2020
 */

@Controller
public class DocTypeController {
    private final DocumentService documentService;
    private final DocumentTypeService documentTypeService;
    private final UserService userService;

    public DocTypeController(
            DocumentService documentService,
            DocumentTypeService documentTypeService,
            UserService userService
    ) {
        this.documentService = documentService;
        this.documentTypeService = documentTypeService;
        this.userService = userService;
    }

}
