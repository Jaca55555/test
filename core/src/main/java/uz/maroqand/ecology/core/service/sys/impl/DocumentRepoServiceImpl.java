package uz.maroqand.ecology.core.service.sys.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.repository.sys.DocumentRepoRepository;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
import uz.maroqand.ecology.core.util.Captcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz)
 * (ru)
 */

@Service
public class DocumentRepoServiceImpl implements DocumentRepoService {

    private final DocumentRepoRepository documentRepoRepository;

    @Autowired
    public DocumentRepoServiceImpl(DocumentRepoRepository documentRepoRepository) {
        this.documentRepoRepository = documentRepoRepository;
    }

    public DocumentRepo getDocument(Integer id) {
        if(id == null) return null;
        return documentRepoRepository.getOne(id);
    }

    public DocumentRepo getDocumentByUuid(String uuid){
        return documentRepoRepository.findByUuid(uuid);
    }

    public DocumentRepo create(DocumentRepoType type, Integer applicationId) {
        DocumentRepo documentRepo = new DocumentRepo();
        documentRepo.setType(type);
        documentRepo.setApplicationId(applicationId);

        final String uuid = UUID.randomUUID().toString();
        documentRepo.setUuid(uuid);
        documentRepo.setCode(Captcha.getVerificationCodeString());

        documentRepo.setCreatedAt(new Date());
        return documentRepoRepository.save(documentRepo);
    }

    @Override
    public byte[] getQRImage(Integer id) {
        DocumentRepo documentRepo = getDocument(id);
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
}