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
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ActivityService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.core.util.TinParser;

import java.util.*;

@Controller
public class BillingController {

    private final InvoiceService invoiceService;
    private final HelperService helperService;
    private final UserService userService;
    private final SoatoService soatoService;
    private final PaymentService paymentService;
    private final ClientService clientService;
    private final RequirementService requirementService;
    private final RegApplicationService regApplicationService;
    private final ActivityService activityService;
    private final ToastrService toastrService;

    @Autowired
    public BillingController(InvoiceService invoiceService, HelperService helperService, UserService userService, SoatoService soatoService, PaymentService paymentService, ClientService clientService, RequirementService requirementService, RegApplicationService regApplicationService, ActivityService activityService, ToastrService toastrService){
        this.invoiceService = invoiceService;
        this.helperService = helperService;
        this.userService = userService;
        this.soatoService = soatoService;
        this.paymentService = paymentService;
        this.clientService = clientService;
        this.requirementService = requirementService;
        this.regApplicationService = regApplicationService;
        this.activityService = activityService;
        this.toastrService = toastrService;
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
            @RequestParam(name = "tin", required = false) String tin,
            @RequestParam(name = "subRegionId", required = false) Integer subRegionId,
            Pageable pageable
    ){
        System.out.println("tin=======");
        System.out.println(tin);
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
                userService.isAdmin()?null:user.getOrganizationId(),
                TinParser.trimIndividualsTinToNull(tin),
                pageable
        );

        List<Invoice> invoiceList = invoicePage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(invoiceList.size());
        for (Invoice invoice : invoiceList){
            invoiceService.checkInvoiceStatus(invoice);
            Client client = null;
            if (invoice.getClientId()!=null){
                client = clientService.getById(invoice.getClientId());
            }
            String clientName = "";
            String clientTin = "";
            if (client!=null){
                clientName = client.getName();
                clientTin = client.getTin()!=null?client.getTin().toString():"";
            }
            convenientForJSONArray.add(new Object[]{
                invoice.getId(),
                invoice.getInvoice(),
                invoice.getPayeeId() != null ? helperService.getOrganizationName(invoice.getPayeeId(), locale) : "",
                invoice.getAmount(),
                Common.uzbekistanDateAndTimeFormat.format(invoice.getCreatedDate()),
                invoice.getStatus(),
                clientName + "  <br/>" + clientTin
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
                        payment.getAmount(),
                        payment.getId(),
                        payment.getInvoiceId()
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

        RegApplication regApplication = regApplicationService.getByOneInvoiceId(invoice.getId());
        List<Category> categoryList = new LinkedList<>();
        List<Requirement> requirementList = null;
        if (regApplication!=null && regApplication.getObjectId()!=null){
            requirementList = requirementService.getRequirementExpertise(regApplication.getObjectId());
        }
       if (requirementList!=null){
           for(Requirement requirement: requirementList){
               categoryList.add(requirement.getCategory());
           }
       }
        List<Activity> activityList = activityService.getByInCategory(categoryList);
        Collections.sort(categoryList);
        result.put("activityList", activityList);
        result.put("activityListSize", activityList.size());
        result.put("categoryList", categoryList);
        result.put("regApplicationId", regApplication!=null?regApplication.getId():null);

        result.put("nowObjectName", regApplication!=null && regApplication.getObjectId()!=null ? helperService.getObjectExpertise(regApplication.getObjectId(), locale):"0");
        result.put("nowActivityName",regApplication!=null &&  regApplication.getActivityId()!=null? helperService.getActivity(regApplication.getActivityId(), locale):"0");
        result.put("nowCategoryName", regApplication!=null && regApplication.getCategory()!=null? helperService.getTranslation(regApplication.getCategory().getName(), locale):"0");

        result.put("payments",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.BillingEdit)
    public String getBillingEditMethod(
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "activityId") Integer activityId
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if(regApplication == null){
            toastrService.create(user.getId(), ToastrType.Error, "Xatolik.","Ariza ma'lumotlari topilmadi");
            return "redirect:" + ExpertiseUrls.BillingList;
        }

        Activity activity = null;
        if(activityId!=null){
            activity = activityService.getById(activityId);
        }
        List<Requirement> requirementList;
        Requirement requirement = null;
        if(activity!=null){
            requirementList = requirementService.getRequirementMaterials(regApplication.getObjectId(), activity.getCategory());
        }else {
            requirementList = requirementService.getRequirementExpertise(regApplication.getObjectId());
        }
        if(requirementList.size()==0){
            toastrService.create(user.getId(), ToastrType.Error, "Xatolik.","Ekpertiza obyektlari topilmadi");
            return "redirect:" + ExpertiseUrls.BillingList;
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            toastrService.create(user.getId(), ToastrType.Error, "Xatolik.","Ekpertiza obyekti topilmadi");
            return "redirect:" + ExpertiseUrls.BillingList;
        }
        regApplication.setRequirementId(requirement.getId());
        regApplication.setActivityId(activityId);
        regApplication.setCategory(activity!=null?activity.getCategory():null);

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        invoiceService.modification(regApplication, invoice, requirement);
        regApplicationService.update(regApplication);

        toastrService.create(user.getId(), ToastrType.Info, "O'zgartirish muvaffaqiyatli.","O'zgartirish muvaffaqiyatli amalga oshirildi");
        return "redirect:" + ExpertiseUrls.BillingList;
    }

    @RequestMapping(value = ExpertiseUrls.BillingDelete)
    public String invoiceCancel(@RequestParam(name = "id") Integer id){
        User user = userService.getCurrentUserFromContext();
        Invoice invoice = invoiceService.getInvoice(id);
        if (invoice==null || !invoice.getStatus().equals(InvoiceStatus.Initial)) return "redirect:" + ExpertiseUrls.BillingList;
        invoiceService.cancelInvoice(invoice);
        return "redirect:" + ExpertiseUrls.BillingList;
    }

}
