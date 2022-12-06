package uz.maroqand.ecology.core.service;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.constant.order.RegApplicationExcelOrder;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.DocumentOrder;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.billing.PaymentFile;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentFileService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.*;

@Service
public class RegApplicationExcelService implements DocumentOrderPerformer{

    private Gson gson = new Gson();
    private final RegApplicationService regApplicationService;
    private final ProjectDeveloperService projectDeveloperService;
    private final HelperService helperService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final PaymentFileService paymentFileService;
    private final ObjectExpertiseService objectExpertiseService;
    private final OrganizationService organizationService;
    private final ConclusionService conclusionService;
    private final SoatoService soatoService;
    private final MaterialService materialService;
    private final PaymentService paymentService;

    public RegApplicationExcelService(RegApplicationService regApplicationService, ProjectDeveloperService projectDeveloperService, HelperService helperService, UserService userService, InvoiceService invoiceService, ClientService clientService, PaymentFileService paymentFileService, ObjectExpertiseService objectExpertiseService, OrganizationService organizationService, ConclusionService conclusionService, SoatoService soatoService, MaterialService materialService, PaymentService paymentService) {
        this.regApplicationService = regApplicationService;
        this.projectDeveloperService = projectDeveloperService;
        this.helperService = helperService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.paymentFileService = paymentFileService;
        this.objectExpertiseService = objectExpertiseService;
        this.organizationService = organizationService;
        this.conclusionService = conclusionService;
        this.soatoService = soatoService;
        this.materialService = materialService;
        this.paymentService = paymentService;
    }

