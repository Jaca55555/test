package uz.maroqand.ecology.core.service.billing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.MinWage;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.repository.billing.InvoiceRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.MinWageService;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MinWageService minWageService;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MinWageService minWageService) {
        this.invoiceRepository = invoiceRepository;
        this.minWageService = minWageService;
    }

    public Invoice create(RegApplication regApplication){
        Invoice invoice = new Invoice();
//        invoice.set


        return null;
    }

}