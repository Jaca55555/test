package uz.maroqand.ecology.docmanagment.entity;

import lombok.Data;
import uz.maroqand.ecology.docmanagment.constant.DocumentTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "document_folder")
public class Folder {

    @Transient
    private static final String sequenceName = "document_folder_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    private DocumentTypeEnum type;

    @NotNull
    private String name;

    @Column(name="date", columnDefinition = "timestamp without time zone")
    private Date date;

    //Folder.id
    @Column(name = "parent_id")
    private Integer parentId;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
