package uz.maroqand.ecology.core.entity;




import uz.maroqand.ecology.core.constant.order.DocumentOrderStatus;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 24.07.2018.
 * (uz)
 * (ru)
 */
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

    @OneToOne
    @JoinColumn(name = "ordered_by")
    private User orderedBy;

    @Column(name = "registered_at", columnDefinition = "timestamp without time zone")
    private Date registeredAt;

    private Boolean deleted;

    @Column(name = "started_at", columnDefinition = "timestamp without time zone")
    private Date startedAt;

    @Column(name = "finished_at", columnDefinition = "timestamp without time zone")
    private Date finishedAt;

    public void merge(DocumentOrder other) {
        this.id = other.id != null ? other.id : this.id;
        this.type = other.type != null ? other.type : this.type;
        this.status = other.status != null ? other.status : this.status;
        this.params = other.params != null ? other.params : this.params;
        this.fileName = other.fileName != null ? other.fileName : this.fileName;
        this.locale = other.locale != null ? other.locale : this.locale;
        this.size = other.size != null ? other.size : this.size;
        this.orderedBy = other.orderedBy != null ? other.orderedBy : this.orderedBy;
        this.registeredAt = other.registeredAt != null ? other.registeredAt : this.registeredAt;
        this.deleted = other.deleted != null ? other.deleted : this.deleted;
        this.startedAt = other.startedAt != null ? other.startedAt : this.startedAt;
        this.finishedAt = other.finishedAt != null ? other.finishedAt : this.finishedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DocumentOrderType getType() {
        return type;
    }

    public void setType(DocumentOrderType type) {
        this.type = type;
    }

    public DocumentOrderStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentOrderStatus status) {
        this.status = status;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public User getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(User orderedBy) {
        this.orderedBy = orderedBy;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
