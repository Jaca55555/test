package uz.maroqand.ecology.core.service.sys;

import com.lowagie.text.DocumentException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;

import java.io.IOException;


/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface FileService {

    File findById(Integer fileId);

    ResponseEntity<Resource> getFileAsResourceForDownloading(File file);

    File uploadFile(MultipartFile multipartFile, Integer userId, String title, String description);

    File findByIdAndUploadUserId(Integer id, Integer userId);

    String getPathForUpload();

    File getByName(String name);

    File save(File file);
    java.io.File renderPdf(String htmlText) throws IOException, DocumentException;

}
