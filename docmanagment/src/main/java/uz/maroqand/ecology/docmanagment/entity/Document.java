package uz.maroqand.ecology.docmanagment.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz) Kiruvchi va chiquvchi hujjatlarni ro'yxatdan o'tkazish uchun
 * (ru)
 */
@Data
@Entity
@Table(name = "document")
public class Document {

    @Transient
    private static final String sequenceName = "client_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //Тип документа
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", insertable = false, updatable = false)
    private DocumentType documentType;

    @Column(name = "document_type_id")
    private Integer documentTypeId;

    //Журнал регистраци
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", insertable = false, updatable = false)
    private Journal journal;

    @Column(name = "journal_id")
    private Integer journalId;


}
