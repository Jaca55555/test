package uz.maroqand.ecology.docmanagement.constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
public enum DocumentStatus {
    Initial(0,"document_status.initial"),//kiritish jarayonida
    New(1,"document_status.new"),//ijrochi belgilanmagan
    InProgress(2,"document_status.inProgress");//jarayonda

    private Integer id;
    private String name;

    DocumentStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<DocumentStatus> getDocumentStatusList() {
        List<DocumentStatus> documentStatusList = new LinkedList<>();
        for (DocumentStatus documentStatus : DocumentStatus.values()) {
            documentStatusList.add(documentStatus);
        }
        return documentStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
