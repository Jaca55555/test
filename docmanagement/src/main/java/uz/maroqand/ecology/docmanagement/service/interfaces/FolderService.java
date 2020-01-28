package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.entity.Folder;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
public interface FolderService {

    List<Folder> getFolderList();

    Page<Folder> getFolderList(String name, Pageable pageable);

    Page<Folder> findFiltered(Integer id, String name, String dateBegin, String dateEnd, Pageable pageable);

    Folder create(Folder folder, Integer userId);

    Folder get(Integer id);

    Folder update(Folder folder);
}
