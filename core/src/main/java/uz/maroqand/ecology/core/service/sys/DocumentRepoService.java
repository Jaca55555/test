package uz.maroqand.ecology.core.service.sys;

import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz)
 * (ru)
 */
public interface DocumentRepoService {

    DocumentRepo getDocument(Integer id);

    DocumentRepo getDocumentByUuid(String uuid);

    DocumentRepo create(DocumentRepoType type, Integer applicationId);
    byte[] getQRImage(Integer id);

}
