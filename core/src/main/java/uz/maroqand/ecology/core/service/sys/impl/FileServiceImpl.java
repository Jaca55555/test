package uz.maroqand.ecology.core.service.sys.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.config.GlobalConfigs;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.repository.sys.FileRepository;
import uz.maroqand.ecology.core.service.sys.FileService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private GlobalConfigs globalConfigs;
    private final DateFormat uploadFolderYMFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, GlobalConfigs globalConfigs) {
        this.fileRepository = fileRepository;
        this.globalConfigs = globalConfigs;
    }

    private static Logger logger = LogManager.getLogger(FileServiceImpl.class);

    @Override
    @Cacheable(value = "fileFindById", key = "#id",unless="#result == ''")
    public File findById(Integer id) {
        if(id==null) return null;
        return fileRepository.findByIdAndDeletedFalse(id);
    }

    private Resource getFileAsResource(File file) {
        try {
            Path filePath = Paths.get(file.getPath());
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                logger.error("could not read file: " + file.getPath());

            }
        } catch (Exception e) {
            logger.error("FilesServiceImpl.getFileAsResource", e);
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Resource> getFileAsResourceForDownloading(File file) {
        Resource fileAsResource = getFileAsResource(file);

        //File not found.
        if (fileAsResource == null) return null;

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

        if (file.getSize() != null) {
            headers.add(HttpHeaders.CONTENT_LENGTH, file.getSize().toString());
        }
        if (file.getExtension() != null) {
            switch (file.getExtension().toLowerCase()) {
                case "pdf":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
                    break;
                case "zip":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
                    break;
                case "rar":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/x-rar-compressed");
                    break;
                case "jpeg":
                case "jpg":
                    headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
                    break;
                case "png":
                    headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
                    break;
                case "gif":
                    headers.add(HttpHeaders.CONTENT_TYPE, "image/gif");
                    break;
                case "doc":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/msword");
                    break;
                case "docx":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    break;
                case "xls":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel");
                    break;
                case "xlsx":
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    break;
                default:
                    //Noma'lum fayl turlari uchun shu tip to'g'ri keladi
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            }
        }
        return new ResponseEntity<Resource>(fileAsResource, headers, HttpStatus.OK);
    }

    @Override
    public File uploadFile(MultipartFile multipartFile, Integer userId, String title, String description) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        try {
            Date date = new Date();
            Long dataLong = date.getTime();

            String extension = getExtensionFromFileName(multipartFile.getOriginalFilename());

            String filename = userId + "_" + dataLong + "." + extension;
            String directory = getPathForUpload();
            Path filePath = Paths.get(directory, filename);

            // save the file localy
            Files.copy(multipartFile.getInputStream(), filePath);

            File file = new File();

            file.setUploadedById(userId);
            file.setDateUploaded(new Date());

            file.setName(multipartFile.getOriginalFilename());
            file.setExtension(extension);
            file.setSize(Integer.valueOf(String.valueOf(multipartFile.getSize())));
            file.setTitle(title);
            file.setDescription(description);

            file.setPath(directory + "/" + filename);

            return fileRepository.saveAndFlush(file);
        } catch (Exception e) {
            logger.error("FilesServiceImpl.uploadFile", e);
        }
        return null;
    }

    private String getExtensionFromFileName(String fileName) {
        String extension = fileName;
        int i = extension.lastIndexOf('.');
        if (i >= 0) {
            extension = extension.substring(i + 1);
        } else {
            extension = "";
        }
        return extension;
    }

    private String currentDir = "";

    public synchronized String getPathForUpload() {
        //TODO: check folder
        createAndSetNextUploadFolder();

        return currentDir;
    }

    private void createAndSetNextUploadFolder() {
        String newCurrentDir = globalConfigs.getUploadedFilesFolder()
                + "/" + uploadFolderYMFormat.format(new Date());
        java.io.File root = new java.io.File(newCurrentDir);
        if (!root.exists() || !root.isDirectory()) {
            root.mkdirs();
        }
        currentDir = newCurrentDir;
    }

    public File findByIdAndUploadUserId(Integer id, Integer userId){
        return fileRepository.findByIdAndUploadedByIdAndDeletedFalse(id, userId);
    }

}