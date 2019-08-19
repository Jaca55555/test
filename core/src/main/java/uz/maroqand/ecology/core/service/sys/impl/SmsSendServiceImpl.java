package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.SmsSendStatus;
import uz.maroqand.ecology.core.entity.sys.SmsSend;
import uz.maroqand.ecology.core.repository.sys.SmsSendRepository;
import uz.maroqand.ecology.core.service.sys.SmsSendService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class SmsSendServiceImpl implements SmsSendService {

    private final SmsSendRepository smsSendRepository;

    @Autowired
    public SmsSendServiceImpl(SmsSendRepository smsSendRepository) {
        this.smsSendRepository = smsSendRepository;
    }

    @Override
    public SmsSend save(SmsSend smsSend) {
        smsSend.setCreatedAt(new Date());
        return smsSendRepository.save(smsSend);
    }

    @Override
    public SmsSend getRegApplicationId(Integer id) {
        return smsSendRepository.findTop1ByRegApplicationIdOrderByIdDesc(id);
    }

    @Override
    public void update(SmsSend smsSend) {
        smsSend.setUpdateAt(new Date());
        smsSendRepository.save(smsSend);
    }

    @Override
    public Page<SmsSend> findFiltered(
            Date dateBegin,
            Date dateEnd,
            Integer id,
            String phone,
            String message,
            SmsSendStatus status,
            Pageable pageable) {
        return smsSendRepository.findAll(getFilteringSpecification(dateBegin,dateEnd,id,phone,message,status),pageable);
    }

    private static Specification<SmsSend> getFilteringSpecification(
            final Date dateBegin,
            final Date dateEnd,
            final Integer id,
            final String phone,
            final String message,
            final SmsSendStatus status
    ) {
        return new Specification<SmsSend>() {
            @Override
            public Predicate toPredicate(Root<SmsSend> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                System.out.println("dateBegin:"+dateBegin);
                System.out.println("dateEnd:"+dateEnd);
                System.out.println("id:"+id);
                System.out.println("phone:"+phone);
                System.out.println("message:"+message);
                System.out.println("status:"+status);

                if (dateBegin != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));
                }
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if (phone != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("phone"), "%" + phone + "%"));
                }
                if (message != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("message"), "%" + message + "%"));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                // Show only registered and non-deleted
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                return overAll;
            }
        };
    }

    public String getPhoneNumber(String phone){
        String phonePrefix = "998";
        phone.trim();
        phone = phone.replaceFirst("\\+","");
        phone = phone.replaceFirst("\\(","");
        phone = phone.replaceFirst("\\)","");
        phone = phone.replaceAll("\\_","");
        phone = phone.replaceAll("\\-","");
        phone = phone.replaceAll("\\ ","");
        //misol 5874589
        if (phone.length()<9){
            return "";
        }

        if (phone.length()==12){
            String prefix=phone.substring(3,5);
            if (       prefix.equals("90")
                    || prefix.equals("91")
                    || prefix.equals("93")
                    || prefix.equals("94")
                    || prefix.equals("97")
                    || prefix.equals("99")){
                System.out.println("pref=" + prefix);
                return phone;
            }else{
                return "";
            }
        }

        if (phone.length() == 9){
            String prefix=phone.substring(0,2);
            if (       prefix.equals("90")
                    || prefix.equals("91")
                    || prefix.equals("93")
                    || prefix.equals("94")
                    || prefix.equals("97")
                    || prefix.equals("99")){
                System.out.println("pref=" + prefix);
                return phonePrefix + phone;
            }else{
                return "";
            }
        }

        return "";
    }

}
