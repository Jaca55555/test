package uz.maroqand.ecology.core.entity.user;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 26.09.2018.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_user_login")
public class UserIdGov {

    @Transient
    static final String sequenceName = "sys_user_login_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "user_type")
    private Character userType; // I : fiz lico   L : yur lico

    @Column(name = "validated_via_eds")
    private Boolean isValidatedUsingEDS;

    @Column(name = "username")
    private String username;

    //Passport data
    @Column(name = "pport_number")
    private String passportNumber; //format: AA1234567


    @Column(name = "pport_issue_date")
    private Date passportIssueDate;

    @Column(name = "pport_expire_date")
    private Date passportExpiryDate;

    @Column(name = "pport_issue_place", columnDefinition = "TEXT")
    private String passportIssuePlace;

    @Column(name = "pport_pin")
    private String passportPIN; //PINFL

    //Address
    @Column(name = "address_temporary", columnDefinition = "TEXT")
    private String actualAddress;

    @Column(name = "address_permanent", columnDefinition = "TEXT")
    private String permanentAddress;

    //Name
    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "middlename")
    private String middlename;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private Date birthdate;

    @Column(name = "birth_country")
    private String birthCountry;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "gender")
    private Boolean gender; //true--male false--female

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_phone")
    private String mobilePhone; //format: 998951234567


    //Legal entity data
    @Column(name = "le_name")
    private String legalEntityName;

    @Column(name = "le_tin")
    private String legalEntityTIN;

    @Column(name = "tin")
    private String tin;


    @Column(name = "auth_result")
    private String authenticationResult;

    @Column(name = "ws_list", columnDefinition = "TEXT")
    private String webServicesList;

    @Column(name = "role_list", columnDefinition = "TEXT")
    private String roleListAsJSON;

    @Column(name = "session_id", columnDefinition = "TEXT")
    private String sessionId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    public void setFemale(Boolean isFemale) {
        gender = !isFemale;
        //true--male false--female
    }

}
