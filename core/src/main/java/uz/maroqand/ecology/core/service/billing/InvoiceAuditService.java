package uz.maroqand.ecology.core.service.billing;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.billing.InvoiceAudit;
import uz.maroqand.ecology.core.repository.billing.InvoiceAuditRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Sadullayev Akmal on 04.05.2020.
 * (uz)
 * (ru)
 */
@Service
public class InvoiceAuditService {

    @Autowired
    private InvoiceAuditRepository invoiceAuditRepository;

    private Gson gson = new Gson();

    public List<InvoiceAudit> getById(Integer id) {
        return invoiceAuditRepository.findByInvoiceId(id);
    }

    public InvoiceAudit findById(Integer id) {
        return invoiceAuditRepository.findById(id).get();
    }

}
