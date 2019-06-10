package uz.maroqand.ecology.core.entity.sys;


import lombok.Data;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_table_history")
public class TableHistory {

    @Transient
    private static final String sequenceName = "sys_table_history_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "type")
    private TableHistoryType type;

    @Column(name = "entity")
    private TableHistoryEntity entity;

    @Column(name = "entity_id")
    private Integer entityId;

    //o'zgarish (before, after)
    @Column(name = "changes_serialized", columnDefinition = "TEXT")
    private String changesSerialized;

    //sabab
    private String message;

    @Column(name="registered_date", columnDefinition = "timestamp without time zone")
    private Date registeredDate;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_additional_id")
    private Integer userAdditionalId;

}
