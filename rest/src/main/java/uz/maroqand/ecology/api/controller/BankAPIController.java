package uz.maroqand.ecology.api.controller;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.dto.api.PaymentNew;
import uz.maroqand.ecology.core.dto.api.PaymentResponse;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@RestController
@RequestMapping("/api/psb")
public class BankAPIController {

    private final Logger logger = LogManager.getLogger(BankAPIController.class);

    private PaymentFileService paymentFileService;
    private InvoiceService invoiceService;
    private PaymentService paymentService;

    @Autowired
    public BankAPIController(PaymentFileService paymentFileService, InvoiceService invoiceService, PaymentService paymentService) {
        this.paymentFileService = paymentFileService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/payment/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public PaymentResponse newPaymentPost(
            @RequestBody(required = false) String params,
            HttpServletRequest request
    ) {
        Map<String, Object> parametersMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            parametersMap.put(parameterName, request.getParameter(parameterName));
        }
        logger.info("RequestParams="+parametersMap);
        logger.info("RequestBody="+params);

        PaymentResponse paymentResponse = new PaymentResponse();
        PaymentNew paymentNew = null;
        try {
            Gson gson = new Gson();
            paymentNew = gson.fromJson(params, PaymentNew.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(paymentNew==null){
            paymentResponse.setCode("1");
            paymentResponse.setMessage("Ошибка в формате JSON объекта");
            return paymentResponse;
        }

        //create PaymentFile
        PaymentFile paymentFile = new PaymentFile();
        try {
            paymentFile.setPayerTin(paymentNew.getPayer().getTin());
            paymentFile.setPayerName(paymentNew.getPayer().getName());
            paymentFile.setBankAccount(paymentNew.getBank().getAccount());
            paymentFile.setBankMfo(paymentNew.getBank().getMfo());

            paymentFile.setAmount(paymentNew.getAmount());
            paymentFile.setDocumentNumber(paymentNew.getDocument_number());
            paymentFile.setPaymentDate(Common.uzbekistanDateAndTimeFormat.parse(paymentNew.getPayment_date()));
            paymentFile.setDetails(paymentNew.getDetails());

            paymentFileService.create(paymentFile);
        }catch (Exception e){
            e.printStackTrace();
            paymentResponse.setCode("2");
            paymentResponse.setMessage("Ошибка при преобразовании данных");
            return paymentResponse;
        }

        //get Invoice
        Invoice invoice = null;
        String invoiceStr = paymentFile.getDetails();
        String[] parts = invoiceStr.split(" ");
        for (String invoiceCheck : parts) {
            if(invoiceCheck.length()==14){
                invoice = invoiceService.getInvoice(invoiceCheck);
                if(invoice!=null) break;
            }
        }

        if(invoice!=null){
            paymentService.pay(invoice.getId(), invoice.getAmount(), new Date(), invoice.getDetail(), PaymentType.BANK);
        }

        paymentResponse.setCode("0");
        paymentResponse.setMessage("Успешно");
        return paymentResponse;
    }

}
