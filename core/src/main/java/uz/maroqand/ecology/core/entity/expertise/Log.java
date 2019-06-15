package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "log")
public class Log {

    @Transient
    private static final String sequenceName = "log_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //kelib tushgan sana
    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    //ko'rib chiqilgan sana
    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by")
    private Integer createdById;


}
