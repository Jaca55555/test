package uz.maroqand.ecology.core.service.sys.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Option;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.ProjectDeveloperService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.DocumentEditorService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OptionService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.FileNameParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public DocumentEditorServiceImpl(HelperService helperService, FileService fileService, RegApplicationService regApplicationService, OptionService optionService, ProjectDeveloperService projectDeveloperService, OrganizationService organizationService, UserService userService, ClientService clientService, ConclusionService conclusionService) {
        this.helperService = helperService;
        this.fileService = fileService;
        this.regApplicationService = regApplicationService;
        this.optionService = optionService;
        this.projectDeveloperService = projectDeveloperService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.clientService = clientService;
        this.conclusionService = conclusionService;
    }

    private Logger logger = LogManager.getLogger(DocumentEditorServiceImpl.class);

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


        forReplace.add(new String[] {"{sys_conclusion.title}", helperService.getTranslation("sys_conclusion.title_" + (organization!=null?organization.getRegionId()+"":""),locale)});
        forReplace.add(new String[] {"{sys_conclusion.full_adress}", helperService.getTranslation("sys_conclusion.full_adress_" + (organization!=null?organization.getRegionId().toString():""),locale)});
        forReplace.add(new String[] {"{sys_conclusion.description}", helperService.getTranslation("sys_conclusion.description_" + (organization!=null?organization.getRegionId().toString():""),locale)});
        forReplace.add(new String[] {"{sys_client}", helperService.getTranslation("sys_client",locale)});
        forReplace.add(new String[] {"{sys_client.name}", opfName});
        forReplace.add(new String[] {"{sys_tin}", helperService.getTranslation("sys_tin",locale)});
        forReplace.add(new String[] {"{applicant.tin}", applicant.getTin().toString()});
        forReplace.add(new String[] {"{sys_category}", helperService.getTranslation("sys_category",locale)});
        forReplace.add(new String[] {"{sys_developer}", helperService.getTranslation("sys_developer",locale)});
        forReplace.add(new String[] {"{sys_developer.name}", developerOpfName});
        forReplace.add(new String[] {"{sys_expert}", helperService.getTranslation("sys_expert",locale)});
        forReplace.add(new String[] {"{sys_expert.name}", helperService.getUserFullNameById(regApplication.getPerformerId())});
        forReplace.add(new String[] {"{sys_organization}", helperService.getTranslation("sys_organization",locale)});
        forReplace.add(new String[] {"{sys_organization.name}", opfName});
        forReplace.add(new String[] {"{sys_director}", helperService.getTranslation("sys_director",locale)});
        forReplace.add(new String[] {"{sys_director.name}", applicant.getDirectorFullName()});
        forReplace.add(new String[] {"{sys_performer.full}","Исп.:" +  helperService.getUserFullNameById(regApplication.getPerformerId()) + "\n  tel.:"});

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

            doc = replaceTextNew(doc, stringsToReplace);

            System.out.println("outputFilePath2="+outputFilePath2);
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
            System.out.println("e.getMessage()==" + e.getMessage());
//            e.printStackTrace();
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
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            for(String key: replacementMap.keySet()) {
                                String text = r.getText(0);

                                if (text != null && text.contains(key)) {
                                    text = text.replace(key, replacementMap.get(key));

                                    if (text.contains("\n")) {
                                        String[] lines = text.split("\n");
                                        r.setText(lines[0], 0); // set first line into XWPFRun
                                        for (int i = 1; i < lines.length; i++) {
                                            // add break and insert new text
                                            r.addBreak();
                                            r.setText(lines[i]);
                                        }
                                    } else {
                                        r.setText(text, 0);
                                    }
                                }
                            }
                        }
                    }
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

}
