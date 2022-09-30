package uz.maroqand.ecology.core.entity;

import lombok.Data;
import uz.maroqand.ecology.core.dto.excel.DocumentOrderStatus;
import uz.maroqand.ecology.core.dto.excel.DocumentOrderType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 18.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_document_orders")
public class DocumentOrder {

    @Transient
    private static final String sequenceName = "sys_document_orders_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Enumerated(EnumType.ORDINAL)
    private DocumentOrderType type;

    @Enumerated(EnumType.ORDINAL)
    private DocumentOrderStatus status;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "locale", columnDefinition = "CHARACTER VARYING", length = 2)
    private String locale;

    @Column(name = "size")
    private Long size;

    //Barcha malumotlar
    @Column(name = "all_list_size")
    private Long allListSize;

    //tayyor bo'lgani
    @Column(name = "comp_list_size")
    private Long compListSize;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordered_by",insertable = false, updatable = false)
    private User orderedBy;*/

    @Column(name = "ordered_by")
    private Integer orderedById;

    @Column(name = "registered_at", columnDefinition = "timestamp without time zone")
    private Date registeredAt;

    private Boolean deleted;

    @Column(name = "started_at", columnDefinition = "timestamp without time zone")
    private Date startedAt;

    @Column(name = "finished_at", columnDefinition = "timestamp without time zone")
    private Date finishedAt;

}
