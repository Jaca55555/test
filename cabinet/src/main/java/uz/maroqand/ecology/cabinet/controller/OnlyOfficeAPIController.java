package uz.maroqand.ecology.cabinet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.util.EditorRequest;
import uz.maroqand.ecology.core.util.EditorResponse;

import java.io.IOException;


@RestController
public class OnlyOfficeAPIController {
    private final Logger logger = LogManager.getLogger(OnlyOfficeAPIController.class);

    private final FileService fileService;

    public OnlyOfficeAPIController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("/onlyoffice")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam(name = "filename" ) String filename) {
        System.out.println("serveFile");
        System.out.println("filename" + filename);
        File file = fileService.getByName(filename);
        if (file==null){
            return null;
        }
        return fileService.getFileAsResourceForDownloading(file);
    }

    @RequestMapping("/onlyoffice/fixationCallback")
    public ResponseEntity<EditorResponse> fixationCallback(@RequestBody EditorRequest callbackRequest) throws IOException {
        System.out.println(callbackRequest.getKey());
        System.out.println(callbackRequest.getUrl());
        System.out.println(callbackRequest.getStatus());
        /*try {

//            fileService.processDocument(callbackRequest, true);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new EditorResponse(-1));
        }*/
        return ResponseEntity.ok(new EditorResponse(0));
    }
}
