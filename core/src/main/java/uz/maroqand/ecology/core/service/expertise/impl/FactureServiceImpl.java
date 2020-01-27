package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Facture;
import uz.maroqand.ecology.core.repository.expertise.FactureRepository;
import uz.maroqand.ecology.core.service.expertise.FactureService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    public FactureServiceImpl(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    public Facture getById(Integer id){
        return factureRepository.getOne(id);
    }

    public Page<Facture> findFiltered(
            Date dateBegin,
            Date dateEnd,
            Boolean dateToday,
            Boolean dateThisMonth,
            String invoice,
            String service,
            String detail,
            Integer regionId,
            Integer subRegionId,
            Integer payeeId,
            Pageable pageable
    ) {
        return factureRepository.findAll(getFilteringSpecification(dateBegin, dateEnd, dateToday, dateThisMonth, invoice, service, detail, regionId, subRegionId, payeeId),pageable);
    }

    private static Specification<Facture> getFilteringSpecification(
            final Date dateBegin,
            final Date dateEnd,
            final Boolean dateToday,
            final Boolean dateThisMonth,
            final String invoice,
            final String service,
            final String detail,
            final Integer regionId,
            final Integer subRegionId,
            final Integer payeeId
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
