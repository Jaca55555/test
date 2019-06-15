package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "sys_file")
public class File {

    @Transient
    private static final String sequenceName = "sys_file_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column
    private String name;

    @Column
    private String extension;

    @Column
    private String path;

    @Column
    private Integer size;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Technical fields
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "date_uploaded", columnDefinition = "timestamp without time zone")
    private Date dateUploaded;

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", updatable = false, insertable = false)
    private User uploadedBy;*/

    @Column(name = "uploaded_by")
    private Integer uploadedById;

    @Column(name = "date_deleted", columnDefinition = "timestamp without time zone")
    private Date dateDeleted;

    @Column(name = "deleted_by")
    private Integer deletedById;

}
