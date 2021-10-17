package uz.maroqand.ecology.cabinet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.service.sys.FileService;

import javax.validation.Valid;

@RestController
@RequestMapping("/get_post")
public class TestingController {

    final
    FileService fileService;

    public TestingController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<?> set(@Valid @RequestParam String data,
    @RequestParam("file") MultipartFile file
    ) {

        fileService.uploadFile(file,1,"test uchun","test uchun desc");
        System.out.println("HHHHHHHHHH");


        System.out.println("param="+data);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
