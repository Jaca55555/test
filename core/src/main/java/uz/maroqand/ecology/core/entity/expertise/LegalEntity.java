package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "legal_entity")
public class LegalEntity {

    @Transient
    private static final String sequenceName = "legal_entity_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //ИНН
    @Size(max = 9)
    private String tin;

    //Фирменное наименование предприятия;
    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opf_id", updatable = false, insertable = false)
    private Opf opf;

    //Организационно правовая форма предприятия
    @Column(name = "opf_id")
    private Integer opfId;

    //Директор
    @Column(name = "director_full_name")
    private String directorFullName;

    //ОКЭД
    @Size(max = 6)
    private String oked;

    //Регион
    @Column(name = "region_id")
    private Integer regionId;

    //Район
    @Column(name = "sub_region_id")
    private Integer subRegionId;

    //Адрес
    private String address;

    //Рабочий телефон
    @Size(max = 20)
    private String phone;

    //Мобильный телефон (для смс уведомление);
    @Size(max = 20)
    @Column(name = "mobile_phone")
    private String mobilePhone;

    //Электронный адрес;
    private String email;

    //МФО обслуживающего банка;
    @Size(max = 6)
    private String mfo;

    //Наименование обслуживающего банка
    @Column(name = "bank_name")
    private String bankName;

    //Расчетный счет (Основной)
    @Column(name = "bank_account")
    private String bankAccount;

}
