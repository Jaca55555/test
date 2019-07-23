package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Invoice create(RegApplication regApplication, Requirement requirement) {
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
        invoice.setAmount(requirement.getQty() * minWage.getAmount());
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
                "ID:" + invoice.getInvoice() +
                        ", " + material.getNameTranslation("ru") +
                        ", " + invoice.getPayerName() +
                        ", " + invoice.getPayeeName());
        invoiceRepository.save(invoice);
        return invoice;
    }

    public Invoice payTest(Integer id) {
        Invoice invoice = getInvoice(id);

        invoice.setClosedDate(new Date());
        invoice.setStatus(InvoiceStatus.Success);
        invoice.setUpdatedAt(new Date());
        invoiceRepository.save(invoice);

        paymentService.pay(invoice.getId(), invoice.getAmount(), new Date(), invoice.getDetail(), PaymentType.UPAY);

        return invoice;
    }

    public Invoice getInvoice(Integer id) {
        return invoiceRepository.findByIdAndDeletedFalse(id);
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
            Pageable pageable
    ) {
        return invoiceRepository.findAll(getFilteringSpecification(dateBegin, dateEnd, dateToday, dateThisMonth, status, invoice, service, detail, regionId, subRegionId, payeeId),pageable);
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
            final Integer payeeId
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

                if(payeeId != null){
                    predicates.add(criteriaBuilder.equal(root.get("payeeId"), payeeId));
                }


                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}