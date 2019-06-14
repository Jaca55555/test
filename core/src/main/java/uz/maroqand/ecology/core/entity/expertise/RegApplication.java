package uz.maroqand.ecology.core.entity.expertise;


import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Entity
@Table(name = "reg_application")
public class RegApplication {

    @Transient
    private static final String sequenceName = "reg_application_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private Applicant applicant;

    @Column(name = "applicant_id")
    private Integer applicantId;

    //Объект экспертизы
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    private ObjectExpertise object;

    @Column(name = "object_id")
    private Integer objectId;

    //Вид деятельности
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    private Activity activity;

    @Column(name = "activity_id")
    private Integer activityId;

    //Категория
    @Column(name = "category")
    @Enumerated(EnumType.ORDINAL)
    private Category category;

    //Вид материалов



    //Наименование объекта
    private String name;

    //разработчика проекта
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", insertable = false, updatable = false)
    private ProjectDeveloper developer;

    @Column(name = "developer_id")
    private Integer developerId;

    //Ko'rib chiquvchi tashkilot
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Organization review;

    @Column(name = "review_id")
    private Integer reviewId;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by")
    private Integer createdById;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", insertable = false, updatable = false)
    private User updateBy;

    @Column(name = "update_by")
    private Integer updateById;

}
