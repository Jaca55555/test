package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "performer_history")
public class PerformerHistory {
    @Transient
    private static final String sequenceName = "performer_history_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    @Column(name = "before_performer")
    private Integer beforePerformer;


    @Column(name = "after_performer")
    private Integer afterPerformer;


    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column
    private Integer applicationNumber;

    @Column(name = "created_by")
    private Integer createdById;


}
