package uz.maroqand.ecology.docmanagement.service.interfaces;

import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz)
 * (ru)
 */
public interface DocumentDescriptionService {

    List<DocumentDescription> getDescriptionList();

    DocumentDescription save(DocumentDescription desc);

}
