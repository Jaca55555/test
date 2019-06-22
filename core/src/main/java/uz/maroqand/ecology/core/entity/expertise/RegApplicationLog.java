package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "reg_application_log")
public class RegApplicationLog {

    @Transient
    private static final String sequenceName = "reg_application_log_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", insertable = false, updatable = false)
    private RegApplication regApplication;

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    //kelib tushgan sana
    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by")
    private Integer createdById;

    //ko'rib chiqilgan sana
    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", insertable = false, updatable = false)
    private User updateBy;

    @Column(name = "update_by")
    private Integer updateById;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private LogType type;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private LogStatus status;

    @Size(max = 1000)
    @Column(name = "comment",columnDefinition = "TEXT")
    private String comment;

    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

}
