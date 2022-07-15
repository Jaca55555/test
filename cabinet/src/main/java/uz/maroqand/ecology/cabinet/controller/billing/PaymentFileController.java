package uz.maroqand.ecology.cabinet.controller.billing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.billing.BillingTemplates;
import uz.maroqand.ecology.cabinet.constant.billing.BillingUrls;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
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

    private final PaymentFileService paymentFileService;
    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final PaymentService paymentService;
    private final  UserService userService;
    private final OrganizationService organizationService;
    private final RegApplicationService regApplicationService;

    @Autowired
    public PaymentFileController(PaymentFileService paymentFileService, InvoiceService invoiceService, ClientService clientService, PaymentService paymentService, UserService userService, OrganizationService organizationService, RegApplicationService regApplicationService) {
        this.paymentFileService = paymentFileService;
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.paymentService = paymentService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.regApplicationService = regApplicationService;
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
        User user = userService.getCurrentUserFromContext();
        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        invoice = StringUtils.trimToNull(invoice);
        detail = StringUtils.trimToNull(detail);
        payerName = StringUtils.trimToNull(payerName);
        bankMfo = StringUtils.trimToNull(bankMfo);

        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
        String account = null;
        String oldAccount = null;
        if (user.getOrganizationId()!=null){
            Organization organization = organizationService.getById(user.getOrganizationId());
            if (organization!=null && organization.getAccount()!=null
                    && !organization.getAccount().isEmpty()){
                account = organization.getAccount();
                oldAccount = organization.getOldAccount();
            }
        }
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
                user.getRole().getId()!=23 ? account:null,
                user.getRole().getId()!=23 ? oldAccount:null,
                pageable
        );

        List<PaymentFile> paymentFileList = paymentFilePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(paymentFileList.size());
        for (PaymentFile paymentFile : paymentFileList){
            StringBuilder accountString  = null;
            boolean invoiceIsNull = Boolean.TRUE;
            if (paymentFile.getInvoice()!=null) invoiceIsNull = Boolean.FALSE;
            if (paymentFile.getReceiverAccount()!=null){
                char accountReceiver[] = paymentFile.getReceiverAccount().toCharArray();
                accountString = new StringBuilder();
                for (int i=0; i<accountReceiver.length;i++){
                    if (i!=0 && (i % 4) ==0 ){
                        accountString.append(" ");
                    }
                    accountString.append(String.valueOf(accountReceiver[i]));
                }
            }
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
                paymentFile.getInvoice()!=null ? paymentFile.getInvoice():"",
                paymentFile.getPayerName(),
                paymentFile.getPaymentDate()!=null? Common.uzbekistanDateAndTimeFormat.format(paymentFile.getPaymentDate()):"",
                String.format("% ,.1f", paymentFile.getAmount()),
                paymentFile.getDetails(),
                invoiceIsNull,
                paymentFile.getPayerTin(),
                accountString!=null?accountString.toString():"",
                paymentFile.getReceiverName(),
                paymentFile.getReceiverInn(),
                paymentFile.getReceiverMfo(),
                paymentFile.getInvoice()!=null? invoiceService.getInvoice(paymentFile.getInvoice())!=null ? invoiceService.getInvoice(paymentFile.getInvoice()).getId():null:null,

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
        User user = userService.getCurrentUserFromContext();
        if (user.getOrganizationId()==null || paymentFile==null){
            return "redirect: " + BillingUrls.PaymentFileList;
        }
        Organization organization = organizationService.getById(user.getOrganizationId());
        if (organization==null || StringUtils.trimToNull(organization.getAccount())==null){
            return "redirect: " + BillingUrls.PaymentFileList;
        }
        if(StringUtils.trimToNull(paymentFile.getReceiverAccount())==null
                || !paymentFile.getReceiverAccount().equals(organization.getAccount())){
            return "redirect: " + BillingUrls.PaymentFileList;
        }

        model.addAttribute("paymentFile", paymentFile);
        return BillingTemplates.PaymentFileView;
    }

    @RequestMapping(BillingUrls.PaymentFileAllDelete+"/{id}")
    public String getPaymentFileViewPageAndDelete(@PathVariable("id") Integer id, Model model ){
        PaymentFile paymentFile = paymentFileService.getById(id);
        User user = userService.getCurrentUserFromContext();
        Payment payment = paymentService.getById(paymentFile.getPaymentId());
        if(payment!=null){
            payment.setDeleted(true);
            paymentService.save(payment);
            System.out.println("payment saqlandi");
            paymentFile.setInvoice(null);
            paymentFile.setPaymentId(null);
            paymentFileService.save(paymentFile);
            System.out.println("paymentFile saqlandi");
        }
        model.addAttribute("paymentFile", paymentFile);
        return BillingTemplates.PaymentFileAllView;
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
        User user = userService.getCurrentUserFromContext();

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile!=null){
            Invoice invoice = invoiceService.getInvoice(id);
            if (invoice!=null){
                paymentFile.setInvoice(invoice.getInvoice());
                paymentFileService.update(paymentFile,user.getId());
                paymentService.pay(invoice.getId(), paymentFile.getAmount(), paymentFile.getPaymentDate(), paymentFile.getDetails(), PaymentType.BANK);
                invoiceService.checkInvoiceStatus(invoice);
            }
        }

        return "redirect:" + BillingUrls.PaymentFileList;
    }


    // Hamma to'lovlarni ko'rish uchun
    @RequestMapping(BillingUrls.PaymentFileAllList)
    public String billingAllList(Model model){
        model.addAttribute("isAdmin",userService.isAdmin());
        model.addAttribute("organizationList",organizationService.getList());
        return BillingTemplates.PaymentFileAllList;
    }

    @RequestMapping(value = BillingUrls.PaymentFileAllListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> billingAllListAjax(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            @RequestParam(name = "invoice", required = false) String invoice,
            @RequestParam(name = "paymentId", required = false) Integer paymentId,
            @RequestParam(name = "organizationId", required = false) Integer organizationId,
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
        String account="";
        if(organizationId!=null) {
            account = organizationService.getById(organizationId).getAccount();
        }
        Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
//        organizationService.getById(1).getAccount();
        HashMap<String,Object> result = new HashMap<>();
        System.out.println("account="+account);
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
                account,
                null,
                pageable
        );

        List<PaymentFile> paymentFileList = paymentFilePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(paymentFileList.size());
        for (PaymentFile paymentFile : paymentFileList){
            StringBuilder accountString  = null;
            boolean invoiceIsNull = Boolean.TRUE;
            if (paymentFile.getInvoice()!=null) invoiceIsNull = Boolean.FALSE;
            if (paymentFile.getReceiverAccount()!=null){
                char accountReceiver[] = paymentFile.getReceiverAccount().toCharArray();
                accountString = new StringBuilder();
                for (int i=0; i<accountReceiver.length;i++){
                    if (i!=0 && (i % 4) ==0 ){
                        accountString.append(" ");
                    }
                    accountString.append(String.valueOf(accountReceiver[i]));
                }
            }
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
                    paymentFile.getInvoice()!=null ? paymentFile.getInvoice():"",
                    paymentFile.getPayerName(),
                    paymentFile.getPaymentDate()!=null? Common.uzbekistanDateAndTimeFormat.format(paymentFile.getPaymentDate()):"",
                    String.format("% ,.1f", paymentFile.getAmount()),
                    paymentFile.getDetails(),
                    invoiceIsNull,
                    paymentFile.getPayerTin(),
                    accountString!=null?accountString.toString():"",
                    paymentFile.getReceiverName(),
                    paymentFile.getReceiverInn(),
                    paymentFile.getReceiverMfo(),
                    paymentFile.getPaymentId(),
                    userService.isAdmin()
            });
        }

        result.put("recordsTotal", paymentFilePage.getTotalElements()); //Total elements
        result.put("recordsFiltered", paymentFilePage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(BillingUrls.PaymentFileAllView+"/{id}")
    public String getPaymentFileAllViewPage(@PathVariable("id") Integer id, Model model ){
        PaymentFile paymentFile = paymentFileService.getById(id);
        if(paymentFile==null){
            return "redirect: " + BillingUrls.PaymentFileAllList;
        }

        model.addAttribute("paymentFile", paymentFile);
        return BillingTemplates.PaymentFileAllView;
    }

    @RequestMapping(BillingUrls.PaymentFileAllEdit)
    public String joinAllInvoice(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        PaymentFile paymentFile = paymentFileService.getById(id);
        if (paymentFile==null){
            return "redirect:" + BillingUrls.PaymentFileAllList;
        }
        System.out.println("payerTin" + paymentFile.getPayerTin());
        Client client = clientService.getByTin(paymentFile.getPayerTin());
        if (client==null){
            return "redirect:" + BillingUrls.PaymentFileAllList;
        }
        List<Invoice> invoiceList = invoiceService.getListByStatusAndClientId(InvoiceStatus.Initial,client.getId());
        model.addAttribute("invoiceList",invoiceList);
        model.addAttribute("paymentFileId",paymentFile.getId());
        return BillingTemplates.PaymentFileAllEdit;
    }

    @RequestMapping(BillingUrls.PaymentFilIsRemoveInvoiceView)
    public String paymentFilIsRemoveInvoiceView(@RequestParam(name = "id") Integer paymentFileId){

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile!=null && paymentFile.getInvoice()!=null && !paymentFile.getInvoice().isEmpty()){
            paymentFileService.removeInvoiceIsDublicate(paymentFile,userService.getCurrentUserFromContext().getId());
        }

        return "redirect:" + BillingUrls.PaymentFileAllList;
    }


    @RequestMapping(BillingUrls.PaymentFileAllEditSubmit)
    public String joinAllInvoiceId(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "paymentFileId") Integer paymentFileId
    ){
        User user = userService.getCurrentUserFromContext();

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile!=null){
            Invoice invoice = invoiceService.getInvoice(id);
            if (invoice!=null){
                paymentFile.setInvoice(invoice.getInvoice());
                paymentFileService.update(paymentFile,user.getId());
                paymentService.pay(invoice.getId(), paymentFile.getAmount(), paymentFile.getPaymentDate(), paymentFile.getDetails(), PaymentType.BANK);
                invoiceService.checkInvoiceStatus(invoice);
            }
        }

        return "redirect:" + BillingUrls.PaymentFileAllList;
    }

    @RequestMapping(BillingUrls.PaymentFileAllConnectInvoice)
    public String connectInvoice(
            @RequestParam(name = "id") Integer paymentFileId,
            Model model
    ){

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile==null || paymentFile.getInvoice()!=null){
            return "redirect:" + BillingUrls.PaymentFileAllList;
        }

        model.addAttribute("paymentFile",paymentFile);
        model.addAttribute("getLink",BillingUrls.PaymentFileAllGetInvoice);

        return BillingTemplates.PaymentFileAllConnectInvoice;

    }


    // status=-1  invoice topilmadi
    // status=-2  ariza topilmadi topilmadi
    @RequestMapping(value = BillingUrls.PaymentFileAllGetInvoice,method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> getInvoiceDetails (
            @RequestParam(name = "invoiceStr") String invoiceStr,
            @RequestParam(name = "id") Integer payFilieId
    ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",1);
        Invoice invoice = invoiceService.getInvoice(invoiceStr);
        if (invoice==null){
            result.put("status",-1);
            return result;
        }

        PaymentFile paymentFile = paymentFileService.getById(payFilieId);
        if (paymentFile==null){
            result.put("status",-3);
            return result;
        }
        RegApplication regApplication = regApplicationService.getByOneInvoiceId(invoice.getId());
        if (regApplication==null ){
            result.put("status",-2);
            return result;
        }

        result.put("regId",regApplication.getId());
        result.put("legalName",invoice.getPayeeName());
        result.put("createdDate",invoice.getCreatedDate()!=null?Common.uzbekistanDateFormat.format(invoice.getCreatedDate()):"");
        result.put("detail",invoice.getDetail());
        result.put("amount",invoice.getAmount());
        result.put("actionUrl",BillingUrls.PaymentFileAllConnectInvoiceSubmit + "?id=" + payFilieId + "&invoiceId=" + invoice.getId() + "&regId=" + regApplication.getId());

        return result;
    }

    @RequestMapping(BillingUrls.PaymentFileAllConnectInvoiceSubmit)
    public String connectInvoiceSubmit(
            @RequestParam(name = "id") Integer paymentFileId,
            @RequestParam(name = "invoiceId") Integer invoiceId,
            @RequestParam(name = "regId") Integer regId
    ){
        User user = userService.getCurrentUserFromContext();
        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile==null){
            return "redirect:" + BillingUrls.PaymentFileAllList + "?failed=-1";
        }

        Invoice invoice = invoiceService.getInvoice(invoiceId);
        if (invoice==null){
            return "redirect:" + BillingUrls.PaymentFileAllList + "?failed=-2";
        }
        RegApplication regApplication = regApplicationService.getById(regId);
        if (regApplication==null || regApplication.getInvoiceId()==null || !regApplication.getInvoiceId().equals(invoiceId)){
            return "redirect:" + BillingUrls.PaymentFileAllList + "?failed=-3";
        }

        paymentFile.setInvoice(invoice.getInvoice());
        paymentFileService.update(paymentFile,user.getId());
        Payment payment  = paymentService.pay(invoice.getId(), paymentFile.getAmount(), paymentFile.getPaymentDate(), paymentFile.getDetails(), PaymentType.BANK);
        payment.setMessage("qo'lda biriktirildi. Invoice kelmagan yoki norog'tri kelgan update userId=" + user.getId().toString());
        paymentService.save(payment);
        invoiceService.checkInvoiceStatus(invoice);

        return "redirect:" + BillingUrls.PaymentFileAllList;
    }
