package uz.maroqand.ecology.core.entity.expertise;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 27.01.2010.
 * (uz)
 * (ru) СЧЕТ-ФАКТУРА
 */
@Getter
@Setter
@Entity
@Table(name = "facture")
public class Facture {

    @Transient
    private static final String sequenceName = "facture_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //sana
    @Column(name = "date")
    private Date date;

    //raqami, №
    @Column(name = "number")
    private String number;

    //
    @Column(name = "doc_date")
    private Date docDate;

    //
    @Column(name = "doc_number")
    private String docNumber;

    //
    @Column(name = "amount")
    private Double amount;

    /*
    * payee
    * */
    //Поставщик:
    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "payee_address")
    private String payeeAddress;

    @Column(name = "payee_tin")
    private String payeeTin;

    @Column(name = "payee_vat")
    private String payeeVAT;

    @Column(name = "payee_director")
    private String payeeDirector;

    @Column(name = "payee_manager")
    private String payeeManager;

    @Column(name = "product_released")
    private String productReleased;

    /*
    * payer
    * */
    //Покупатель:
    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_address")
    private String payerAddress;

    @Column(name = "payer_tin")
    private String payerTin;

    @Column(name = "payer_vat")
    private String payerVAT;

}
