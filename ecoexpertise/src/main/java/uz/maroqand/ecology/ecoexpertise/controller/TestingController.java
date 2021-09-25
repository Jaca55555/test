package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/get_post")
public class TestingController {

    @PostMapping
    public ResponseEntity<?> set(@Valid @RequestParam String data) {
        System.out.println("HHHHHHHHHH");


        System.out.println("param="+data);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
