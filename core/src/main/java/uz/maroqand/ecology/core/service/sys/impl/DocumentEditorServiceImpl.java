package uz.maroqand.ecology.core.service.sys.impl;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;
import uz.maroqand.ecology.core.entity.sys.File;

import uz.maroqand.ecology.core.entity.sys.Option;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ProjectDeveloperService;
import uz.maroqand.ecology.core.service.sys.*;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.FileNameParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
@Service
public class DocumentEditorServiceImpl implements DocumentEditorService {

    private final HelperService helperService;
    private final FileService fileService;
    private final RegApplicationService regApplicationService;
    private final OptionService optionService;
    private final ProjectDeveloperService projectDeveloperService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ClientService clientService;
    private final ConclusionService conclusionService;
    private final DocumentRepoService documentRepoService;

    private Logger logger = LogManager.getLogger(DocumentEditorServiceImpl.class);


    public DocumentEditorServiceImpl(HelperService helperService, FileService fileService, RegApplicationService regApplicationService, OptionService optionService, ProjectDeveloperService projectDeveloperService, OrganizationService organizationService, UserService userService, ClientService clientService, ConclusionService conclusionService, DocumentRepoService documentRepoService) {
        this.helperService = helperService;
        this.fileService = fileService;
        this.regApplicationService = regApplicationService;
        this.optionService = optionService;
        this.projectDeveloperService = projectDeveloperService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.clientService = clientService;
        this.conclusionService = conclusionService;
        this.documentRepoService = documentRepoService;
    }

    public List<String[]> getDataForReplacingInMurojaatBlanki(RegApplication regApplication, String locale) {
        List<String[]> forReplace = new LinkedList<>();
        String  developerOpfName="null";
        if (regApplication.getDeveloperId()!=null){
            ProjectDeveloper projectDeveloper = projectDeveloperService.getById(regApplication.getDeveloperId());
            if (projectDeveloper!=null){
                if (locale.equals("ru")){
                    developerOpfName=projectDeveloper.getOpfId()!=null?helperService.getOpfShortName(projectDeveloper.getOpfId(),locale):"";
                    developerOpfName += " \"" + projectDeveloper.getName() + "\"";
                }else{
                    developerOpfName ="\"" + projectDeveloper.getName()+"\" ";
                    developerOpfName += projectDeveloper.getOpfId()!=null?helperService.getOpfShortName(projectDeveloper.getOpfId(),locale):"";
                }
            }
        }

        Client applicant = clientService.getById(regApplication.getApplicantId());
        String opfName="";
        if (locale.equals("ru")){
            opfName=applicant.getOpfId()!=null?helperService.getOpfShortName(applicant.getOpfId(),locale):"";
            opfName += "  \"" + applicant.getName() + "\"";
        }else{
            opfName ="\"" + applicant.getName() + "\" ";
            opfName += applicant.getOpfId()!=null?helperService.getOpfShortName(applicant.getOpfId(),locale):"";
        }

        Organization organization = organizationService.getById(userService.getCurrentUserFromContext().getOrganizationId());

        forReplace.add(new String[] {"sys_conclusion_title", helperService.getTranslation("sys_conclusion.title_" + (organization!=null?organization.getRegionId()+"":""),locale).replaceAll("<br>","\n")});
        forReplace.add(new String[] {"sys_conclusion_full_adress", helperService.getTranslation("sys_conclusion.full_adress_" + (organization!=null?organization.getRegionId()+"":""),locale).replaceAll("<br>","\n")});
        forReplace.add(new String[] {"sys_conclusion_description", helperService.getTranslation("sys_conclusion.description_" + (organization!=null?organization.getRegionId()+"":""),locale).replaceAll("<br>","\n")});
        forReplace.add(new String[] {"sys_client", helperService.getTranslation("sys_client",locale)});
        forReplace.add(new String[] {"sys_name_cilent", opfName});
        forReplace.add(new String[] {"sys_tin", helperService.getTranslation("sys_tin",locale)});
        forReplace.add(new String[] {"tin_applicant", applicant.getTin().toString()});
        forReplace.add(new String[] {"sys_category", helperService.getTranslation("sys_category",locale)});
        forReplace.add(new String[] {"sys_developer", helperService.getTranslation("sys_developer",locale)});
        forReplace.add(new String[] {"sys_name_developer", developerOpfName});
        forReplace.add(new String[] {"sys_expert", helperService.getTranslation("sys_expert",locale)});
        forReplace.add(new String[] {"sys_name_expert", helperService.getUserFullNameById(regApplication.getPerformerId())});
        forReplace.add(new String[] {"sys_organization", helperService.getTranslation("sys_organization",locale)});
        forReplace.add(new String[] {"sys_name_organization", opfName});
        forReplace.add(new String[] {"sys_director", helperService.getTranslation("sys_director",locale)});
        forReplace.add(new String[] {"sys_name_director", applicant.getDirectorFullName()==null || applicant.getDirectorFullName().isEmpty()?"-":applicant.getDirectorFullName()});
        forReplace.add(new String[] {"sys_performer.full","Исп.:" +  helperService.getUserFullNameById(regApplication.getPerformerId()) + "\n  tel.:"});

        return forReplace;
    }

