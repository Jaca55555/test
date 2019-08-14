package uz.maroqand.ecology.core.entity.billing;

import lombok.Data;
import uz.maroqand.ecology.core.constant.billing.ContractType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 14.08.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "invoice_contract")
public class Contract {

    @Transient
    private static final String sequenceName = "invoice_contract_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private ContractType type;

    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "requirement_id")
    private Integer requirementId;

    private String name;

    @Column(name = "amount", precision = 20, scale = 2)
    private Double amount;

    @Column(name = "qty", precision = 20, scale = 2)
    private Double cost;

    @Column(name = "created_date")
    private Date createdDate;

}
