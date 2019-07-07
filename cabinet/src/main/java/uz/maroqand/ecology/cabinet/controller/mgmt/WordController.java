package uz.maroqand.ecology.cabinet.controller.mgmt;

import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.DivToSdt;
import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.Charset;


@Controller
public class WordController {

    @RequestMapping(value = MgmtUrls.WordEditor)
    public String getEditor(Model model){
        model.addAttribute("action_url",MgmtUrls.WordCreate);
        return MgmtTemplates.WordEditor;
    }

    @RequestMapping(value = MgmtUrls.WordCreate)
    public String cteateMethod(@RequestParam(name = "textVal") String text){
        System.out.println(text);

        Charset encoding = Charset.forName("UTF-8");
        byte[] encoded = text.getBytes();
        String html = new String(encoded,encoding);
        System.out.println("html==" + html);
        html = html.replaceAll("<br>","</br>");
        html = html.replaceAll("&nbsp;"," ");
        html = html.replaceAll("&lt;","<");
        html = html.replaceAll("&gt;",">");
        html = html.replaceAll("&amp;","&");
        html = html.replaceAll("&quot;","\"");
        html = html.replaceAll("&apos;","\'");
        System.out.println("html==" + html);

        // To docx, with content controls
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
        XHTMLImporter.setRunFormatting(FormattingOption.IGNORE_CLASS);
        XHTMLImporter.setDivHandler(new DivToSdt());

        try {
            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    XHTMLImporter.convert(html, null));
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

        System.out.println(XmlUtils.marshaltoString(wordMLPackage
                .getMainDocumentPart().getJaxbElement(), true, true));

        try {
            wordMLPackage.save(new File("e:\\final_document.docx"));
        } catch (Docx4JException e) {
            e.printStackTrace();
        }


        return "redirect:" + MgmtUrls.WordEditor;
    }

   /* public static void xhtmlToDocx(String destinationPath, String fileName)
    {
        File dir = new File (destinationPath);
        File actualFile = new File (dir, fileName);

        WordprocessingMLPackage wordMLPackage = null;
        try
        {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        }
        catch (InvalidFormatException e)
        {
            e.printStackTrace();
        }


        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

        OutputStream fos = null;
        try
        {
            fos = new ByteArrayOutputStream();

            System.out.println(XmlUtils.marshaltoString(wordMLPackage
                    .getMainDocumentPart().getJaxbElement(), true, true));

            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);
            Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML",
                    true);
            Docx4J.toHTML(htmlSettings, fos, Docx4J.FLAG_EXPORT_PREFER_XSL);
            wordMLPackage.save(new File(destinationPath));
        }
        catch (Docx4JException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
