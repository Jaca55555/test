package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class BillingController {

    private final InvoiceService invoiceService;
    private final HelperService helperService;
    private final UserService userService;
    private final SoatoService soatoService;
    private final PaymentService paymentService;
    private final ClientService clientService;

    @Autowired
    public BillingController(InvoiceService invoiceService, HelperService helperService, UserService userService, SoatoService soatoService, PaymentService paymentService, ClientService clientService){
        this.invoiceService = invoiceService;
        this.helperService = helperService;
        this.userService = userService;
        this.soatoService = soatoService;
        this.paymentService = paymentService;
        this.clientService = clientService;
    }

    @RequestMapping(ExpertiseUrls.BillingList)
    public String billingList(Model model){
        model.addAttribute("regionsList", soatoService.getRegions());
        model.addAttribute("subRegionsList", soatoService.getSubRegions());
        return ExpertiseTemplates.BillingList;
    }

    @RequestMapping(value = ExpertiseUrls.BillingListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> billingListAjax(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "dateToday", required = false, defaultValue = "false") Boolean dateToday,
            @RequestParam(name = "dateThisMonth", required = false, defaultValue = "false") Boolean dateThisMonth,
            @RequestParam(name = "status", required = false) InvoiceStatus status,
            @RequestParam(name = "invoice", required = false) String invoiceNumber,
            @RequestParam(name = "service", required = false) String service,
            @RequestParam(name = "detail", required = false) String detail,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "subRegionId", required = false) Integer subRegionId,
            Pageable pageable
    ){
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        invoiceNumber = StringUtils.trimToNull(invoiceNumber);
        service = StringUtils.trimToNull(service);
        detail = StringUtils.trimToNull(detail);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);

        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<Invoice> invoicePage = invoiceService.findFiltered(
                dateBegin,
                dateEnd,
                dateToday,
                dateThisMonth,
                status,
                invoiceNumber,
                service,
                detail,
                regionId,
                subRegionId,
                user.getOrganizationId(),
                pageable
        );

        List<Invoice> invoiceList = invoicePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(invoiceList.size());
        for (Invoice invoice : invoiceList){
            convenientForJSONArray.add(new Object[]{
                    invoice.getId(),
                    invoice.getInvoice(),
                    invoice.getPayeeId() != null ? helperService.getOrganizationName(invoice.getPayeeId(), locale) : "",
                    invoice.getAmount(),
                    invoice.getStatus()
            });
        }

        result.put("recordsTotal", invoicePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", invoicePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.BillingView)
    @ResponseBody
    public HashMap<String,Object> billingView(@RequestParam(name = "id") Integer id){
        HashMap<String,Object> result = new HashMap<>();

        Invoice invoice = invoiceService.getInvoice(id);
        if(invoice == null){
            return null;
        }
        Client client = clientService.getById(invoice.getClientId());
        List<Payment> paymentList = paymentService.getByInvoiceId(id);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Double paymentTotal = 0.0;
        Double invoiceLeft = invoice.getAmount();

        List<Object[]> convenientForJSONArray = new ArrayList<>(paymentList.size());
            for (Payment payment : paymentList){
                convenientForJSONArray.add(new Object[]{
                        payment.getType(),
                        payment.getPaymentDate() != null ? Common.uzbekistanDateAndTimeFormat.format(payment.getPaymentDate()) : "",
                        payment.getAmount()
                });
                paymentTotal += payment.getAmount();
        }
            invoiceLeft -= paymentTotal;
            result.put("paymentTotal",paymentTotal);
            result.put("invoiceLeft",invoiceLeft);

            result.put("invoiceNumber", invoice.getInvoice());
            result.put("invoiceStatus", invoice.getStatus());
            result.put("invoiceDate", invoice.getCreatedDate() != null ? Common.uzbekistanDateFormat.format(invoice.getCreatedDate()) : "");
            result.put("invoiceExpiryAt", invoice.getExpireDate() != null ? Common.uzbekistanDateAndTimeFormat.format(invoice.getExpireDate()) : "");
            result.put("invoiceDetail", invoice.getDetail());
            result.put("invoiceQty", invoice.getQty());
            result.put("invoiceAmount", invoice.getAmount());

            result.put("payeeName", invoice.getPayeeName());
            result.put("payeeTin", invoice.getPayeeTin());
            result.put("payeeAccount", invoice.getPayeeAccount());
            result.put("payeeAddress", invoice.getPayeeAddress());

            result.put("payerName", invoice.getPayerName());
            if(client.getType() != null){
                result.put("payerType", helperService.getApplicantType(client.getType().getId(),locale));
            }else {
                result.put("payerType","");
            }
            result.put("payerTin", client.getTin());
            result.put("payerPassport", client.getPassportSerial() + client.getPassportNumber());
            result.put("payerPhone", client.getPhone());
            result.put("payerEmail", client.getEmail());
            result.put("payerBank", client.getBankName());
            result.put("payerBankAccount", client.getBankAccount());


            result.put("payments",convenientForJSONArray);
            return result;
    }
}