    public boolean buildMurojaatBlanki(RegApplication regApplication, List<String[]> forReplacing, Integer userId) {
        Conclusion oldConclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        if (oldConclusion!=null && oldConclusion.getConclusionWordFileId()!=null){
            return true;
        }
        try {
            Option option = optionService.getOption("conclusion_blank_file");
            Integer optionVal;
            if(option!=null){
                optionVal = Integer.parseInt(option.getValue());
            }else {
                optionVal = 1;
            }
            File file = replaceStringsInWordDocument(
                    optionVal,
                    forReplacing,
                    "conclusion_blank_file"+regApplication.getId().toString()
            );
            fileService.save(file);
            Conclusion conclusion = conclusionService.create(regApplication.getId(),"",userId);
            conclusion.setConclusionWordFileId(file.getId());
            conclusionService.update(conclusion,"",userId);

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean conclusionComplete(Conclusion conclusion) {

        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        if (conclusion==null || conclusion.getConclusionWordFileId()==null || conclusion.getDocumentRepoId()==null){
            return false;
        }

        DocumentRepo documentRepo = documentRepoService.getDocument(conclusion.getDocumentRepoId());
        if (documentRepo==null){
            return false;
        }

        File input = fileService.findById(conclusion.getConclusionWordFileId());
        if (input==null){
            return false;
        }


            if (input!=null){
                String inputFilePath = input.getPath();
                String outputFileDirectory = fileService.getPathForUpload();
                String outputFilePath2 = outputFileDirectory + "/" + input.getName();

                try {

                    XWPFDocument doc = new XWPFDocument(new FileInputStream(inputFilePath));
                    XWPFParagraph newPara = doc.createParagraph();
                    XWPFRun newRun = newPara.createRun();
                    newRun.setText(helperService.getTranslation("sys_conclusion_number", locale) + " :" + conclusion.getNumber());
                    newRun.addBreak();
                    newRun.setText(helperService.getTranslation("sys_conclusion_date", locale) + (conclusion.getDate() != null ? Common.uzbekistanDateFormat.format(conclusion.getDate()) : ""));
                    newRun.addBreak();
                    newRun.setText(helperService.getTranslation("sys_documentRepo_info", locale));
                    newRun.addBreak();

                    QRCodeWriter writer = new QRCodeWriter();
                    int width = 116, height = 116;
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
                    int white = 255 << 16 | 255 << 8 | 255;
                    int black = 0;

                    String url = "https://cb.eco-service.uz/repository/get-document";

                    try {
                        BitMatrix bitMatrix = writer.encode(url + "/" + documentRepo.getUuid(), BarcodeFormat.QR_CODE, width, height);
                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                image.setRGB(i, j, bitMatrix.get(i, j) ? black : white); // set pixel one by one
                            }
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        baos.flush();
                        InputStream is = new ByteArrayInputStream(baos.toByteArray());
                        baos.close();
                        newRun.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "new", Units.toEMU(100), Units.toEMU(12 * 6 / 9 * 9));
                        is.close();
                        saveWord(input.getPath(), doc);
                        return true;
                    }catch (Exception e){

                    }
                }catch (Exception e){

                }
            }
        return false;
    }

