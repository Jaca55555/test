package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.constant.order.RegApplicationExcelOrder;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.DocumentOrdersService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            @RequestParam(name = "reviewId", defaultValue = "", required = false) Integer reviewId,
            @RequestParam(name = "type", defaultValue = "", required = false)DocumentOrderType type,
            @RequestParam(name = "category", defaultValue = "", required = false) Category category,
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
        HashMap<String, Object> result = new HashMap<>();
        Date dateEnd ;
        if(dateEndStr!=null){
            dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
        }else{
            dateEnd = new Date();
        }

        long timeDiff = Math.abs(dateEnd.getTime() - dateBegin.getTime());
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        System.out.println("The number of days between dates: " + daysDiff);

        result.put("message", "3 oylik oraliqdan kam kiritishingiz majburiy");
        if(daysDiff>91){
            return result;
        }
        RegApplicationExcelOrder documentOrder = new RegApplicationExcelOrder();
        documentOrder.setBeginDate(dateBegin);
        documentOrder.setEndDate(dateEnd);
        documentOrder.setReviewId(reviewId);
        documentOrder.setCategory(category);
        System.out.println("documentOrder"+documentOrder);

        boolean queued = documentOrdersService.orderDocument(documentOrder,type, user, locale);
        return documentOrdersService.getFrontendResponseForOrder(queued,locale);
    }
}
