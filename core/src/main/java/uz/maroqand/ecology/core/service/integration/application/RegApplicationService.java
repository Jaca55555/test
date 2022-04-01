package uz.maroqand.ecology.core.service.integration.application;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.dto.api.RegApplicationDTO;
import uz.maroqand.ecology.core.dto.api.ResponseDTO;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.FileService;

import java.io.IOException;

@Service
public class RegApplicationService {

    final ConclusionService conclusionService;
    final FileService fileService;

    public RegApplicationService(ConclusionService conclusionService, FileService fileService) {
        this.conclusionService = conclusionService;
        this.fileService = fileService;
    }


    public ResponseDTO postResponse(RegApplication regApplication) throws IOException {
        ResponseDTO response = new ResponseDTO();

        RegApplicationDTO regApplicationDTO = RegApplicationDTO.fromEntity(regApplication,conclusionService,fileService);

        response.setStatusCode(0);
        response.setData(regApplicationDTO);
        response.setStatusMessage("Conclusion is given");

        return  response;
    }

}
