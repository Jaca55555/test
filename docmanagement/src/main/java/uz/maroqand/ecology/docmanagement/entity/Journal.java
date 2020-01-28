package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "document_journal")
public class Journal {

    @Transient
    private static final String sequenceName = "document_journal_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //Тип документа
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", insertable = false, updatable = false)
    private DocumentType documentType;

    @Column(name = "document_type_id")
    private Integer documentTypeId;

    private String name;

    private String prefix;

    private String suffix;

    //Journal.ID
    private Integer numbering;

    //Ограниченнный доступ (Только для служебного пользования)
    @Column(name = "restricted", columnDefinition = "boolean DEFAULT true")
    private Boolean restricted;

    //(uz) status = TRUE bo'lsa ma'lumotdan foydalanisla bo'ladi(active), status = FALSE bo'lsa ma'lumotdan foydalanish yopilgan(Closed)
    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private Boolean status = true;

    //(uz) deleted=TRUE bo'lsa o'chirilgan bo'ladi, deleted=FALSE bo'lsa aksincha
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
