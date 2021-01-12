package uz.maroqand.ecology.cabinet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.DocumentEditorService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.util.EditorRequest;
import uz.maroqand.ecology.core.util.EditorResponse;

import java.io.IOException;
import java.net.URISyntaxException;


@RestController
public class OnlyOfficeAPIController {
    private final Logger logger = LogManager.getLogger(OnlyOfficeAPIController.class);

    private final FileService fileService;
    private final DocumentEditorService documentEditorService;

    public OnlyOfficeAPIController(FileService fileService, DocumentEditorService documentEditorService) {
        this.fileService = fileService;
        this.documentEditorService = documentEditorService;
    }

    @RequestMapping("/docEditor/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
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

        System.out.println("key==" + callbackRequest.getKey());
        System.out.println("url==" + callbackRequest.getUrl());
        System.out.println("status==" + callbackRequest.getStatus());

        if (callbackRequest.getStatus()!=2 && callbackRequest.getStatus()!=3){
            System.out.println("if1==");
            return ResponseEntity.ok(new EditorResponse(0));
        }
        boolean isSave = documentEditorService.saveFileInputDownload(callbackRequest.getKey(),callbackRequest.getUrl());
        if(isSave){
            System.out.println("isSave==");
            return ResponseEntity.ok(new EditorResponse(0));
        }
        return ResponseEntity.ok(new EditorResponse(-1));
    }
}
