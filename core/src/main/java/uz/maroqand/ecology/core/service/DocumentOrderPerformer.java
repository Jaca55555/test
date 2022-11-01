package uz.maroqand.ecology.core.service;

import uz.maroqand.ecology.core.entity.DocumentOrder;

/**
 * Created by Utkirbek Boltaev on 24.07.2018.
 * (uz)
 * (ru)
 */
public interface DocumentOrderPerformer {

    boolean performDocumentOrder(DocumentOrder order) throws Exception;
}
