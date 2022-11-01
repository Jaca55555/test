package uz.maroqand.ecology.core.service;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import uz.maroqand.ecology.core.constant.order.DocumentOrderType;
import uz.maroqand.ecology.core.constant.order.RegApplicationExcelOrder;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.DocumentOrder;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RegApplicationExcelService implements DocumentOrderPerformer{

    private Gson gson = new Gson();
    private final RegApplicationService regApplicationService;
    private final HelperService helperService;
    private final UserService userService;

    public RegApplicationExcelService(RegApplicationService regApplicationService, HelperService helperService, UserService userService) {
        this.regApplicationService = regApplicationService;
        this.helperService = helperService;
        this.userService = userService;
    }

    @Override
    public boolean performDocumentOrder(DocumentOrder order) throws Exception {
        if (order.getType()==null) return false;

        NumberFormat numberFormat  = NumberFormat.getInstance();
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (order.getType().equals(DocumentOrderType.RegApplication)){

            RegApplicationExcelOrder excelOrder = gson.fromJson(order.getParams(),RegApplicationExcelOrder.class);
            String locale = order.getLocale();
            if (locale == null) {
                locale = "uz";
            }
            XSSFSheet sheet = workbook.createSheet("RegApplication");
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
                    "registration_number",//1
                    "registration_date",//2
                    "doc.regDate",//3
                    "doc.organization",//4
                    "doc_content",//5
                    "sys_document.performer",//6
                    "sys_document.task_comment"
            ));
            for (int cellIndex = 0; cellIndex < traslateTag.size(); cellIndex++) {
                titleRow.createCell(cellIndex).setCellValue(helperService.getTranslation(traslateTag.get(cellIndex),locale));
                titleRow.getCell(cellIndex).setCellStyle(styleBorder);
            }
            FilterDto filterDto = new FilterDto();
            filterDto.setDateBegin(Common.uzbekistanDateFormat.format(order.getStartedAt()));
            filterDto.setDateEnd(Common.uzbekistanDateFormat.format(order.getFinishedAt()));
            int page = 0;
            User user = userService.getCurrentUserFromContext();
            Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                    filterDto,
                    userService.isAdmin()||user.getRole().getId()==16?null:user.getOrganizationId(),
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 100));

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
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(3);
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(4);
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(5);
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(6);
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
                cell.setCellStyle(style);

                cell = documentRow.createCell(7);
                cell.setCellValue(helperService.getObjectExpertise(regApplication.getObjectId(),locale));
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
