package uz.maroqand.ecology.core.entity.billing;

import lombok.Data;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.service.billing.InvoiceService;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "invoice")
public class Invoice {

    @Transient
    private static final String sequenceName = "invoice_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*  payer       */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", updatable = false, insertable = false)
    private Client client;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "payer_name")
    private String payerName;

    /*  payee       */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id", updatable = false, insertable = false)
    private Organization payee;

    @Column(name = "payee_id")
    private Integer payeeId;

    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "payee_account")
    private String payeeAccount;

    @Column(name = "payee_tin")
    private Integer payeeTin;


    @Column(name = "payee_address")
    private String payeeAddress;

    @Column(name = "payee_mfo")
    private String payeeMfo;

    /* invoice  */
    @Column(name = "invoice")
    private String invoice;

    @Column(name = "amount", precision = 20, scale = 2)
    private Double amount;

    @Column(name = "qty", precision = 20, scale = 2)
    private Double qty;

    //invoice yaratilgan sana
    @Column(name = "created_date")
    private Date createdDate;

    //amal qilish muddati
    @Column(name = "expire_date")
    private Date expireDate;

    //invoice yopilgan sana
    @Column(name = "closed_date")
    private Date closedDate;

    //invoice bekor qilingan sana
    @Column(name = "canceled_date")
    private Date canceledDate;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String detail;

    /*  */
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private InvoiceStatus status;

    /*  */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="registered_at", columnDefinition = "timestamp without time zone")
    private Date registeredAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

    @Column(name = "update_by_id")
    private Integer updateById;

}
