package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.constant.order.RegApplicationExcelOrder;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.DocumentOrdersService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;
import java.util.Map;
@Controller
public class RegApplicationExcelController {

    private final UserService userService;
    private final DocumentOrdersService documentOrdersService;

    public RegApplicationExcelController(UserService userService, DocumentOrdersService documentOrdersService) {
        this.userService = userService;
        this.documentOrdersService = documentOrdersService;
    }

    @RequestMapping(value = SysUrls.RegApplicationExcel)
    @ResponseBody
    public Map<String,Object> reportTotalNonprofitTypeExcelList(
            @RequestParam(name = "dateBegin", defaultValue = "", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", defaultValue = "", required = false) String dateEndStr,
            @RequestParam(name = "type", defaultValue = "", required = false)DocumentOrderType type,
            Model model
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        User user = userService.getCurrentUserFromContext();
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);

        Date dateBegin ;
        if(dateBeginStr!=null){
            dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        }else{
            dateBegin = DateParser.TryParse("01.01.2022", Common.uzbekistanDateFormat);
        }

        Date dateEnd ;
        if(dateEndStr!=null){
            dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
        }else{
            dateEnd = new Date();
        }


        RegApplicationExcelOrder documentOrder = new RegApplicationExcelOrder();
        documentOrder.setBeginDate(dateBegin);
        documentOrder.setEndDate(dateEnd);
        System.out.println("documentOrder"+documentOrder);

        boolean queued = documentOrdersService.orderDocument(documentOrder,type, user, locale);
        return documentOrdersService.getFrontendResponseForOrder(queued,locale);
    }
}
