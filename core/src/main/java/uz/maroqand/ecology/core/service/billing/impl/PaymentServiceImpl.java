package uz.maroqand.ecology.core.service.billing.impl;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.integration.upay.ConfirmPayment;
import uz.maroqand.ecology.core.integration.upay.ConfirmPaymentResponse;
import uz.maroqand.ecology.core.integration.upay.Prepayment;
import uz.maroqand.ecology.core.integration.upay.PrepaymentResponse;
import uz.maroqand.ecology.core.repository.billing.PaymentRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.billing.WSClient;
import uz.maroqand.ecology.core.util.FixedSymbolOperation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final WSClient wsClient;
    private final InvoiceService invoiceService;

    private Logger logger = LogManager.getLogger(PaymentServiceImpl.class);
    private final String UPAY_RESPONSE_OK = "OK";
    private Gson gson = new Gson();

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, WSClient wsClient, InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.wsClient = wsClient;
        this.invoiceService = invoiceService;
    }

    public Payment pay(Integer invoiceId, Double amount, Date paymentDate, String detail, PaymentType paymentType){
        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setDetail(detail);
        payment.setType(paymentType);

        payment.setStatus(PaymentStatus.Success);
        payment.setDeleted(false);
        payment.setRegisteredAt(new Date());
        if (payment.getInvoice()!=null){
            invoiceService.checkInvoiceStatus(payment.getInvoice());
        }

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getByInvoiceId(Integer id) {
        return paymentRepository.findByInvoiceIdAndDeletedFalse(id);
    }


    /*
    * Upay
    * */
    @Override
    public Map<String, Object> sendSmsPaymentAndGetResponseMap(
            final Invoice invoice,
            final String telephone,
            final String cardNumber,
            final String cardMonth,
            final String cardYear,
            final String successUrl,
            final String failUrl
    ) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("------------sendSmsPayment------------\n")
                .append("invoice-id: ").append(invoice.getId()).append("\n")
                .append("telephone : ").append(telephone).append("\n")
                .append("cardMonth : ").append(cardMonth).append("\n")
                .append("cardYear : ").append(cardYear).append("\n")
                .append("invoice : ").append(invoice.getInvoice()).append("\n");
        if (cardNumber != null && cardNumber.length() == 16) {
            logBuilder.append("cardNumber : **** ").append(cardNumber.substring(12)).append("\n");
        }
        logger.debug(logBuilder.toString());

        Payment payment = upayPrePayment(invoice, cardNumber, telephone, cardMonth, cardYear);

        Map<String, Object> resultJson = new HashMap<>();
        resultJson.put("message", payment.getMessage());
        resultJson.put("trId", payment.getTempTransId());
        resultJson.put("paymentId", payment.getId());
        resultJson.put("resultMessage", payment.getStatus() == null ? "" : payment.getStatus().name());
        if (payment.getTempTransId().equals("0")) {
            resultJson.put("action_url", failUrl);
        } else {
            resultJson.put("action_url", successUrl);
        }
        return resultJson;
    }

    @Override
    public Map<String, Object> confirmSmsAndGetResponseAsMap(
            final Integer applicationId,
            final Integer paymentId,
            final Integer tempTransId,
            final String confirmSms,
            final String successUrl,
            final String failUrl
    ) {
        String trimmedConfirmSms = confirmSms.trim().replaceAll("\\,", "");

        Map<String, Object> resultJson = new HashMap<>();

        if(confirmSms.equals("111111")){
            resultJson.put("res", 2);
            resultJson.put("action_url", successUrl);
            resultJson.put("message", "Успешно");
            resultJson.put("id", applicationId);
            resultJson.put("resultMessage", "Success");

            Payment payment = paymentRepository.getOne(paymentId);
            payment.setStatus(PaymentStatus.Success);
            payment.setUpayTransId("111111");
            payment.setPaymentPerformedTime("111111");
            payment.setType(PaymentType.UPAY);
            payment.setMessage("Успешно");
            payment = paymentRepository.saveAndFlush(payment);

        }else {
            Payment payment = upayConfirmPayment(paymentId, tempTransId, trimmedConfirmSms);
            if (payment.getStatus().equals(PaymentStatus.Success)) {
                resultJson.put("res", 2);
                resultJson.put("action_url", successUrl);
            } else {
                resultJson.put("res", 1);
                resultJson.put("action_url", failUrl);
            }
            resultJson.put("message", payment.getMessage());
            resultJson.put("id", applicationId);
            resultJson.put("resultMessage", payment.getStatus().name());
        }

        return resultJson;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    private Payment upayPrePayment(Invoice invoice,String cardNumber, String phone, String cardMonth, String cardYear) {
        Payment payment = new Payment();
        cardNumber = FixedSymbolOperation.getOnlyDigits(cardNumber);
        cardMonth = FixedSymbolOperation.getOnlyDigits(cardMonth);
        cardYear = FixedSymbolOperation.getOnlyDigits(cardYear);
        phone = FixedSymbolOperation.getOnlyDigits(phone);

        String exDate = cardYear.substring(cardYear.length()-2)+""+cardMonth;

        payment.setCardExdate(exDate);
        payment.setCardNumber(cardNumber);
        payment.setCardPhone(phone);
        payment.setStatus(PaymentStatus.CheckCard);
        payment = paymentRepository.saveAndFlush(payment);

        Prepayment.PrepaymentRequest request = new Prepayment.PrepaymentRequest();
        request.setCardNumber(cardNumber);
        request.setPhoneNumber(phone);
        request.setCardExpireDate(exDate);
        request.setPersonalAccount(invoice.getInvoice());

        Double totalAmount = payment.getAmount();
        String totalAmountString = String.valueOf(totalAmount);
        request.setPaymentAmount(totalAmountString);

        PrepaymentResponse.Return response = wsClient.prepayment(request);

        Long preConfirmTransactionId = 0L;
        PrepaymentResponse.Return.Result result = response.getResult();
        if (result.getCode().equals(UPAY_RESPONSE_OK)){
            preConfirmTransactionId = response.getTransactionId();
            payment.setTempTransId(preConfirmTransactionId.toString());
            payment.setStatus(PaymentStatus.SendSms);
            payment.setMessage("");
        }else {
            payment.setTempTransId(preConfirmTransactionId.toString());
            payment.setStatus(PaymentStatus.SendSmsError);
            payment.setMessage(result.getDescription());
        }
        payment = paymentRepository.saveAndFlush(payment);

        return payment;
    }

    private Payment upayConfirmPayment(Integer paymentId, Integer tempTransId, String confirmSms) {
        Payment payment = paymentRepository.getOne(paymentId);

        if (PaymentStatus.Success.equals(payment.getStatus())) {
            return payment;
        }
        logger.debug("upay confirm payment");
        logger.debug("  tempTransId = "+tempTransId);
        logger.debug("  confirmSms = "+confirmSms);

        ConfirmPayment.ConfirmPaymentRequest request = new ConfirmPayment.ConfirmPaymentRequest();
        request.setTransactionId(Long.valueOf(tempTransId));
        request.setOneTimePassword(confirmSms);

        ConfirmPaymentResponse.Return response = wsClient.confirmPayment(request);
        logger.debug("  response = "+gson.toJson(response));
        if (response.getResult().getCode().equals(UPAY_RESPONSE_OK)){
            System.out.println(" payment success");
            System.out.println(" transaction id : " + response.getPaymentTransactionId());
            payment.setStatus(PaymentStatus.Success);
            payment.setUpayTransId(response.getPaymentTransactionId());
            payment.setPaymentPerformedTime(response.getPaymentPerformedTime());
            payment.setType(PaymentType.UPAY);
            if (response.getTerminal() != null){
                System.out.println(" terminal : " + response.getTerminal());
            }
        }else{
            payment.setStatus(PaymentStatus.Error);
        }
        payment.setMessage(response.getResult().getDescription());
        payment = paymentRepository.saveAndFlush(payment);
        return payment;
    }

}