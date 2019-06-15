package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.repository.billing.InvoiceRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.MinWageService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.expertise.MaterialService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MinWageService minWageService;
    private final PaymentService paymentService;
    private final MaterialService materialService;
    private final OrganizationService organizationService;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MinWageService minWageService, PaymentService paymentService, MaterialService materialService, OrganizationService organizationService) {
        this.invoiceRepository = invoiceRepository;
        this.minWageService = minWageService;
        this.paymentService = paymentService;
        this.materialService = materialService;
        this.organizationService = organizationService;
    }

    public Invoice create(RegApplication regApplication, Requirement requirement){
        Invoice invoice = new Invoice();
        /*  payer   */
        invoice.setClientId(regApplication.getApplicantId());
        invoice.setPayerName(regApplication.getApplicant().getName());

        /*  payee */
        Organization review = organizationService.getById(requirement.getReviewId());
        invoice.setPayeeId(requirement.getReviewId());
        invoice.setPayeeName(review.getNameRu());
        invoice.setPayeeAccount(review.getAccount());
        invoice.setPayeeTin(review.getTin());
        invoice.setPayeeAddress(review.getAddress());
        invoice.setPayeeMfo(review.getMfo());

        /* invoice  */
        invoice.setInvoice(createInvoiceSerial());
        MinWage minWage = minWageService.getMinWage();
        invoice.setAmount(requirement.getQty()*minWage.getAmount());
        invoice.setQty(requirement.getQty());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 60);

        invoice.setCreatedDate(new Date());
        invoice.setExpireDate(calendar.getTime());

        invoice.setStatus(InvoiceStatus.Initial);
        invoice.setDeleted(false);
        invoice.setRegisteredAt(new Date());

        Material material = materialService.getById(requirement.getMaterialId());

        invoice.setDetail(
                "ID:"+invoice.getInvoice()+
                ", "+material.getNameTranslation("ru")+
                ", "+invoice.getPayerName()+
                ", "+invoice.getPayeeName());

        return invoice;
    }

    public Invoice payTest(Integer id){
        Invoice invoice = getInvoice(id);

        invoice.setClosedDate(new Date());
        invoice.setStatus(InvoiceStatus.Success);
        invoice.setUpdatedAt(new Date());
        invoiceRepository.save(invoice);

        Payment payment = new Payment();
        payment.setInvoiceId(invoice.getId());
        payment.setAmount(invoice.getAmount());
        payment.setPaymentDate(new Date());
        payment.setDetail(invoice.getDetail());

        payment.setType(PaymentType.BANK);
        payment.setStatus(PaymentStatus.Success);

        payment.setDeleted(true);
        payment.setRegisteredAt(new Date());

        paymentService.pay(payment);

        return invoice;
    }

    public Invoice getInvoice(Integer id){
        return invoiceRepository.getOne(id);
    }

    private String createInvoiceSerial() {
        return String.valueOf(10000000000000L + (long) (new Random().nextDouble() * 89999999999999L));
    }

}