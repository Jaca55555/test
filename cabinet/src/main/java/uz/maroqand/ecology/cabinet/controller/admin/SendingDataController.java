package uz.maroqand.ecology.cabinet.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.SendingData;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SendingDataService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SendingDataController {

    private final SendingDataService sendingDataService;
    private final FileService fileService;


    public SendingDataController(SendingDataService sendingDataService, FileService fileService) {
        this.sendingDataService = sendingDataService;
        this.fileService = fileService;
    }


    @GetMapping(value = MgmtUrls.SendingDataList)
    public String getList(){
        return "mgmt/sending_data/list";
    }

    @GetMapping(value = MgmtUrls.SendingDataAjaxList)
    @ResponseBody
    public Map<String, Object> getSendingDataAjaxList(Pageable pageable) {

        Page<SendingData> list = sendingDataService.getAjaxList(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", list.getTotalElements()); //Total elements
        map.put("recordsFiltered", list.getTotalElements()); //Filtered elements
        List<Object[]> convenientForJSONArray = new ArrayList<>(list.getContent().size());
        for (SendingData sendingData: list.getContent()){

            File file = sendingData.getFileId()!=null?fileService.findById(sendingData.getFileId()):null;
            convenientForJSONArray.add(new Object[]{
                    sendingData.getId(),
                    sendingData.getDataSend(),
                    sendingData.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(sendingData.getCreatedAt()):"",
                    file!=null? file.getName():" ",
                    sendingData.getErrors(),
                    sendingData.getDeliveryStatus()
            });
        }
        map.put("data", convenientForJSONArray);

        return map;
    }



}