    @Override
    public Boolean saveFileInputDownload(String fileName, String url) {
        System.out.println("saveFileInputDownload");
        try{
            File file = fileService.getByName(fileName);
            System.out.println("url==" + url);
            logger.info("url==" + url);
            logger.info("file.getPath()==" + file.getPath());
            System.out.println("file.getPath()==" + file.getPath());

            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(file.getPath());
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
            httpGet.addHeader("Referer", "https://www.google.com");

            try {
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity fileEntity = httpResponse.getEntity();
                System.out.println("fileSavePath==" + file.getPath());
                if (fileEntity != null) {
                    FileUtils.copyInputStreamToFile(fileEntity.getContent(), new java.io.File(file.getPath()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            httpGet.releaseConnection();

//            saveFile(new URL(url),file.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public File replaceStringsInWordDocument(Integer fileId, List<String[]> stringsToReplace, String fileName) {
        File input = fileService.findById(fileId);
        if (input == null) {
            logger.fatal("Could not find the file.");
            return null;
        }

        System.out.println("id="+input.getId());
        System.out.println("path="+input.getPath());
        System.out.println("ext="+input.getExtension());
        String inputFilePath = input.getPath();
        String outputFileDirectory = fileService.getPathForUpload();
        String extension = FileNameParser.getExtensionFromFileName(inputFilePath);
        String outputFilePath2 = outputFileDirectory + "/" + fileName + "." + extension;

        try {
            XWPFDocument doc = new XWPFDocument(new FileInputStream(inputFilePath));
            replaceTextNew(doc, stringsToReplace);
            saveWord(outputFilePath2, doc);

            File fileEntity = new File();
            fileEntity.setDateUploaded(new Date());
            fileEntity.setName(fileName + "." + extension);
            fileEntity.setPath(outputFilePath2);
            fileEntity.setExtension(extension);
            fileEntity.setSize(fileSize(outputFilePath2).intValue());
            fileService.save(fileEntity);

            return fileEntity;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private long replaceInParagraphs(
            List<XWPFParagraph> xwpfParagraphs,
            Map<String, String> replacements
    ) {
        long count = 0;

        for (XWPFParagraph paragraph : xwpfParagraphs) {
            List<XWPFRun> runs = paragraph.getRuns();

            for (Map.Entry<String, String> replPair : replacements.entrySet()) {
                String find = replPair.getKey();
                String repl = replPair.getValue();
                TextSegment found = paragraph.searchText(find, new PositionInParagraph());
                if ( found != null ) {
                    count++;
                    if ( found.getBeginRun() == found.getEndRun() ) {
                        // whole search string is in one Run
                        XWPFRun run = runs.get(found.getBeginRun());
                        String runText = run.getText(run.getTextPosition());
                        String replaced = runText.replace(find, repl);

                        //New lines--breaks
                        if (replaced.contains("\n")) {
                            String[] lines = replaced.split("\n");
                            run.setText(lines[0], 0); // set first line into XWPFRun
                            for (int i = 1; i < lines.length; i++) {
                                // add break and insert new text
                                run.addBreak();
                                run.setText(lines[i]);
                            }
                        } else {
                            run.setText(replaced, 0);
                        }
                    } else {
                        // The search string spans over more than one Run
                        // Put the Strings together
                        StringBuilder b = new StringBuilder();
                        for (int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++) {
                            XWPFRun run = runs.get(runPos);
                            b.append(run.getText(run.getTextPosition()));
                        }
                        String connectedRuns = b.toString();
                        String replaced = connectedRuns.replace(find, repl);

                        // The first Run receives the replaced String of all connected Runs
                        XWPFRun partOne = runs.get(found.getBeginRun());
                        //New lines--breaks
                        if (replaced.contains("\n")) {
                            String[] lines = replaced.split("\n");
                            partOne.setText(lines[0], 0); // set first line into XWPFRun
                            for (int i = 1; i < lines.length; i++) {
                                // add break and insert new text
                                partOne.addBreak();
                                partOne.setText(lines[i]);
                            }
                        } else {
                            partOne.setText(replaced, 0);
                        }

                        // Removing the text in the other Runs.
                        for (int runPos = found.getBeginRun()+1; runPos <= found.getEndRun(); runPos++) {
                            XWPFRun partNext = runs.get(runPos);
                            partNext.setText("", 0);
                        }
                    }
                }
            }

        }
        return count;
    }

    private Long fileSize(String path) {
        java.io.File oldFile = new java.io.File(path);
        return oldFile.length();
    }

    private XWPFDocument replaceTextNew(XWPFDocument doc, List<String[]> forReplacement) {
        Map<String,String> replacementMap = new HashMap<>();
        for(String[] row: forReplacement) {
            replacementMap.put(row[0], row[1]);
        }
        replaceInParagraphs(doc.getParagraphs(), replacementMap);
        replaceInTables(doc, replacementMap);
        return doc;
    }

    private void replaceInTables(XWPFDocument doc, Map<String,String> replacementMap) {
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceInParagraphs(cell.getParagraphs(), replacementMap);
                }
            }
        }
    }



    private void saveWord(String filePath, XWPFDocument doc) throws FileNotFoundException, IOException {
        FileOutputStream out = null;
        System.out.println("/saveWord.filePath="+filePath);
        try{
            out = new FileOutputStream(filePath);
            doc.write(out);
        }
        finally{
            out.close();
        }
    }

    public static boolean saveFile(URL fileURL, String fileSavePath) {

        boolean isSucceed = true;



        return isSucceed;
    }

}

