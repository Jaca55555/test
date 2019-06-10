package uz.maroqand.ecology.core.service.sys;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface FileService {

    File findById(Integer fileId);

    ResponseEntity<Resource> getFileAsResourceForDownloading(File file);

    File uploadFile(MultipartFile multipartFile, Integer userId, Integer guiUserId, String description);

    File findByIdAndUploadUserId(Integer id, Integer userId);

    File findByIdAndUploadGuiUserId(Integer id, Integer guiUserId);

    File save(File file);

    String getPathForUpload();

}
