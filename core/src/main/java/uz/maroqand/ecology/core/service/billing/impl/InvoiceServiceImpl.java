package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.ContractType;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Contract;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.billing.InvoiceRepository;
import uz.maroqand.ecology.core.service.billing.ContractService;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.MinWageService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentService paymentService;
    private final ContractService contractService;
    private final OrganizationService organizationService;
    private final HelperService helperService;
    private final MinWageService minWageService;
    private final RegApplicationService regApplicationService;
    private final UserService userService;

    @Autowired
    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            PaymentService paymentService,
            ContractService contractService,
            OrganizationService organizationService,
            HelperService helperService,
            MinWageService minWageService,
            RegApplicationService regApplicationService, UserService userService) {
        this.invoiceRepository = invoiceRepository;
        this.paymentService = paymentService;
        this.contractService = contractService;
        this.organizationService = organizationService;
        this.helperService = helperService;
        this.minWageService = minWageService;
        this.regApplicationService = regApplicationService;
        this.userService = userService;
    }

    @Override
    public Invoice create(RegApplication regApplication, Requirement requirement) {
        Organization organization = organizationService.getById(regApplication.getReviewId());

        Invoice invoice = new Invoice();
        invoice = createPayer(regApplication, invoice);
        invoice = createPayee(invoice, organization);
        invoice = invoiceRepository.save(invoice);

        invoice = createInvoice(regApplication, invoice, requirement);
        invoice.setRegisteredAt(new Date());
        invoice = invoiceRepository.save(invoice);
        return invoice;
    }

    @Override
    public Invoice modification(RegApplication regApplication, Invoice invoice, Requirement requirement){
        Organization organization = organizationService.getById(regApplication.getReviewId());
        invoice = createPayer(regApplication, invoice);
        invoice = createPayee(invoice, organization);

        MinWage minWage = minWageService.getMinWage();
        Double amountAfter = requirement.getQty() * minWage.getAmount();
        System.out.println("amountAfter="+amountAfter);
        if(!invoice.getAmount().equals(amountAfter)){
            Double amount = amountAfter - invoice.getAmount();
            Contract contract = contractService.createByAmount(invoice, requirement, amount, ContractType.ModificationApplication);
            invoice.setAmount(amountAfter);
            invoice.setQty(amountAfter/minWage.getAmount());
        }
        invoice = checkInvoiceStatus(invoice);
        invoice.setUpdatedAt(new Date());
        invoice = invoiceRepository.save(invoice);
        return invoice;
    }

    /*  payee */
    private Invoice createPayee(Invoice invoice, Organization organization) {
        invoice.setPayeeId(organization.getId());
        invoice.setPayeeName(organization.getNameRu());
        invoice.setPayeeAccount(organization.getAccount());
        invoice.setPayeeTin(organization.getTin());
        invoice.setPayeeAddress(organization.getAddress());
        invoice.setPayeeMfo(organization.getMfo());
        return invoice;
    }

    /*  payer   */
    private Invoice createPayer(RegApplication regApplication, Invoice invoice) {
        invoice.setClientId(regApplication.getApplicantId());
        invoice.setPayerName(regApplication.getApplicant().getName());
        return invoice;
    }

    /* invoice  */
    private Invoice createInvoice(RegApplication regApplication, Invoice invoice, Requirement requirement) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 60);
        String materials = helperService.getMaterialShortNames(regApplication.getMaterials(),"oz");

        Contract contract = contractService.create(invoice,requirement, ContractType.NewApplication,regApplication.getAddNds());
        invoice.setAmount(contract.getAmount());
        invoice.setQty(contract.getCost());

        invoice.setInvoice(createInvoiceSerial());
        invoice.setCreatedDate(new Date());
        invoice.setExpireDate(calendar.getTime());
        invoice.setDetail(
                "ID:" + invoice.getInvoice() +
                ", " + materials +
                ", " + invoice.getPayerName() +
                ", " + invoice.getPayeeName()
        );

        invoice.setStatus(InvoiceStatus.Initial);
        invoice.setDeleted(false);
        return invoice;
    }

    /* check */
    @Override
    public Invoice checkInvoiceStatus(Invoice invoice) {
        System.out.println("checkInvoiceStatus");
        System.out.println(invoice.getStatus().name());
        Double paymentAmount = 0.0;
        List<Payment> paymentList = paymentService.getByInvoiceId(invoice.getId());
        for (Payment payment:paymentList){
            if(payment.getStatus().equals(PaymentStatus.Success)){
                paymentAmount += payment.getAmount();
            }
        }
        paymentAmount+=0.50;
        RegApplication regApplication = regApplicationService.getByOneInvoiceId(invoice.getId());
        System.out.println("paymentAmount==" + paymentAmount);
        if (regApplication!=null && regApplication.getCreatedById()!=null && regApplication.getBudget()){
            System.out.println("if1");
            double partialSuccess = invoice.getAmount()*0.15; // budjet tashkiloti 15 % to'lasa
            System.out.println("paymentAmount==" + paymentAmount);
            System.out.println("partialSuccess==" + partialSuccess);
            if (paymentAmount>=partialSuccess && invoice.getAmount()>paymentAmount){
                System.out.println("if2");


                User user = userService.findById(regApplication.getCreatedById());
                if (user!=null){
                    regApplicationService.sendRegApplicationAfterPayment(regApplication,user,invoice, LocaleContextHolder.getLocale().toLanguageTag());
                }
                invoice.setStatus(InvoiceStatus.PartialSuccess);
                invoiceRepository.save(invoice);
                return invoice;
            }
        }

        if(invoice.getAmount() <= paymentAmount){
            System.out.println("if3");

            invoice.setStatus(InvoiceStatus.Success);
            if (regApplication!=null && regApplication.getCreatedById()!=null){
                User user = userService.findById(regApplication.getCreatedById());
                if (user!=null){
                    regApplicationService.sendRegApplicationAfterPayment(regApplication,user,invoice, LocaleContextHolder.getLocale().toLanguageTag());
                }
            }
        }else {
            invoice.setStatus(InvoiceStatus.Initial);
        }

        invoiceRepository.save(invoice);
        return invoice;
    }




    public Invoice payTest(Integer id) {
        Invoice invoice = getInvoice(id);

        invoice.setClosedDate(new Date());
        invoice.setStatus(InvoiceStatus.Success);
        invoice.setUpdatedAt(new Date());
        invoiceRepository.save(invoice);

        Payment payment = paymentService.pay(invoice.getId(), invoice.getAmount(), new Date(), invoice.getDetail(), PaymentType.UPAY);
        payment.setMessage("for test");
        paymentService.save(payment);
        checkInvoiceStatus(invoice);

        return invoice;
    }

    @Override
    public List<Invoice> getListByStatus(InvoiceStatus invoiceStatus) {
        return invoiceRepository.findAllByStatusAndDeletedFalse(invoiceStatus);
    }

    @Override
    public List<Invoice> getListByStatusAndClientId(InvoiceStatus invoiceStatus, Integer clientId) {
        return invoiceRepository.findAllByStatusAndClientIdAndDeletedFalse(InvoiceStatus.Initial,clientId);
    }

    @Override
    public Invoice cancelInvoice(Invoice invoice) {
        invoice.setStatus(InvoiceStatus.Canceled);
        invoice.setCanceledDate(new Date());
        invoice = invoiceRepository.save(invoice);

        return invoice;
    }

    public Invoice getInvoice(Integer id) {
        return invoiceRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Double getInvoiceResidualAmount(Invoice invoice) {

        Double amount = invoice.getAmount();
        List<Payment> paymentList = paymentService.getByInvoiceId(invoice.getId());
        for (Payment payment:paymentList) {
            if (payment.getStatus().equals(PaymentStatus.Success) || payment.getStatus().equals(PaymentStatus.AlreadyPaid)){
                amount-=payment.getAmount();
            }
        }
        return amount;
    }

    public Invoice getInvoice(String invoice) {
        return invoiceRepository.findByInvoiceAndDeletedFalse(invoice);
    }

    private String createInvoiceSerial() {
        return String.valueOf(10000000000000L + (long) (new Random().nextDouble() * 89999999999999L));
    }

    public Page<Invoice> findFiltered(
            Date dateBegin,
            Date dateEnd,
            Boolean dateToday,
            Boolean dateThisMonth,
            InvoiceStatus status,
            String invoice,
            String service,
            String detail,
            Integer regionId,
            Integer subRegionId,
            Integer payeeId,
            Integer tin,
            Pageable pageable
    ) {
        return invoiceRepository.findAll(getFilteringSpecification(dateBegin, dateEnd, dateToday, dateThisMonth, status, invoice, service, detail, regionId, subRegionId, payeeId,tin),pageable);
    }

    private static Specification<Invoice> getFilteringSpecification(
            final Date dateBegin,
            final Date dateEnd,
            final Boolean dateToday,
            final Boolean dateThisMonth,
            final InvoiceStatus status,
            final String invoice,
            final String service,
            final String detail,
            final Integer regionId,
            final Integer subRegionId,
            final Integer payeeId,
            final Integer tin
    ) {
        return new Specification<Invoice>() {
            @Override
            public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(dateBegin != null && dateEnd != null){
                    predicates.add(criteriaBuilder.between(root.get("createdDate"), dateBegin ,dateEnd));
                }

                if(dateBegin != null && dateEnd == null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dateBegin));
                }

                if(dateBegin == null && dateEnd != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), dateEnd));
                }

                if(dateToday){
                    //get today date
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.AM_PM, Calendar.AM);
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date today = calendar.getTime();
                    calendar.add(Calendar.DATE, +1);
                    Date tomorrow = calendar.getTime();

                    predicates.add(criteriaBuilder.between(root.get("createdDate"), today, tomorrow));
                }

                if(dateThisMonth){
                    //get first day of month
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.AM_PM, Calendar.AM);
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    Date firstDayOfMonth = calendar.getTime();

                    predicates.add(criteriaBuilder.between(root.get("createdDate"), firstDayOfMonth, Calendar.getInstance().getTime()));
                }

                if(status != null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }

                if(invoice != null){
                    predicates.add(criteriaBuilder.equal(root.get("invoice"), invoice));
                }

                if(detail != null){
                    predicates.add(criteriaBuilder.like(root.get("detail"), "%" + detail + "%"));
                }

                if(regionId != null && subRegionId == null){
                    predicates.add(criteriaBuilder.equal(root.get("client").get("regionId"), regionId));
                }

                if(subRegionId != null){
                    predicates.add(criteriaBuilder.equal(root.get("client").get("subRegionId"), subRegionId));
                }

                if(tin != null){
                    predicates.add(criteriaBuilder.equal(root.get("client").get("tin"), tin));
                }

                if(payeeId != null){
                    predicates.add(criteriaBuilder.equal(root.get("payeeId"), payeeId));
                }


                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}