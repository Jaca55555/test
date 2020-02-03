package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;

import javax.persistence.*;

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

}
