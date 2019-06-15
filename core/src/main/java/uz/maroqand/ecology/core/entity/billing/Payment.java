package uz.maroqand.ecology.core.entity.billing;

import lombok.Data;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.billing.PaymentType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Transient
    private static final String sequenceName = "payment_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*  Invoice */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", updatable = false, insertable = false)
    private Invoice invoice;

    @Column(name = "invoice_id")
    private Integer invoiceId;


    /*  Card     */
    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_exdate")
    private String cardExdate;


    /*  UPAY    */
    @Column(name = "temp_trans_id")
    private String tempTransId;

    @Column(name = "message")
    private String message;

    @Column(name = "upay_trans_id")
    private String upayTransId;

    @Column(name = "payment_performed_time")
    private String paymentPerformedTime;


    /*  Bank    */
    @Column(name = "bank_trans_id")
    private String bankTransId;

    @Column(name = "bank_mfo")
    private String bankMfo;

    @Column(name = "bank_inn")
    private String bankInn;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_bmm_number")
    private String bankBmmNumber;

    @Column(name = "request_id")
    private String requestId;

    /*  */
    //to'lov sanasi
    @Column(name="payment_date", columnDefinition = "timestamp without time zone")
    private Date paymentDate;

    /*  */
    //to'lov turi
    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private PaymentType type;

    //to'lov statusi
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus status;

    /*  */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="registered_at", columnDefinition = "timestamp without time zone")
    private Date registeredAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

}
