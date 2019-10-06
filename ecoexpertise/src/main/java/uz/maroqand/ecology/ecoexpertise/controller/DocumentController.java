package uz.maroqand.ecology.ecoexpertise.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
import uz.maroqand.ecology.core.util.Captcha;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Utkirbek Boltaev on 06.10.2019.
 * (uz)
 * (ru)
 */
@Controller
public class DocumentController {

    private DocumentRepoService documentRepoService;
    private ConclusionService conclusionService;
    private Logger logger = LogManager.getLogger(DocumentController.class);
    private final ConcurrentHashMap<String,String> conHashMap = new ConcurrentHashMap<>();

    @Autowired
    public DocumentController(DocumentRepoService documentRepoService, ConclusionService conclusionService) {
        this.documentRepoService = documentRepoService;
        this.conclusionService = conclusionService;
    }

    @RequestMapping(value = SysUrls.GetDocument+"/{uuid}", method = RequestMethod.GET)
    public String getDocumentPage(
            @PathVariable("uuid") String uuid,
            Model model
    ) {
        DocumentRepo documentRepo = documentRepoService.getDocumentByUuid(uuid);
        if(documentRepo != null){
            Conclusion conclusion = conclusionService.getById(documentRepo.getId());
            model.addAttribute("conclusion",conclusion);
            model.addAttribute("document",documentRepo);
        }
        model.addAttribute("uuid", uuid);
        return "document_search";
    }

    @RequestMapping(value = SysUrls.GetDocument+"/{uuid}", method = RequestMethod.POST)
    public String getDocument(
            @PathVariable("uuid") String uuid,
            @RequestParam("code") String code,
            @RequestParam("answer") String answer,
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession();
        logger.debug("GetDocument uuid=" + uuid+", code="+code+" ,session.getId()="+session.getId()+", answer="+answer);
        if(!conHashMap.containsKey(session.getId()) || !conHashMap.get(session.getId()).equals(answer)){
            return "redirect:" + SysUrls.GetDocument  + "/" + uuid + "&failed=1";
        }
        DocumentRepo documentRepo = documentRepoService.getDocumentByUuid(uuid);
        if(documentRepo == null){
            return "redirect:" + SysUrls.GetDocument  + "/" + uuid + "&failed=2";
        }

        Conclusion conclusion = conclusionService.getById(documentRepo.getApplicationId());
        model.addAttribute("conclusion",conclusion);
        model.addAttribute("documentRepo",documentRepo);
        return "document_view";
    }

    @RequestMapping(value = SysUrls.GetCaptcha, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getCapcha(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        String captchaText = Captcha.getSaltString();
        conHashMap.put(session.getId(), captchaText);
        logger.debug("GetCapcha  session=" + session.getId()+",captchaText="+captchaText);
        return  Captcha.generateImage(captchaText);
    }

    @RequestMapping(value = SysUrls.GetQRImage, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getQRImage(
            @RequestParam("id") Integer id
    ) {
        DocumentRepo documentRepo = documentRepoService.getDocument(id);
        if(documentRepo == null){
            return new byte[]{};
        }
        QRCodeWriter writer = new QRCodeWriter();
        int width = 116, height = 116;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
        int white = 255 << 16 | 255 << 8 | 255;
        int black = 0;

        String url = "http://172.24.0.218:8080" + SysUrls.GetDocument;

        try {
            BitMatrix bitMatrix = writer.encode(url+"/"+documentRepo.getUuid(), BarcodeFormat.QR_CODE, width, height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, bitMatrix.get(i, j) ? black : white); // set pixel one by one
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( image, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

}
