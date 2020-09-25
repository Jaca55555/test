package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 * (ru)Вид доставки документа
 */
@Data
@Entity
@Table(name = "document_communication_tool")
public class CommunicationTool {

    @Transient
    private static final String sequenceName = "document_communication_tool_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @NotNull
    private String name;

    //(uz) status = TRUE bo'lsa ma'lumotdan foydalanisla bo'ladi(active), status = FALSE bo'lsa ma'lumotdan foydalanish yopilgan(Closed)
    //(ru) status = TRUE данные могут быть использованы(active), status = FALSE данные закрываются(Closed)
    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private Boolean status = true;

    /*
     * Technical Fields
     */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false, insertable = false)
    private User user;
    @Column(name = "created_by_id")
    private Integer createdById;


    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

}
