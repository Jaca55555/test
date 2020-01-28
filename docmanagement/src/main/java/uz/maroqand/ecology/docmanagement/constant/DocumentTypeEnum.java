package uz.maroqand.ecology.docmanagement.constant;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
public enum DocumentTypeEnum {
    IncomingDocuments(1,"doc.doc_type_incoming"), //Входящие документы
    OutgoingDocuments(2,"doc.doc_type_outgoing"); //Исходящие документы

    private Integer id;
    private String name;

    DocumentTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<DocumentTypeEnum> getList() {
        List<DocumentTypeEnum> documentSubType = new LinkedList<>();
        Collections.addAll(documentSubType, DocumentTypeEnum.values());
        return documentSubType;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
