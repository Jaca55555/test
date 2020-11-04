package uz.maroqand.ecology.cabinet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.FileServiceImpl;

import javax.annotation.Resource;


@RestController
public class OnlyOfficeAPIController {
    private final Logger logger = LogManager.getLogger(OnlyOfficeAPIController.class);

    private final FileService fileService;

    public OnlyOfficeAPIController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/docs/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping("/api/fixationCallback")
    public ResponseEntity<CallbackResponse> fixationCallback(@RequestBody CallbackRequest callbackRequest) throws IOException {
        try {
            storageService.processDocument(callbackRequest, true);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new CallbackResponse("-1"));
        }
        return ResponseEntity.ok(new CallbackResponse("0"));
    }
}
