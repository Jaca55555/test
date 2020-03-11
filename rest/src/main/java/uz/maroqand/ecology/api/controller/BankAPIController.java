package uz.maroqand.ecology.api.controller;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.dto.api.*;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.util.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @RequestMapping(value = "/payment/new", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public PaymentResponse newPaymentPost(
            @RequestBody( required = false) String params,
            HttpServletRequest request
    ) {
        Gson gson = new Gson();
        Map<String, Object> parametersMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            parametersMap.put(parameterName, request.getParameter(parameterName));
        }
        logger.info("RequestParams="+parametersMap);
        logger.info("RequestBody="+params);

        PaymentResponse paymentResponse = new PaymentResponse();
        PaymentData paymentData = null;
        try {
            paymentData = gson.fromJson(params, PaymentData.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(paymentData==null){
            paymentResponse.setCode("1");
            paymentResponse.setMessage("Ошибка в формате JSON объекта");
            return paymentResponse;
        }
        System.out.println(paymentData);
        System.out.println(paymentData.getData());
        List<PaymentResponseData> data = new LinkedList<>();
        for (PaymentNew paymentNew:paymentData.getData()){
            //create PaymentFile
            PaymentFile paymentFile = new PaymentFile();
            try {
                paymentFile.setPayerTin(paymentNew.getPayer().getTin());
                paymentFile.setPayerName(paymentNew.getPayer().getName());
                paymentFile.setBankAccount(paymentNew.getBank().getAccount());
                paymentFile.setBankMfo(paymentNew.getBank().getMfo());

                paymentFile.setBankId(paymentNew.getId());
                try {
                    paymentFile.setAmount(Double.parseDouble(paymentNew.getAmount())/100);
                } catch (Exception e){}

                paymentFile.setAmountOriginal(paymentNew.getAmount());
                paymentFile.setDocumentNumber(paymentNew.getDocument_number());
                Date date = DateParser.TryParse(paymentNew.getPayment_date(),Common.uzbekistanDateAndTimeFormatBank);
                paymentFile.setPaymentDate(date);
                paymentFile.setDetails(paymentNew.getDetails());

                paymentFile = paymentFileService.create(paymentFile);

                data.add(new PaymentResponseData(paymentNew.getId(), paymentFile.getId()));
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
                paymentService.pay(invoice.getId(), paymentFile.getAmount(), new Date(), paymentFile.getDetails(), PaymentType.BANK);
            }

        }

        paymentResponse.setCode("0");
        paymentResponse.setMessage("Успешно");
        paymentResponse.setData(data);

        /*String response = HttpRequestHelper.makePostRequestWithJSON("http://172.25.43.81:9999/eco/tobank", gson.toJson(paymentResponse));
        logger.info("Response /eco/tobank="+response);*/

        return paymentResponse;
    }

}
