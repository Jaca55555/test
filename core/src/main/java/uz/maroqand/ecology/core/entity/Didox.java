package uz.maroqand.ecology.core.entity;

import lombok.Data;
import uz.maroqand.ecology.core.constant.order.DocumentOrderStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "didox")
public class Didox {
    @Transient
    private static final String sequenceName = "didox_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Enumerated(EnumType.ORDINAL)
    private DocumentOrderStatus status;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @Column(name = "created_by")
    private Integer createdById;

    @Column(name = "update_by")
    private Integer updateById;

}