    @Override
    public boolean performDocumentOrder(DocumentOrder order) throws Exception {
        if (order.getType()==null) return false;
        System.out.println("order"+order);
        NumberFormat numberFormat  = NumberFormat.getInstance();
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (order.getType().equals(DocumentOrderType.RegApplication)){

            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet(helperService.getTranslation("sys_reg_applications",locale));
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 8 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 40 * 256);
            sheet.setColumnWidth(5, 15 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "reg_application.id",//1
                    "coordinate.object_expertise",//2
                    "sys_category",//3
                    "organization.id",//4
                    "sys_client_tin",//5
                    "reg_application_log.createdAt",//6
                    "sys_documentOrders.status"
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            filterDto.setCategory(excelOrder.getCategory());
            int page = 0;
            User user = order.getOrderedBy();
            Page<RegApplication> regApplicationPage = regApplicationService.findFilteredExcel(
                    filterDto,
                    excelOrder.getReviewId()!=null? excelOrder.getReviewId() : user.getOrganizationId(),
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 10000,Sort.by("registrationDate").descending()));

            for(RegApplication regApplication:regApplicationPage){
                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(regApplication.getId());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(regApplication.getObject().getNameTranslation(locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(regApplication.getCategory()!=null ? helperService.getCategory(regApplication.getCategory().getId(),locale):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(regApplication.getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(regApplication.getApplicant().getTin());
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(regApplication.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(helperService.getTranslation(regApplication.getStatus().getName(),locale));
                cell.setCellStyle(style);





            }
        }
        if (order.getType().equals(DocumentOrderType.General)){

            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet(helperService.getTranslation("sys_reg_applications",locale));
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 8 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 40 * 256);
            sheet.setColumnWidth(5, 15 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 25 * 256);
            sheet.setColumnWidth(8, 25 * 256);
            sheet.setColumnWidth(9, 25 * 256);
            sheet.setColumnWidth(10, 25 * 256);
            sheet.setColumnWidth(11, 25 * 256);
            sheet.setColumnWidth(12, 25 * 256);
            sheet.setColumnWidth(13, 25 * 256);
            sheet.setColumnWidth(14, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "reg_application.id",//1
                    "coordinate.object_expertise",//2
                    "sys_category",//3
                    "organization.id",//4
                    "sys_client_tin",//5
                    "reg_application_log.createdAt",//6
                    "sys_documentOrders.status",//7
                    "ID",//8
                    "reg_application.id",//9
                    "coordinate.object_expertise",//10
                    "sys_category",//11
                    "organization.id",//12
                    "sys_client_tin",//13
                    "reg_application_log.createdAt"
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            filterDto.setCategory(excelOrder.getCategory());
            int page = 0;
            User user = order.getOrderedBy();
            Page<RegApplication> regApplicationPage = regApplicationService.findFilteredExcel(
                    filterDto,
                    excelOrder.getReviewId()!=null? excelOrder.getReviewId() : user.getOrganizationId(),
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 10000,Sort.by("registrationDate").descending()));

            for(RegApplication regApplication:regApplicationPage){
                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(regApplication.getId());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(regApplication.getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(helperService.getMaterialShortNames(regApplication.getMaterials(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(regApplication.getId());
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(projectDeveloperService.getById(regApplication.getDeveloperId()).getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(regApplication.getApplicant().getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(regApplication.getCategory()!=null ? helperService.getCategory(regApplication.getCategory().getId(),locale):"");
                cell.setCellStyle(style);


                cell = documentRow.createCell(8);
                cell.setCellValue(invoiceService.getInvoice(regApplication.getInvoiceId()).getClosedDate()!=null ? Common.uzbekistanDateFormat.format(invoiceService.getInvoice(regApplication.getInvoiceId()).getClosedDate()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(9);
                cell.setCellValue(invoiceService.getInvoice(regApplication.getInvoiceId()).getClosedDate()!=null ? invoiceService.getInvoice(regApplication.getInvoiceId()).getAmount().toString():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(10);
                cell.setCellValue(conclusionService.getByRegApplicationIdLast(regApplication.getId()).getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(conclusionService.getByRegApplicationIdLast(regApplication.getId()).getCreatedAt()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(11);
                cell.setCellValue(conclusionService.getByRegApplicationIdLast(regApplication.getId()).getCreatedAt()!=null ? conclusionService.getByRegApplicationIdLast(regApplication.getId()).getNumber():"");
                cell.setCellStyle(style);


                cell = documentRow.createCell(12);
                cell.setCellValue("");
                cell.setCellStyle(style);

                cell = documentRow.createCell(13);
                cell.setCellValue(conclusionService.getByRegApplicationIdLast(regApplication.getId()).getCreatedAt()!=null ? conclusionService.getByRegApplicationIdLast(regApplication.getId()).getNumber():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(14);
                cell.setCellValue(regApplication.getReview().getName());
                cell.setCellStyle(style);






            }
        }
        else if (order.getType().equals(DocumentOrderType.Performer)){
            System.out.println("Performer uchun yaratildi");
            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet(helperService.getTranslation("reg_performer_list",locale));
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 16 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 24 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 25 * 256);
            sheet.setColumnWidth(9, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "sys_organization",//1
                    "doc_reference.performers",//2
                    "reg_application.position",//3
                    "reg_application.id",//4
                    "organization.id",//5
                    "sys_category",//6
                    "sys_conclusion.status",//7
                    "registration_date",//8
                    "sys_createdAt"//9
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            filterDto.setCategory(excelOrder.getCategory());
            Set<RegApplicationStatus> regApplicationStatusSet = new HashSet<>();
            regApplicationStatusSet.add(RegApplicationStatus.Process);
            filterDto.setStatusForReg(regApplicationStatusSet);
            int page = 0;
            User user = order.getOrderedBy();
            Page<RegApplication> regApplicationPage = regApplicationService.findFilteredExcel(
                    filterDto,
                    excelOrder.getReviewId()!=null? excelOrder.getReviewId() : user.getOrganizationId(),
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 10000,Sort.by("registrationDate").descending()));

            for(RegApplication regApplication:regApplicationPage){
                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(regApplication.getReview().getName());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(regApplication.getPerformer()!=null ? regApplication.getPerformer().getFName():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(regApplication.getPerformer()!=null ? regApplication.getPerformer().getPosition().getName():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(regApplication.getId());
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(regApplication.getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(regApplication.getCategory()!=null ? helperService.getCategory(regApplication.getCategory().getId(),locale):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(regApplication.getConclusionId()!=null ? helperService.getTranslation(conclusionService.getById(regApplication.getConclusionId()).getStatus().getName(),locale) :"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(8);
                cell.setCellValue(regApplication.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(9);
                cell.setCellValue(regApplication.getConclusionId()!=null ?  Common.uzbekistanDateFormat.format(conclusionService.getById(regApplication.getConclusionId()).getCreatedAt()):"");
                cell.setCellStyle(style);




            }
        }
        else if (order.getType().equals(DocumentOrderType.Deadline)){

            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet(helperService.getTranslation("reg_deadline_list",locale));
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 16 * 256);
            sheet.setColumnWidth(2, 20 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 24 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 25 * 256);
            sheet.setColumnWidth(9, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "sys_organization",//1
                    "doc_reference.performers",//2
                    "reg_application.position",//3
                    "reg_application.id",//4
                    "organization.id",//5
                    "sys_category",//6
                    "sys_conclusion.status",//7
                    "registration_date",//8
                    "sys_createdAt"//9
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            filterDto.setDeadlineDate(Common.uzbekistanDateFormat.format(new Date()));
            filterDto.setCategory(excelOrder.getCategory());
            Set<RegApplicationStatus> regApplicationStatusSet = new HashSet<>();
            regApplicationStatusSet.add(RegApplicationStatus.Process);
            filterDto.setStatusForReg(regApplicationStatusSet);
            int page = 0;
            User user = order.getOrderedBy();
            Page<RegApplication> regApplicationPage = regApplicationService.findFilteredExcel(
                    filterDto,
                    excelOrder.getReviewId()!=null? excelOrder.getReviewId() : user.getOrganizationId(),
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 10000, Sort.by("registrationDate").descending()));

            for(RegApplication regApplication:regApplicationPage){
                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(regApplication.getReview().getName());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(regApplication.getPerformer()!=null ? regApplication.getPerformer().getFName():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(regApplication.getPerformer()!=null ? regApplication.getPerformer().getPosition().getName():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(regApplication.getId());
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(regApplication.getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(regApplication.getCategory()!=null ? helperService.getCategory(regApplication.getCategory().getId(),locale):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(regApplication.getConclusionId()!=null ? helperService.getTranslation(conclusionService.getById(regApplication.getConclusionId()).getStatus().getName(),locale) :"");
                cell.setCellStyle(style);


                cell = documentRow.createCell(8);
                cell.setCellValue(regApplication.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(9);
                cell.setCellValue(regApplication.getConclusionId()!=null ?  Common.uzbekistanDateFormat.format(conclusionService.getById(regApplication.getConclusionId()).getCreatedAt()):"");
                cell.setCellStyle(style);




            }
        }
        else if (order.getType().equals(DocumentOrderType.Invoice)) {
            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet(helperService.getTranslation("invoice_list",locale));
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 8 * 256);
            sheet.setColumnWidth(2, 18 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 20 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "invoice.id",//1
                    "invoice",//2
                    "invoice_payer",//3
                    "sys_sum",//4
                    "sys_invoice_createdAt",//5
                    "sys_status",//6
                    "invoice_payer"
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            int page = 0;
            User user = order.getOrderedBy();
            Page<Invoice> invoicePage = invoiceService.findFiltered(
                    null,
                    excelOrder.getBeginDate(),
                    excelOrder.getEndDate(),
                    false,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    excelOrder.getReviewId()!=null? excelOrder.getReviewId() : user.getOrganizationId(),
                    null,
                    PageRequest.of(page, 10000));
            for(Invoice invoice:invoicePage){

                invoiceService.checkInvoiceStatus(invoice);
                Client client = null;
                if (invoice.getClientId()!=null){
                    client = clientService.getById(invoice.getClientId());
                }
                String clientName = "";
                String clientTin = "";
                if (client!=null){
                    clientName = client.getName();
                    clientTin = client.getTin()!=null?client.getTin().toString():"";
                }


                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(invoice.getId());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(invoice.getInvoice());
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(invoice.getPayee().getName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(String.format("% ,.1f", invoice.getAmount()));
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(Common.uzbekistanDateAndTimeFormat.format(invoice.getCreatedDate()));
                cell.setCellStyle(style);
                String status = invoice.getStatus().name();
                cell = documentRow.createCell(6);
                cell.setCellValue(helperService.getTranslation("invoice_status."+status.toLowerCase(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(clientName + "  " + clientTin);
                cell.setCellStyle(style);



        }
            }
        else if (order.getType().equals(DocumentOrderType.Payment)){
            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet("Payment");
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);

            sheet.setColumnWidth(0, 6 * 256);
            sheet.setColumnWidth(1, 8 * 256);
            sheet.setColumnWidth(2, 10 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 20 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 15 * 256);
            sheet.setColumnWidth(7, 25 * 256);
            sheet.setColumnWidth(8, 25 * 256);
            sheet.setColumnWidth(9, 25 * 256);
            sheet.setColumnWidth(10, 25 * 256);


            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "ID",//0
                    "payment.id",//1
                    "invoice",//2
                    "payer_name",//3
                    "payment.date",//4
                    "sys_sum",//5
                    "sys_payment_detail",//6
                    "sys_receiver_acc",//7
                    "sys_receiver_acc",//8
                    "sys_receiver_name",//9
                    "sys_receiver_mfo"

            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(excelOrder.getBeginDate()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(excelOrder.getEndDate()));
            int page = 0;
            User user = order.getOrderedBy();
            String account = null;
            String oldAccount = null;

            Page<PaymentFile> paymentFilePage = paymentFileService.findFiltered(
                    excelOrder.getBeginDate(),
                    excelOrder.getEndDate(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    excelOrder.getReviewId()!=null? organizationService.getById(excelOrder.getReviewId()).getAccount(): organizationService.getById(user.getOrganizationId()).getAccount(),
                    excelOrder.getReviewId()!=null? organizationService.getById(excelOrder.getReviewId()).getOldAccount(): organizationService.getById(user.getOrganizationId()).getOldAccount(),

                    PageRequest.of(page, 10000));

            for(PaymentFile paymentFile:paymentFilePage){

                StringBuilder accountString  = null;
                boolean invoiceIsNull = Boolean.TRUE;
                if (paymentFile.getInvoice()!=null) invoiceIsNull = Boolean.FALSE;
                if (paymentFile.getReceiverAccount()!=null){
                    char[] accountReceiver = paymentFile.getReceiverAccount().toCharArray();
                    accountString = new StringBuilder();
                    for (int k=0; k<accountReceiver.length;k++){
                        if (k!=0 && (k % 4) ==0 ){
                            accountString.append(" ");
                        }
                        accountString.append(String.valueOf(accountReceiver[k]));
                    }
                }
                if (paymentFile.getInvoice()==null){
                    String invoiceStr = paymentFile.getDetails();
                    String[] parts = invoiceStr.split(" ");
                    for (String invoiceCheck : parts) {
                        if(invoiceCheck.length()==14){
                            invoiceService.getInvoice(invoiceCheck);
                            if(invoiceService.getInvoice(invoiceCheck)!=null ){
                                invoiceIsNull = Boolean.FALSE;
                                break;
                            }
                        }
                    }
                }


                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(j++);
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(paymentFile.getId());
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(paymentFile.getPayment()!=null ? paymentFile.getPayment().getInvoice().getInvoice():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(paymentFile.getPayerName());
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(paymentFile.getPayment()!=null? Common.uzbekistanDateAndTimeFormat.format(paymentFile.getPayment().getPaymentDate()):"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(String.format("% ,.1f", paymentFile.getAmount()));
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(paymentFile.getDetails());
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(paymentFile.getPayment()!=null ? paymentFile.getPayment().getBankAccount():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(8);
                cell.setCellValue(accountString!=null?accountString.toString():"");
                cell.setCellStyle(style);

                cell = documentRow.createCell(9);
                cell.setCellValue(paymentFile.getReceiverName());
                cell.setCellStyle(style);


                cell = documentRow.createCell(10);
                cell.setCellValue(paymentFile.getReceiverMfo());
                cell.setCellStyle(style);



            }
        }
        else {
            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet("Arizalar ro'yhati");
            Row row = sheet.createRow(3);
            Cell cell = row.createCell(4);
            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 8 * 256);
            sheet.setColumnWidth(2, 8 * 256);
            sheet.setColumnWidth(3, 8 * 256);
            sheet.setColumnWidth(4, 8 * 256);
            sheet.setColumnWidth(5, 8 * 256);



            CellStyle style = workbook.createCellStyle();//Create style

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setWrapText(true);

            //border MEDIUM
            CellStyle styleBorder = workbook.createCellStyle();//Create style
            Font font = workbook.createFont();//Create font
            font.setBold(true);//Make font bold
            styleBorder.setFont(font);
            styleBorder.setAlignment(HorizontalAlignment.CENTER);
            styleBorder.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBorder.setBorderTop(BorderStyle.MEDIUM);
            styleBorder.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            styleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleBorder.setBorderBottom(BorderStyle.MEDIUM);
            styleBorder.setBorderLeft(BorderStyle.MEDIUM);
            styleBorder.setBorderRight(BorderStyle.MEDIUM);
            styleBorder.setWrapText(true);

            int i = 0;
            int j=1;
            Row titleRow = sheet.createRow(i++);
//        titleRow.setRowStyle(style);


            List<String> traslateTag = new LinkedList<>(Arrays.asList(
                    "sys_region",//0
                    "front.all_statistic_title",//1
                    "task_status.inProgress",//2
                    "sys_performerStatus.approved",//3
                    "sys_performerStatus.denied",//4
                    "sys_confirmStatus.modification"//5

            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            int page = 0;
            Page<Soato> soatoPage = soatoService.getFiltered(null,null,null,  PageRequest.of(page, 10000));
            Set<Integer>organizationIds=new HashSet<>();
            for (int k=1;k<=19;k++){
                if(k==2){
                    continue;
                }

                organizationIds.add(i);
            }
            for(Soato soato:soatoPage){



                Row documentRow = sheet.createRow(i++);
                //id
                cell = documentRow.createCell(0);
                cell.setCellValue(soato.getNameTranslation(order.getLocale()));
                cell.setCellStyle(style);
                //registrationNumber
                cell = documentRow.createCell(1);
                cell.setCellValue(soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(excelOrder.getCategory(), excelOrder.getBeginDate(),excelOrder.getEndDate(),null,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), null,soato.getId(),organizationIds));
                cell.setCellStyle(style);

                //ekspertiza obyekti
                cell = documentRow.createCell(2);
                cell.setCellValue(soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.Process,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.Process,soato.getId(),organizationIds));
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.Approved,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.Approved,soato.getId(),organizationIds));
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(),  RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.NotConfirmed,soato.getId(),organizationIds));
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(soato.getParentId()==null? regApplicationService.countByCategoryAndStatusAndRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(), RegApplicationStatus.Modification,soato.getId(),organizationIds):regApplicationService.countByCategoryAndStatusAndSubRegionId(excelOrder.getCategory(),excelOrder.getBeginDate(),excelOrder.getEndDate(),RegApplicationStatus.Modification,soato.getId(),organizationIds));
                cell.setCellStyle(style);

            }

        }




        String file = order.getFileName() + ".xls";

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();

        order.setFileName(file);
        return true;
        }

    }
