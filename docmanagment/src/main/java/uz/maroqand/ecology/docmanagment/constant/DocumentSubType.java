package uz.maroqand.ecology.docmanagment.constant;

import uz.maroqand.ecology.core.constant.expertise.ApplicantType;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public enum DocumentSubType {
    LegalEntity(0,"sys_applicantType.legalEntity"),
    Individual(1,"sys_applicantType.individual"),
    IndividualEnterprise(2,"sys_applicantType.individualEnterprise"),
    ForeignIndividual(3,"sys_applicantType.foreignIndividual");

    private Integer id;
    private String name;

    DocumentSubType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    private static Map<Integer, DocumentSubType> applicantTypeMap;
    static {
        applicantTypeMap = new HashMap<>();
        for (DocumentSubType documentSubType : DocumentSubType.values()) {
            applicantTypeMap.put(documentSubType.getId(), documentSubType);
        }
    }

    public static DocumentSubType getDocumentSubType(Integer id) {
        return applicantTypeMap.get(id);
    }

    public static List<DocumentSubType> getDocumentSubTypeList() {
        List<DocumentSubType> documentSubType = new LinkedList<>();
        Collections.addAll(documentSubType, DocumentSubType.values());
        return documentSubType;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
