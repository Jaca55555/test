package uz.maroqand.ecology.cabinet.controller.billing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.billing.BillingTemplates;
import uz.maroqand.ecology.cabinet.constant.billing.BillingUrls;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
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
    private InvoiceService invoiceService;
    private ClientService clientService;
    private PaymentService paymentService;

    @Autowired
    public PaymentFileController(PaymentFileService paymentFileService, InvoiceService invoiceService, ClientService clientService, PaymentService paymentService) {
        this.paymentFileService = paymentFileService;
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.paymentService = paymentService;
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
            boolean invoiceIsNull = Boolean.TRUE;
            if (paymentFile.getInvoice()!=null) invoiceIsNull = Boolean.FALSE;
            if (paymentFile.getInvoice()==null){
                String invoiceStr = paymentFile.getDetails();
                String[] parts = invoiceStr.split(" ");
                for (String invoiceCheck : parts) {
                    if(invoiceCheck.length()==14){
                        invoiceService.getInvoice(invoiceCheck);
                        if(invoiceService.getInvoice(invoiceCheck)!=null ){
                            invoiceIsNull = Boolean.FALSE;
                            break;
                        }
                    }
                }
            }
            convenientForJSONArray.add(new Object[]{
                paymentFile.getId(),
                paymentFile.getInvoice(),
                paymentFile.getPayerName(),
                paymentFile.getPaymentDate()!=null? Common.uzbekistanDateAndTimeFormat.format(paymentFile.getPaymentDate()):"",
                paymentFile.getAmount(),
                paymentFile.getDetails(),
                invoiceIsNull
            });
        }

        result.put("recordsTotal", paymentFilePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", paymentFilePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(BillingUrls.PaymentFileView+"/{id}")
    public String getPaymentFileViewPage(@PathVariable("id") Integer id, Model model ){
        PaymentFile paymentFile = paymentFileService.getById(id);
        if(paymentFile==null){
            return "redirect: " + BillingUrls.PaymentFileList;
        }

        model.addAttribute("paymentFile", paymentFile);
        return BillingTemplates.PaymentFileView;
    }

    @RequestMapping(BillingUrls.PaymentFileEdit)
    public String joinInvoice(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        PaymentFile paymentFile = paymentFileService.getById(id);
        if (paymentFile==null){
            return "redirect:" + BillingUrls.PaymentFileList;
        }
        System.out.println("payerTin" + paymentFile.getPayerTin());
        Client client = clientService.getByTin(paymentFile.getPayerTin());
        if (client==null){
            return "redirect:" + BillingUrls.PaymentFileList;
        }
        List<Invoice> invoiceList = invoiceService.getListByStatusAndClientId(InvoiceStatus.Initial,client.getId());
        model.addAttribute("invoiceList",invoiceList);
        model.addAttribute("paymentFileId",paymentFile.getId());
        return BillingTemplates.PaymentFileEdit;
    }

    @RequestMapping(BillingUrls.PaymentFileEditSubmit)
    public String joinInvoiceId(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "paymentFileId") Integer paymentFileId
    ){

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile!=null){
            Invoice invoice = invoiceService.getInvoice(id);
            if (invoice!=null){
                paymentFile.setInvoice(invoice.getInvoice());
                paymentFileService.save(paymentFile);
                paymentService.pay(invoice.getId(), paymentFile.getAmount(), paymentFile.getPaymentDate(), paymentFile.getDetails(), PaymentType.BANK);
            }
        }

        return "redirect:" + BillingUrls.PaymentFileList;
    }

}