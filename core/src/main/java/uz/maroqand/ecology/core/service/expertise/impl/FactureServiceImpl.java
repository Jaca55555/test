package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Facture;
import uz.maroqand.ecology.core.entity.expertise.FactureProduct;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.repository.expertise.FactureProductRepository;
import uz.maroqand.ecology.core.repository.expertise.FactureRepository;
import uz.maroqand.ecology.core.service.billing.MinWageService;
import uz.maroqand.ecology.core.service.expertise.FactureService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 27.01.2010.
 * (uz)
 */
@Service
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final FactureProductRepository factureProductRepository;
    private final HelperService helperService;
    private final MinWageService minWageService;

    public FactureServiceImpl(FactureRepository factureRepository, FactureProductRepository factureProductRepository, HelperService helperService, MinWageService minWageService) {
        this.factureRepository = factureRepository;
        this.factureProductRepository = factureProductRepository;
        this.helperService = helperService;
        this.minWageService = minWageService;
    }

    public Facture create(
            RegApplication regApplication,
            Client client,
            Organization organization,
            Requirement requirement,
            Invoice invoice,
            String locale
    ){
        Facture facture = new Facture();

        facture.setDate(new Date());

        facture.setDocNumber(regApplication.getId().toString());
        facture.setDocDate(regApplication.getRegistrationDate());

        facture.setPayeeName(organization.getName());
        facture.setPayeeAddress(organization.getFullAddressTranslation(helperService, locale));
        if(organization.getTin()!=null) facture.setPayeeTin(organization.getTin().toString());
        if(organization.getVat()!=null) facture.setPayeeTin(organization.getVat().toString());
        facture.setPayeeDirector(organization.getDirector());
        facture.setPayeeManager(organization.getManager());

        facture.setPayerName(client.getName());
        facture.setPayerAddress(client.getFullAddressTranslation(helperService, locale));
        if(client.getTin()!=null) facture.setPayerTin(client.getTin().toString());
        facture.setPayerVAT("");

        facture.setAmount(invoice.getAmount());

        facture = factureRepository.save(facture);
        facture.setNumber(facture.getId().toString());

        createProduct(facture, requirement, locale);
        return factureRepository.save(facture);
    }

    public FactureProduct createProduct(
            Facture facture,
            Requirement requirement,
            String locale
    ){
        MinWage minWage = minWageService.getMinWage();
        Double amount = requirement.getQty() * minWage.getAmount();

        FactureProduct factureProduct = new FactureProduct();
        factureProduct.setFactureId(facture.getId());
        factureProduct.setNumber(1);
        factureProduct.setName(requirement.getMaterialName());
        if(locale.equals("uz")){
            factureProduct.setUnit("so'm");
        }else {
            factureProduct.setUnit("сум");
        }
        factureProduct.setQty(1);
        factureProduct.setPrice(null);
        factureProduct.setCost(amount);
        factureProduct.setVatPresent(0.0);
        factureProduct.setVatSum(0.0);
        factureProduct.setTotal(amount);

        return factureProductRepository.save(factureProduct);
    }

    @Override
    public Facture getById(Integer id){
        if(id==null) return null;
        return factureRepository.getOne(id);
    }

    @Override
    public List<FactureProduct> getByFactureId(Integer factureid){
        if(factureid==null) return null;
        return factureProductRepository.findByFactureIdOrderByNumberAsc(factureid);
    }

    public Page<Facture> findFiltered(
            Date dateBegin,
            Date dateEnd,
            String number,
            String payerName,
            String payerTin,
            String payeeName,
            String payeeTin,
            Pageable pageable
    ) {
        return factureRepository.findAll(getFilteringSpecification(dateBegin, dateEnd, number, payerName, payerTin, payeeName, payeeTin),pageable);
    }

    private static Specification<Facture> getFilteringSpecification(
            final Date dateBegin,
            final Date dateEnd,
            final String number,
            final String payerName,
            final String payerTin,
            final String payeeName,
            final String payeeTin
    ) {
        return new Specification<Facture>() {
            @Override
            public Predicate toPredicate(Root<Facture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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

                if(number != null){
                    predicates.add(criteriaBuilder.equal(root.get("number"), number));
                }
                if(payerName != null){
                    predicates.add(criteriaBuilder.like(root.get("payerName"), "%" + payerName + "%"));
                }
                if(payerTin != null){
                    predicates.add(criteriaBuilder.equal(root.get("payerTin"), payerTin));
                }
                if(payeeName != null){
                    predicates.add(criteriaBuilder.like(root.get("payeeName"), "%" + payeeName + "%"));
                }
                if(payeeTin != null){
                    predicates.add(criteriaBuilder.equal(root.get("payeeTin"), payeeTin));
                }

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
