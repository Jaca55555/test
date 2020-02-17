package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 30.04.2019.
 * (uz) Хужжат тури
 * (ru) Постановление, Закон, Письмо, Указ, Жалоба, Протокол
 */
@Data
@Entity
@Table(name = "document_view")
public class DocumentView {

    @Transient
    private static final String sequenceName = "document_view_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    //(uz) status = TRUE bo'lsa ma'lumotdan foydalanisla bo'ladi(active), status = FALSE bo'lsa ma'lumotdan foydalanish yopilgan(Closed)
    //(ru) status = TRUE данные могут быть использованы(active), status = FALSE данные закрываются(Closed)
    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private Boolean status = true;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "uz":
                return name;
            case "ru":
            default:
                return nameRu;
        }
    }

}
