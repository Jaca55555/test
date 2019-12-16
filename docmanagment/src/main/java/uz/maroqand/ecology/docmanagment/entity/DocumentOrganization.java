package uz.maroqand.ecology.docmanagment.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) Korxonalar ma'lumotlari(kiruvchi xatlarda yuboruvchi, chiquvchi xatlarda qabul qiluvchi sifatida tanlash uchun)
 * (ru)
 */
@Data
@Entity
@Table(name = "document_organization")
public class DocumentOrganization {

    @Transient
    private static final String sequenceName = "document_organization_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @NotNull
    private String name;

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
