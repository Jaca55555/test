package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Controller
public class CommentController {
    private DocumentLogService documentLogService;
    private HelperService helperService;
    private UserService userService;
    @Autowired
    public CommentController(
            UserService userService,
            DocumentLogService documentLogService,
            HelperService helperService
    ) {
        this.documentLogService = documentLogService;
        this.helperService = helperService;
        this.userService = userService;
    }
    @RequestMapping(value = DocUrls.CommentList)
    public String getCommentPage() {
        System.out.println("hello world");
        return DocTemplates.CommentList;
    }

    @RequestMapping(value = DocUrls.CommentListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getCommentList(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();

        Page<DocumentLog> logPage = documentLogService.findFiltered(dateBeginStr, dateEndStr, user.getId(),  1,pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<DocumentLog> logList = logPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(logList.size());
        for (DocumentLog documentLog : logList){
            convenientForJSONArray.add(new Object[]{
                    documentLog.getId(),
                    documentLog.getCreatedById()!=null? helperService.getUserFullNameById(documentLog.getCreatedById()):"",
                    documentLog.getCreatedAt()!=null? Common.uzbekistanDateAndTimeFormat.format(documentLog.getCreatedAt()):"",
                    documentLog.getContent()!=null? documentLog.getContent():"",
            });
        }
        result.put("recordsTotal", logPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", logPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

}
