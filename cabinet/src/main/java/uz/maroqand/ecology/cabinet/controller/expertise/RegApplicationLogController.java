package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 23.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class RegApplicationLogController {

    private final FileService fileService;
    private final UserService userService;
    private final RegApplicationLogService regApplicationLogService;

    @Autowired
    public RegApplicationLogController(FileService fileService, UserService userService, RegApplicationLogService regApplicationLogService) {
        this.fileService = fileService;
        this.userService = userService;
        this.regApplicationLogService = regApplicationLogService;
    }

    @RequestMapping(value = ExpertiseUrls.FileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileName,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplicationLog == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplicationLog="+regApplicationLog.getId(),fileName);
        if (file != null) {
            Set<File> fileSet = regApplicationLog.getDocumentFiles();
            fileSet.add(file);
            regApplicationLogService.updateDocument(regApplicationLog, user);

            responseMap.put("name", file.getName());
            responseMap.put("link", ExpertiseUrls.FileDownload + "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(ExpertiseUrls.FileDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId
    ){
        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(value = ExpertiseUrls.FileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplicationLog == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = regApplicationLog.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                regApplicationLogService.updateDocument(regApplicationLog, user);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

}