//########################################
    @RequestMapping(BillingUrls.PaymentFileConnectInvoice)
    public String connectInvoiceBilling(
            @RequestParam(name = "id") Integer paymentFileId,
            Model model
    ){

        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile==null || paymentFile.getInvoice()!=null){
            return "redirect:" + BillingUrls.PaymentFileList;
        }

        model.addAttribute("paymentFile",paymentFile);
        model.addAttribute("getLink",BillingUrls.PaymentFileGetInvoice);

        return BillingTemplates.PaymentFileConnectInvoice;

    }


    // status=-1  invoice topilmadi
    // status=-2  ariza topilmadi topilmadi
    @RequestMapping(value = BillingUrls.PaymentFileGetInvoice,method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> getInvoiceDetailsBilling (
            @RequestParam(name = "invoiceStr") String invoiceStr,
            @RequestParam(name = "id") Integer payFilieId
    ){

        HashMap<String,Object> result = new HashMap<>();
        result.put("status",1);
        User user = userService.getCurrentUserFromContext();
        Invoice invoice = invoiceService.getInvoice(invoiceStr);
        if (invoice==null){
            result.put("status",-1);
            return result;
        }

        PaymentFile paymentFile = paymentFileService.getById(payFilieId);
        if (paymentFile==null){
            result.put("status",-3);
            return result;
        }
        RegApplication regApplication = regApplicationService.getByOneInvoiceId(invoice.getId());
        if (regApplication==null || !regApplication.getReviewId().equals(user.getOrganizationId())){
            result.put("status",-2);
            return result;
        }

        result.put("regId",regApplication.getId());
        result.put("legalName",invoice.getPayeeName());
        result.put("createdDate",invoice.getCreatedDate()!=null?Common.uzbekistanDateFormat.format(invoice.getCreatedDate()):"");
        result.put("detail",invoice.getDetail());
        result.put("amount",invoice.getAmount());
        result.put("actionUrl",BillingUrls.PaymentFileConnectInvoiceSubmit + "?id=" + payFilieId + "&invoiceId=" + invoice.getId() + "&regId=" + regApplication.getId());

        return result;
    }

    @RequestMapping(BillingUrls.PaymentFileConnectInvoiceSubmit)
    public String connectInvoiceSubmitBilling(
            @RequestParam(name = "id") Integer paymentFileId,
            @RequestParam(name = "invoiceId") Integer invoiceId,
            @RequestParam(name = "regId") Integer regId
    ){
        User user = userService.getCurrentUserFromContext();
        PaymentFile paymentFile = paymentFileService.getById(paymentFileId);
        if (paymentFile==null){
            return "redirect:" + BillingUrls.PaymentFileList + "?failed=-1";
        }

        Invoice invoice = invoiceService.getInvoice(invoiceId);
        if (invoice==null){
            return "redirect:" + BillingUrls.PaymentFileList + "?failed=-2";
        }
        RegApplication regApplication = regApplicationService.getById(regId);
        if (regApplication==null || regApplication.getInvoiceId()==null || !regApplication.getInvoiceId().equals(invoiceId)){
            return "redirect:" + BillingUrls.PaymentFileList + "?failed=-3";
        }

        paymentFile.setInvoice(invoice.getInvoice());
        Payment payment  = paymentService.pay(invoice.getId(), paymentFile.getAmount(), paymentFile.getPaymentDate(), paymentFile.getDetails(), PaymentType.BANK);
        paymentFile.setPaymentId(payment.getId());
        paymentFileService.update(paymentFile,user.getId());
        payment.setMessage("qo'lda biriktirildi. Invoice kelmagan yoki norog'tri kelgan update userId=" + user.getId().toString());
        paymentService.save(payment);
        invoiceService.checkInvoiceStatus(invoice);

        return "redirect:" + BillingUrls.PaymentFileList;
    }
}