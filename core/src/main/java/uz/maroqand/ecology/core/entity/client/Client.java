package uz.maroqand.ecology.core.entity.client;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.entity.sys.Soato;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz) Arizachi ma'lumotlari
 */
@Data
@Entity
@Table(name = "client")
public class Client {

    @Transient
    private static final String sequenceName = "client_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private ApplicantType type;

    //ИНН
    @Size(max = 9)
    private String tin;

    //Фирменное наименование предприятия;
    //Ф.И.О.;
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

    /*
    * Passport ma'lumotlari
    * */
    //Серия паспорта;
    @Size(max = 3)
    @Column(name = "passport_serial")
    private String passportSerial;

    //Номер паспорта;
    @Size(max = 10)
    @Column(name = "passport_number")
    private String passportNumber;

    //Дата выдачи паспорта;
    @Column(name="passport_date_of_issue", columnDefinition = "date")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date passportDateOfIssue;

    //Действителен до;
    @Column(name="passport_date_of_expiry", columnDefinition = "date")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date passportDateOfExpiry;

    //Кем выдан;
    @Column(name = "passport_issued_by")
    private String passportIssuedBy;

    //ОКЭД
    @Size(max = 6)
    private String oked;

    //Регион
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private Soato region;

    @Column(name = "region_id")
    private Integer regionId;

    //Район
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_region_id", updatable = false, insertable = false)
    private Soato subRegion;

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
