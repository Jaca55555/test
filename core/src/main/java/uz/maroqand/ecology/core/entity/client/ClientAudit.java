package uz.maroqand.ecology.core.entity.client;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "client_audit")
public class ClientAudit {

    @Transient
    private static final String sequenceName = "client_audit_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(name = "client_id", updatable = false, insertable = false)
    private Client client;

    @Column(name = "client_id")
    private Integer clientId;

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
