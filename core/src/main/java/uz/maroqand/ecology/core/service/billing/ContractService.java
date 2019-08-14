package uz.maroqand.ecology.core.service.billing;

import uz.maroqand.ecology.core.constant.billing.ContractType;
import uz.maroqand.ecology.core.entity.billing.Contract;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.Requirement;

/**
 * Created by Utkirbek Boltaev on 14.08.2019.
 * (uz)
 * (ru)
 */
public interface ContractService {

    Contract create(Invoice invoice, Requirement requirement, ContractType contractType);

    Contract createByAmount(Invoice invoice, Requirement requirement, Double amount, ContractType contractType);

}
