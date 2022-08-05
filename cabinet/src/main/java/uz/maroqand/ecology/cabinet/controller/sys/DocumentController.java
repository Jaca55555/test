package uz.maroqand.ecology.cabinet.controller.sys;

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
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.cabinet.constant.sys.SysTemplates;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
import uz.maroqand.ecology.core.util.Captcha;

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

    private final DocumentRepoService documentRepoService;
    private final ConclusionService conclusionService;
    private final RegApplicationService regApplicationService;
    private Logger logger = LogManager.getLogger(DocumentController.class);
    private final ConcurrentHashMap<String,String> conHashMap = new ConcurrentHashMap<>();

    @Autowired
    public DocumentController(DocumentRepoService documentRepoService, ConclusionService conclusionService, RegApplicationService regApplicationService) {
        this.documentRepoService = documentRepoService;
        this.conclusionService = conclusionService;
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping(value = SysUrls.GetDocument+"/{uuid}", method = RequestMethod.GET)
    public String getDocumentPage(
            @PathVariable("uuid") String uuid,
            Model model
    ) {
        DocumentRepo documentRepo = documentRepoService.getDocumentByUuid(uuid);
        if(documentRepo != null){
            Conclusion conclusion = conclusionService.getByRepoId(documentRepo.getId());
            if (conclusion==null || conclusion.getRegApplicationId()==null){
                return "redirect:/login";
            }
            RegApplication regApplication = regApplicationService.getById(conclusion.getRegApplicationId());
            if (regApplication==null){
                return "redirect:/login";
            }
            model.addAttribute("conclusion",conclusion);
            model.addAttribute("regApplication",regApplication);
            model.addAttribute("documentRepo",documentRepo);
        }
        model.addAttribute("uuid", uuid);
        return SysTemplates.ConclusionView;
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

        String url = "https://cb.eco-service.uz/repository/get-document";

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

    @RequestMapping(value = SysUrls.GetQRImageOffer, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getQRImage(
            @RequestParam("id") Integer id,
            @RequestParam("rid") Integer rid
    ) {
        System.out.println("regId="+rid);
        DocumentRepo documentRepo = documentRepoService.getDocument(id);
        if(documentRepo == null){
            return new byte[]{};
        }
        QRCodeWriter writer = new QRCodeWriter();
        int width = 116, height = 116;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
        int white = 255 << 16 | 255 << 8 | 255;
        int black = 0;

        String url = "http://eco-service.uz" + SysUrls.GetOffer;

        try {
            BitMatrix bitMatrix = writer.encode(url+"/"+documentRepo.getUuid()+"/"+rid, BarcodeFormat.QR_CODE, width, height);
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
