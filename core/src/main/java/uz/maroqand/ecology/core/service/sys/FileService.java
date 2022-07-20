package uz.maroqand.ecology.core.service.sys;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;


/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface FileService {

    File findById(Integer fileId);
    List<File> findListByRegApplicationId(Integer fileId);

    ResponseEntity<Resource> getFileAsResourceForDownloading(File file);

    public ResponseEntity<Resource> getFileAsResourceForView(File file);

    File uploadFile(MultipartFile multipartFile, Integer userId, String title, String description);

    File findByIdAndUploadUserId(Integer id, Integer userId);

    String getPathForUpload();

    File getByName(String name);

    File save(File file);
    java.io.File renderPdf(String htmlText) throws IOException;

}
