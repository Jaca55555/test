package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 03.02.2020
 */

@Data
@Entity
@Table(name = "document_description")
public class DocumentDescription {
    @Transient
    private static final String sequenceName = "document_description_id_seq";

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
