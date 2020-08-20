package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import java.util.Date;



@Data
@Entity
@Table(name = "document_task_content")
public class DocumentTaskContent {
    @Transient
    private static final String sequenceName = "document_task_content_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "content")
    private String content;
    @Column(name = "content_ru")
    private String contentRu;
    @Column(name = "content_oz")
    private String contentOz;
    @Column(name = "created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false, insertable = false)
    private User user;
    @Column(name = "created_by_id")
    private Integer createdById;
    public String getNameTranslation(String locale){
        switch (locale) {
            case "ru":
                return contentRu;
            case "oz":
                return contentOz;
            default:
                return content;
        }
    }
}

