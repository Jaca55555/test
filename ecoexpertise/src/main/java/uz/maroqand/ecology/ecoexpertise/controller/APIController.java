package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.dto.api.Response;
import uz.maroqand.ecology.core.dto.api.ResponsePay;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
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
//    private static final String AUTH_KEY = "123456";
    private final PaymentFileService paymentFileService;
    private final OrganizationService organizationService;

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final RegApplicationService regApplicationService;

    public APIController(PaymentFileService paymentFileService, OrganizationService organizationService, InvoiceService invoiceService, PaymentService paymentService, RegApplicationService regApplicationService) {
        this.paymentFileService = paymentFileService;
        this.organizationService = organizationService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.regApplicationService = regApplicationService;
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
        response.setPayerTin(invoice.getClient().getTin()!=null ? invoice.getClient().getTin(): Integer.valueOf(invoice.getClient().getPinfl()));
        response.setInvoice(invoice.getInvoice());
        response.setRegApplicationId(regApplicationService.findByInvoiceIdAndDeletedFalse(invoice.getId()).getId());
        response.setOrgName(invoice.getPayeeName());
        response.setTin(invoice.getPayeeTin().toString());
        response.setAmount(Common.decimalFormat.format(invoice.getAmount()));
        response.setPaidAmount(Common.decimalFormat.format(invoice.getAmount()-residualAmount));
        response.setResidualAmount(Common.decimalFormat.format(residualAmount));
        response.setOrgAccount(invoice.getPayeeAccount());
        response.setMfo(invoice.getPayeeMfo());
        response.setPaymentDetails("00668~0~" +invoice.getClient().getTin() +"~"+  invoice.getInvoice()+ "~" + regApplicationService.findByInvoiceIdAndDeletedFalse(invoice.getId()).getId() +"~"+ invoice.getPayerName()+ "  "+  invoice.getPayeeName());

        response.setStatus("0");
        response.setMessage("Успешно");
        return response;
    }


    @RequestMapping(value = "/upay/payment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponsePay upayPayment(
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

        System.out.println("auth" + auth);

        logger.info("Auth:{}, upayTransId:{}, upayTransTime:{}, upayPaymentAmount:{}, personalAccount:{}",
                auth, upayTransId, upayTransTime, upayPaymentAmount, personalAccount);

        ResponsePay response = new ResponsePay();

        if(auth==null || !auth.equals(AUTH_KEY)){
            response.setStatus("-1");
            response.setMessage("Ошибка авторизации");
            return response;
        }

        Double amount = null;
        try {
            amount = Double.parseDouble(upayPaymentAmount)/1.01;
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

        if (amount==null || amount>(invoice.getAmount()-invoiceService.getPayAmount(invoice.getId()))){
            response.setStatus("-4");
            response.setMessage("Сумма платежа большая");
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
        payment.setInvoiceId(invoice.getId());
        paymentService.save(payment);

        PaymentFile paymentFile = new PaymentFile();
        paymentFile.setReceiverInn(invoice.getPayeeTin());
        paymentFile.setPayerName(invoice.getPayerName());
        paymentFile.setInvoice(invoice.getInvoice());
        paymentFile.setPaymentDate(new Date());
        paymentFile.setReceiverName(invoice.getPayeeName());
        paymentFile.setPaymentId(payment.getId());
//        paymentFile.setReceiverName(invoice.get);

//        paymentFile.setBankAccount(invoice.getPayeeAccount());
//        paymentFile.setBankMfo(invoice.getPayeeMfo());


        paymentFile.setReceiverAccount(invoice.getPayeeAccount());
        paymentFile.setReceiverMfo(invoice.getPayeeMfo());


//        paymentFile.setBankId(paymentNew.getId());
        paymentFile.setAmount(amount);


        paymentFile.setAmountOriginal(upayPaymentAmount);
        paymentFile.setDocumentNumber(upayTransId);
//        Date date = DateParser.TryParse(paymentNew.getPayment_date(),Common.uzbekistanDateAndTimeFormatBank);
//        paymentFile.setPaymentDate(time);
        paymentFile.setDetails("00668~0~" +invoice.getClient().getTin() +"~"+  invoice.getInvoice()+ "~" + regApplicationService.findByInvoiceIdAndDeletedFalse(invoice.getId()).getId() +"~"+ invoice.getPayerName()+ "  "+  invoice.getPayeeName());
        paymentFileService.create(paymentFile);
        invoiceService.checkInvoiceStatus(invoice);
        response.setStatus("0");
        response.setMessage("Платеж принят");
        return response;
    }
}
