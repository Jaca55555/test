package uz.maroqand.ecology.docmanagment.entity;

import lombok.Data;
import uz.maroqand.ecology.docmanagment.constant.DocumentTypeEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) Hujjat turlari: Kiruvchi, Chiquvchi, Buyruq, Ichki hujjat
 * (ru) Тип документа: Входящие, Исходящие, Приказ, Внутренние
 */
@Data
@Entity
@Table(name = "document_type")
public class DocumentType {

    @Transient
    private static final String sequenceName = "document_type_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "type")
    private DocumentTypeEnum type;

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

}
