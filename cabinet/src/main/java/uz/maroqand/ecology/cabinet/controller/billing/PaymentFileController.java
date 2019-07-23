package uz.maroqand.ecology.cabinet.controller.billing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.billing.BillingTemplates;
import uz.maroqand.ecology.cabinet.constant.billing.BillingUrls;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Controller
public class PaymentFileController {

    private PaymentFileService paymentFileService;

    @Autowired
    public PaymentFileController(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    @RequestMapping(BillingUrls.PaymentFileList)
    public String billingList(Model model){

        return BillingTemplates.PaymentFileList;
    }

    @RequestMapping(value = BillingUrls.PaymentFileListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> billingListAjax(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "invoice", required = false) String invoice,
            @RequestParam(name = "paymentId", required = false) Integer paymentId,

            @RequestParam(name = "payerTin", required = false) Integer payerTin,
            @RequestParam(name = "payerName", required = false) String payerName,
            @RequestParam(name = "detail", required = false) String detail,
            @RequestParam(name = "bankMfo", required = false) String bankMfo,

            @RequestParam(name = "isComplete", required = false) Boolean isComplete,
            Pageable pageable
    ){
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        invoice = StringUtils.trimToNull(invoice);
        detail = StringUtils.trimToNull(detail);
        payerName = StringUtils.trimToNull(payerName);
        bankMfo = StringUtils.trimToNull(bankMfo);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        HashMap<String,Object> result = new HashMap<>();

        Page<PaymentFile> paymentFilePage = paymentFileService.findFiltered(
                dateBegin,
                dateEnd,
                invoice,
                paymentId,
                payerTin,
                payerName,
                detail,
                bankMfo,
                isComplete,
                pageable
        );

        List<PaymentFile> paymentFileList = paymentFilePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(paymentFileList.size());
        for (PaymentFile paymentFile : paymentFileList){
            convenientForJSONArray.add(new Object[]{
                    paymentFile.getId(),
                    paymentFile.getInvoice(),
                    paymentFile.getPayerName(),
                    paymentFile.getPaymentDate()!=null? Common.uzbekistanDateAndTimeFormat.format(paymentFile.getPaymentDate()):"",
                    paymentFile.getAmount(),
                    paymentFile.getDetails()
            });
        }

        result.put("recordsTotal", paymentFilePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", paymentFilePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

}