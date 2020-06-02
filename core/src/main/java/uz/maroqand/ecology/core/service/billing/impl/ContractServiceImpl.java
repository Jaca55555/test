package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.billing.ContractType;
import uz.maroqand.ecology.core.entity.billing.Contract;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.repository.billing.ContractRepository;
import uz.maroqand.ecology.core.service.billing.ContractService;
import uz.maroqand.ecology.core.service.billing.MinWageService;

/**
 * Created by Utkirbek Boltaev on 14.08.2019.
 * (uz)
 * (ru)
 */

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final MinWageService minWageService;

    @Autowired
    public ContractServiceImpl(ContractRepository contractRepository, MinWageService minWageService) {
        this.contractRepository = contractRepository;
        this.minWageService = minWageService;
    }

    public Contract create(Invoice invoice, Requirement requirement, ContractType contractType, Boolean isNds){
        Contract contract = new Contract();
        contract.setType(contractType);
        contract.setInvoiceId(invoice.getId());
        contract.setRequirementId(requirement.getId());

        MinWage minWage = minWageService.getMinWage();
        Double amount = requirement.getQty() * minWage.getAmount();
        if (isNds!=null && isNds){
            amount=amount*1.15;
        }
        contract.setAmount(amount);
        contract.setCost(requirement.getQty());

        return contractRepository.save(contract);
    }

    public Contract createByAmount(Invoice invoice, Requirement requirement, Double amount, ContractType contractType){
        MinWage minWage = minWageService.getMinWage();

        Contract contract = new Contract();
        contract.setType(contractType);
        contract.setInvoiceId(invoice.getId());
        contract.setRequirementId(requirement.getId());
        contract.setAmount(amount);
        contract.setCost(amount/minWage.getAmount());

        return contractRepository.save(contract);
    }

}