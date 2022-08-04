package uz.maroqand.ecology.core.service.billing.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.PaymentType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.repository.billing.PaymentFileRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */

@Service
public class PaymentFileServiceImpl implements PaymentFileService {

    private final PaymentFileRepository paymentFileRepository;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private static final Logger logger = LogManager.getLogger(PaymentFileServiceImpl.class);


    @Autowired
    public PaymentFileServiceImpl(PaymentFileRepository paymentFileRepository, InvoiceService invoiceService, PaymentService paymentService) {
        this.paymentFileRepository = paymentFileRepository;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    public PaymentFile getById(Integer id){
        return paymentFileRepository.getOne(id);
    }

    @Override
    public List<PaymentFile> getByInvoice(String invoice) {
        return paymentFileRepository.findByInvoiceAndDeletedFalse(invoice);
    }

    @Override
    public PaymentFile save(PaymentFile paymentFile) {
        return paymentFileRepository.save(paymentFile);
    }

    @Override
    public PaymentFile update(PaymentFile paymentFile, Integer userId) {
        paymentFile.setUpdateById(userId);
        paymentFile.setUpdatedAt(new Date());
        return paymentFileRepository.save(paymentFile);
    }

    public PaymentFile create(PaymentFile paymentFile){
        paymentFile.setCreatedAt(new Date());
        paymentFile.setDeleted(false);
        return paymentFileRepository.save(paymentFile);
    }

    @Override
    public void removeInvoiceIsDublicate(PaymentFile paymentFile, Integer userId) {
        //todo paymentFile ichiga paymentId yozib keyin o'chirishni qilish kerak

    }

    public Page<PaymentFile> findFiltered(
            Date dateBegin,
            Date dateEnd,
            String invoice,
            Integer paymentId,

            Integer payerTin,
            String payerName,
            String details,
            String bankMfo,

            Boolean isComplete,
            String account,
            String oldAccount,
            Integer datefileter,
            Pageable pageable
    ) {
        return paymentFileRepository.findAll(getFilteringSpecification(dateBegin,dateEnd,invoice,paymentId,payerTin,payerName,details,bankMfo,isComplete,account,oldAccount, datefileter),pageable);
    }

    private static Specification<PaymentFile> getFilteringSpecification(
            final Date dateBegin,
            final Date dateEnd,
            final String invoice,
            final Integer paymentId,
            final Integer payerTin,
            final String payerName,
            final String details,
            final String bankMfo,
            final Boolean isComplete,
            final String account,
            final String oldAccount,
            final Integer datafilter

            ) {
        return new Specification<PaymentFile>() {
            @Override
            public Predicate toPredicate(Root<PaymentFile> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(dateBegin != null && dateEnd != null){
                    predicates.add(criteriaBuilder.between(root.get("createdAt"), dateBegin ,dateEnd));
                }

                if(dateBegin != null && dateEnd == null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateBegin));
                }

                if(dateBegin == null && dateEnd != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dateEnd));
                }

                if(invoice != null){
                    predicates.add(criteriaBuilder.equal(root.get("invoice"), invoice));
                }
                if(paymentId != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), paymentId));
                }

                if(payerTin != null){
                    predicates.add(criteriaBuilder.equal(root.get("payerTin"), payerTin));
                }
                if(payerName != null){
                    predicates.add(criteriaBuilder.like(root.get("payerName"), "%" + payerName + "%"));
                }
                if(details != null){
                    predicates.add(criteriaBuilder.like(root.get("details"), "%" + details + "%"));
                }
                if(bankMfo != null){
                    predicates.add(criteriaBuilder.like(root.get("bankMfo"), "%" + bankMfo + "%"));
                }

                if(isComplete != null){
                    if(isComplete){
                        predicates.add(criteriaBuilder.isNotNull(root.get("paymentId")));
                    }else {
                        predicates.add(criteriaBuilder.isNull(root.get("paymentId")));
                    }
                }

                if (datafilter != null){
                    predicates.add(criteriaBuilder.lessThan(root.join("payment").join("invoice").join("client").get("regionId"), 9999));
                }

                if((account != null&& !account.isEmpty()) || (oldAccount != null)){
                    predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("receiverAccount"), "%"+account+"%"), criteriaBuilder.like(root.get("receiverAccount"), "%"+oldAccount+"%")) );
                }

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}