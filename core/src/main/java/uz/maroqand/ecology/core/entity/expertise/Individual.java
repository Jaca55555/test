package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
@Table(name = "individual")
public class Individual {

    @Transient
    private static final String sequenceName = "individual_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @Size(max = 3)
    @Column(name = "passport_serial")
    private String passportSerial;

    @Size(max = 10)
    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name="passport_date_of_issue", columnDefinition = "date")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date passportDateOfIssue;

    @Column(name="passport_date_of_expiry", columnDefinition = "date")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date passportDateOfExpiry;

    @Column(name = "passport_issued_by")
    private String passportIssuedBy;

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

}
