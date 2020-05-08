package uz.maroqand.ecology.core.entity.billing;

import com.google.gson.Gson;
import uz.maroqand.ecology.core.dto.expertise.InvoiceChange;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Sadullayev Akmal on 04.05.2020.
 * (uz)
 * (ru)
 */
@Entity
@Table(name = "invoice_audit")
public class InvoiceAudit {

    @Transient
    static final String sequenceName = "registry_individual_audit_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id",updatable = false,insertable = false)
    private Invoice invoice;

    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Transient
    private InvoiceChange change;

    @Column(name = "changes_serialized", columnDefinition = "TEXT")
    private String changesSerialized;

    @Column(columnDefinition = "TEXT")
    private String reason;

    /*@Enumerated(value = EnumType.ORDINAL)
    private InvoiceAuditType type;
*/

    //(uz) deleted = FALSE bo'lsa ma'lumot o'chirilmagan bo'ladi, deleted = TRUE bo'lsa ma'lumot o'chirilgan bo'ladi
    //(ru) deleted = FALSE не удаляет данные, deleted = TRUE будет удаляться
    @Column(name = "deleted", columnDefinition = "boolean DEFAULT false")
    private Boolean deleted;

    //(uz) Ma'lumotlar o'zgartirilgan sana va vaqt
    //(ru) Дата и время изменения данных
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP without time zone")
    private Date updatedAt;

    //(uz) Ma'lumotarni o'zgartirgan user
    //(ru) Пользователь, изменяющий информацию пользователя
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by",updatable = false,insertable = false)
    private User updatedBy;

    @Column(name = "updated_by")
    private Integer updatedById;


    public void setChange(InvoiceChange change) {
        Gson gson = new Gson();
        this.change = change;
        this.changesSerialized = gson.toJson(change);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInvoiceChangeId() {
        return invoiceId;
    }

    public void setInvoiceChangeId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoiceChange getChange() {
        return change;
    }

    public String getChangesSerialized() {
        return changesSerialized;
    }

    public void setChangesSerialized(String changesSerialized) {
        this.changesSerialized = changesSerialized;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Integer updatedById) {
        this.updatedById = updatedById;
    }
}
