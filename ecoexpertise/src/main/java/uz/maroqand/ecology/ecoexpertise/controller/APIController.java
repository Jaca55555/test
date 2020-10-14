package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.dto.api.Response;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@RestController
public class APIController {
    private final Logger logger = LogManager.getLogger(APIController.class);

    private static final String AUTH_KEY = "A347E44AC8752BA7ED33A1C36300DEW0";

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    public APIController(InvoiceService invoiceService, PaymentService paymentService) {
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/upay/check", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response check(
            @RequestHeader(value="Auth", required = false) String auth,
            @RequestParam(value="personalAccount", required = false) String personalAccount,
            HttpServletRequest request
    ) {
        Map<String, Object> parametersMap = new HashMap<>();
        Map<String, Object> headersMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            parametersMap.put(parameterName, request.getParameter(parameterName));
        }
        while (headerNames.hasMoreElements()) {
            String parameterName = headerNames.nextElement();
            headersMap.put(parameterName, request.getHeader(parameterName));
        }
        logger.info("HeadersParams="+headersMap);
        logger.info("RequestParams="+parametersMap);


        logger.info("Auth:{}, PersonalAccount:{}", auth, personalAccount);
        Response response = new Response();

        if(auth==null || !auth.equals(AUTH_KEY)){
            response.setStatus("-1");
            response.setMessage("Ошибка авторизации");
            return response;
        }

        if (personalAccount!=null && personalAccount.length()!=14){
            response.setStatus("-2");
            response.setMessage("Ошибка personalAccount");
            return response;
        }

        Invoice invoice = invoiceService.getInvoice(personalAccount);
        if (invoice==null){
            response.setStatus("-3");
            response.setMessage("invoice не найден ");
            return response;
        }

        Double residualAmount = invoice.getAmount() - invoiceService.getPayAmount(invoice.getId());

        response.setClient(invoice.getPayerName());
        response.setTin(invoice.getPayeeTin().toString());
        response.setAmount(invoice.getAmount().toString());
        response.setPaidAmount((invoice.getAmount()-residualAmount) + "");
        response.setResidualAmount(residualAmount.toString());


        response.setStatus("0");
        response.setMessage("Успешно");
        return response;
    }

    @RequestMapping(value = "/upay/payment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response upayPayment(
            @RequestHeader(value="Auth", required = false) String auth,
            @RequestParam(value="upayTransId", required = false) String upayTransId,
            @RequestParam(value="upayTransTime", required = false) String upayTransTime,
            @RequestParam(value="upayPaymentAmount", required = false) String upayPaymentAmount,
            @RequestParam(value="personalAccount", required = false) String personalAccount,
            HttpServletRequest request
    ) {


        Map<String, Object> parametersMap = new HashMap<>();
        Map<String, Object> headersMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            parametersMap.put(parameterName, request.getParameter(parameterName));
        }
        while (headerNames.hasMoreElements()) {
            String parameterName = headerNames.nextElement();
            headersMap.put(parameterName, request.getHeader(parameterName));
        }
        logger.info("RequestParams="+parametersMap);
        logger.info("HeadersMap="+headersMap);


        logger.info("Auth:{}, upayTransId:{}, upayTransTime:{}, upayPaymentAmount:{}, personalAccount:{}",
                auth, upayTransId, upayTransTime, upayPaymentAmount, personalAccount);

        Response response = new Response();

        if(auth==null || !auth.equals(AUTH_KEY)){
            response.setStatus("-1");
            response.setMessage("Ошибка авторизации");
            return response;
        }

        Double amount = null;
        try {
            amount = Double.parseDouble(upayPaymentAmount);
        } catch (Exception e){ e.printStackTrace(); }


        if (personalAccount!=null && personalAccount.length()!=14){
            response.setStatus("-2");
            response.setMessage("Ошибка personalAccount");
            return response;
        }

        Invoice invoice = invoiceService.getInvoice(personalAccount);
        if (invoice==null){
            response.setStatus("-3");
            response.setMessage("personalAccount не найден ");
            return response;
        }

        Date time = DateParser.TryParse(upayTransTime, Common.uzbekistanDateAndTimeFormat);
        Payment payment = new Payment();
        payment.setType(PaymentType.UPAY);
        payment.setTempTransId(upayTransId);
        payment.setStatus(PaymentStatus.Success);
        payment.setAmount(amount);
        payment.setPaymentDate(time);
        payment.setRegisteredAt(new Date());
        payment.setDeleted(Boolean.FALSE);
        paymentService.save(payment);

        response.setStatus("0");
        response.setMessage("Платеж принят");
        return response;
    }
}
