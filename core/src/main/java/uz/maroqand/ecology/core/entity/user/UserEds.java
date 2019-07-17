package uz.maroqand.ecology.core.entity.user;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 15.08.2018.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_user_eds")
public class UserEds {

    @Transient
    static final String sequenceName = "sys_user_eds_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",updatable = false,insertable = false)
    private User user;

    @Column(name = "user_id")
    private Integer userId;

    // param: CN
    @Column(name = "full_name")
    private String fullName;

    // param: Name
    private String firstname;

    // param: SURNAME
    private String lastname;

    // param: 1.2.860.3.16.1.2
    private String pinfl;

    // param: UID
    private Integer tin;

    // param: 1.2.860.3.16.1.1
    @Column(name = "le_tin")
    private Integer leTin;

    // param: O
    private String owner;

    // param: OU
    private String workplace;

    // param: T
    private String position;

    // param: ST
    private String region;

    // param: L
    private String subRegion;

    // param: C
    private String country;

    // param: E
    private String email;

}
