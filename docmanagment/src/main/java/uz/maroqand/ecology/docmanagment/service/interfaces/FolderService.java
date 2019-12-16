package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagment.entity.Folder;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
public interface FolderService {

    List<Folder> getFolderList();

    Page<Folder> getFolderList(String name, Pageable pageable);

    Folder create(Folder folder, Integer userId);

}
