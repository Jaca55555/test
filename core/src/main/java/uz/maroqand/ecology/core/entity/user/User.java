package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import uz.maroqand.ecology.core.entity.sys.Organization;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
@Getter @Setter
@Entity
@Table(name = "sys_user", indexes = {@Index(columnList = "username")}, uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
public class User {

    @Transient
    private static final String sequenceName = "sys_user_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @NotNull
    @Size(min = 3)
    private String username;

    @NotNull
    @Size(min = 6)
    private String password;

    private String email;

    private String phone;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "middlename")
    private String middlename;

    //True: male, false: female
    @Column(name = "gender")
    private Boolean gender;

    private Boolean enabled;

    @Column(name="date_registered", columnDefinition = "timestamp without time zone")
    private Date dateRegistered;


    //foydalanuvchi fiz yoki yur bo'lsa, fiz inn
    @Column(name = "tin")
    private Integer tin;

    //foydalanuvchi yur bo'lsa yoki NNT bo'lsa, yur inn
    @Column(name = "le_tin")
    private Integer leTin;

    @Column(name = "pinfl")
    private String pinfl;


    //Organization.ID
    @Column(name = "organization_id")
    private Integer organizationId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    //Department.ID
    @Column(name = "department_id")
    private Integer departmentId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    private Position position;

    @Column(name = "position_id")
    private Integer positionId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    //Oxirgi kirgan vaqti
    @Column(name = "last_event", columnDefinition = "timestamp without time zone")
    private Date lastEvent;

    //Document ijro nazorati uchun ma'sul shaxs
    @Column(name = "is_execute_controller", columnDefinition = "boolean DEFAULT false")
    private Boolean isExecuteController;

    //Document qabul qiluvchi rahbar
    @Column(name = "is_execute_chief", columnDefinition = "boolean DEFAULT false")
    private Boolean isExecuteChief;

    //Performer  masul ijrochi
    @Column(name = "is_performer", columnDefinition = "boolean DEFAULT false")
    private Boolean isPerformer;

    //Agreement  kelishish uchun masul
    @Column(name = "is_agreement", columnDefinition = "boolean DEFAULT false")
    private Boolean isAgreement;

    @Transient
    private Integer userAdditionalId;

    @Column(name = "telegram_user_id")
    private Integer telegramUserId;

    @Column(name = "telegram_user_private")
    private Boolean isPrivate;

    //lang selected
    private String lang;

    public String getFullName(){
        String fullName = " ";
        if (lastname!=null) fullName+=lastname + " ";
        if (firstname!=null) fullName+=firstname+" ";
        if (middlename!=null) fullName+=middlename+" ";
        return fullName;
    }
    public String getFName(){
        String fullName = " ";
        if (firstname!=null) fullName+=firstname+" ";
        if (lastname!=null) fullName+=lastname + " ";
        return fullName;
    }
    public String getShortName(){
        String shortName = " ";
        if (firstname!=null) shortName+=firstname.substring(0,1).toUpperCase() + ".";
        if (middlename!=null) shortName+=middlename.substring(0,1).toUpperCase() + ".";
        if (lastname!=null) shortName+=lastname+" ";
        return shortName;
    }



    public Boolean getPrivate(){
        return isPrivate;
    }

}
