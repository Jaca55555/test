package uz.maroqand.ecology.core.entity.billing;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "payment_file")
public class PaymentFile {

    @Transient
    private static final String sequenceName = "payment_file_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", updatable = false, insertable = false)
    private Payment payment;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "invoice")
    private String invoice;

    @Column(name = "bank_id")
    private Long bankId;

    //Тўловчини СТИРИ
    @Column(name = "payer_tin")
    private Integer payerTin;

    //Тўловчини номи
    @Column(name = "payer_name")
    private String payerName;

    //Тўловчини ҳисоб рақами
    @Column(name = "bank_account")
    private String bankAccount;

    //Тўловчининг банк МФОси
    @Column(name = "bank_mfo")
    private String bankMfo;

    //qabul qiluvchi СТИРИ
    @Column(name = "receiver_inn")
    private Integer receiverInn;

    //Qabul qiluvchi номи
    @Column(name = "receiver_name")
    private String receiverName;

    //qabul qiluvchi ҳисоб рақами
    @Column(name = "receiver_account")
    private String receiverAccount;

    //Тўловчининг банк МФОси
    @Column(name = "receiver_mfo")
    private String receiverMfo;

    //Тўлов суммаси
    private Double amount;

    //Тўлов суммаси
    @Column(name = "amount_original")
    private String amountOriginal;

    //Документ рақами
    @Column(name = "document_number")
    private String documentNumber;

    //Тўлов санаси ва вақти
    @Column(name = "payment_date")
    private Date paymentDate;

    //Тўлов топшириқномаси
    private String details;

    /*  */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

}
