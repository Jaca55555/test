package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class DateShowService {
    public LocalDateTime dateShow(){
        LocalDateTime sana= LocalDateTime.now();
        return sana;
    }
}
