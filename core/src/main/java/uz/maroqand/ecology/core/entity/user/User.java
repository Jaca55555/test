package uz.maroqand.ecology.core.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
@Data
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

    @Column(name = "full_name")
    private String fullName;

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

    //Organization.ID
    /*@NotNull
    @Column(name = "organization_id")
    private Integer organizationId;*/

    /*@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;*/

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles;

    @Transient
    private Integer userAdditionalId;

}
