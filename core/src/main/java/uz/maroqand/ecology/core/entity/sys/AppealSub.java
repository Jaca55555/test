package uz.maroqand.ecology.core.entity.sys;


import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 11.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_appeal_sub")
public class AppealSub {

    @Transient
    private static final String sequenceName = "sys_appeal_sub_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id",insertable = false,unique = false)
    private Appeal appeal;*/

    @Column(name = "appeal_id")
    private Integer appealId;

    @Column(columnDefinition = "TEXT")
    private String message;

    /*
    * Technical Fields
    */
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "admin_write", columnDefinition = "boolean DEFAULT false")
    private Boolean adminWrite = false;

    @Column(name = "in_progress", columnDefinition = "boolean DEFAULT false")
    private Boolean inProgress = false;

    @Column(name = "closed", columnDefinition = "boolean DEFAULT false")
    private Boolean closed = false;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",insertable = false,updatable = false)
    private User createdBy;*/

    @Column(name = "created_by")
    private Integer createdById;

    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
