package uz.maroqand.ecology.core.service;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.dto.excel.DocumentOrderParams;
import uz.maroqand.ecology.core.dto.excel.DocumentOrderStatus;
import uz.maroqand.ecology.core.dto.excel.ExcelResponseDto;
import uz.maroqand.ecology.core.entity.DocumentOrder;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.report.ReportExcelOrder;
import uz.maroqand.ecology.core.repository.DocumentOrderRepository;
import uz.maroqand.ecology.core.service.sys.impl.FileServiceImpl;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.util.FileNameParser;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 03.07.2020.
 */
@Service
public class DocumentOrderService {

    private final FileServiceImpl filesService;
    private final DocumentOrderRepository documentOrderRepository;
    private final HelperService helperService;
    private Gson gson = new Gson();

    @Autowired
    public DocumentOrderService(FileServiceImpl filesService, DocumentOrderRepository documentOrderRepository, HelperService helperService) {
        this.filesService = filesService;
        this.documentOrderRepository = documentOrderRepository;
        this.helperService = helperService;
    }

    //yangi buyurtmani bazaga saqlaymiz
    public DocumentOrder saveOrder(DocumentOrderParams orderedDocumentData, Integer authorId, String locale) {
        DocumentOrder documentOrder = new DocumentOrder();
        documentOrder.setOrderedById(authorId);
        documentOrder.setRegisteredAt(new Date());
        documentOrder.setType(orderedDocumentData.getType());
        documentOrder.setStatus(DocumentOrderStatus.Pending);
        documentOrder.setParams(gson.toJson(orderedDocumentData));
        documentOrder.setLocale(locale);
        documentOrder.setDeleted(false);
        return documentOrderRepository.saveAndFlush(documentOrder);
    }

    public ExcelResponseDto getFrontendResponseForNotWorkingOrder(String locale) {
        ExcelResponseDto excelResponseDto = new ExcelResponseDto();
        excelResponseDto.setStatus(0);
        if (locale.equals("uz")) {
            excelResponseDto.setMessage("Diqqat! Hujjatga buyurtmangiz qabul qilinmadi. Xizmat vaqtincha ishlamayabdi.");
        } else if (locale.equals("oz")) {
            excelResponseDto.setMessage("Диққат! Ҳужжатга буюртмангиз қабул қилинмади. Хизмат вақтинча ишламаябди.");
        } else {
            excelResponseDto.setMessage("Внимание! Ваш заказ на документ не принят. Сервис временно недоступен.");
        }
        return excelResponseDto;
    }

    public boolean isDownloadable(DocumentOrder documentOrder) {
        return (documentOrder != null && documentOrder.getFileName() != null && !documentOrder.getFileName().isEmpty());
    }

    public ResponseEntity<Resource> documentOrderToDownloadableResource(DocumentOrder documentOrder) {
        File file = new File();
        file.setPath(documentOrder.getFileName());
        file.setSize(documentOrder.getSize().intValue());
        file.setExtension(FileNameParser.getExtensionFromFileName(documentOrder.getFileName()));

        String tag = "sys_documentOrders.type" + documentOrder.getType().name();
        String fileName;

//        if(documentOrder.getType().equals(DocumentOrderType.AppealReport)){
//            ReportExcelOrder excelOrder = gson.fromJson(documentOrder.getParams(), ReportExcelOrder.class);
//            tag += "_"+excelOrder.getReportType();
//            fileName = helperService.getTranslation(tag, "uz");
//            fileName +=  "_" + Common.dateFormat.format(excelOrder.getDateBegin()) + "_" + Common.dateFormat.format(excelOrder.getDateEnd());
//
//        } else if(documentOrder.getType().equals(DocumentOrderType.Appeal)){
//            AppealExcelOrder excelOrder = gson.fromJson(documentOrder.getParams(), AppealExcelOrder.class);
//            fileName = helperService.getTranslation("appeal_register.list", "uz");
//            fileName +=  "_" + (excelOrder.getBeginDate()!=null? Common.dateFormat.format(excelOrder.getBeginDate()):"");
//            fileName += "_" + (excelOrder.getEndDate()!=null? Common.dateFormat.format(excelOrder.getEndDate()):"");
//
//        } else if(documentOrder.getType().equals(DocumentOrderType.Task)){
//            TaskExcelOrder excelOrder = gson.fromJson(documentOrder.getParams(), TaskExcelOrder.class);
//            fileName = helperService.getTranslation("appeal_task.tasks", "uz");
//            fileName +=  "_" + excelOrder.getBeginDateStr() + "_" + excelOrder.getEndDateStr();
//
//        } else {
//            fileName = helperService.getTranslation(tag, "uz");
//        }

//        if(fileName.equals("")){
//            fileName = "docOrder_" + documentOrder.getId();
//        }

//        file.setName(fileName.replaceAll(" ","_") + "." + file.getExtension());
        return filesService.getFileAsResourceForDownloading(file);
    }

}