package uz.maroqand.ecology.core.service.eimzo;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.service.integration.eimzo.yt_cryptoauth.CryptoAuth;
import uz.maroqand.ecology.core.service.integration.eimzo.yt_cryptoauth.CryptoAuthService;

/**
 * Created by a.ruzmetov on 30.03.2017.
 */
@Service
public class YTCryptoAuth {

    private static YTCryptoAuth client = null;
    private CryptoAuthService service;
    private CryptoAuth port;

    public YTCryptoAuth() {
        try {
            service = new CryptoAuthService();
            port = service.getCryptoAuthPort();
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public static synchronized YTCryptoAuth getInstance(){
        if (client==null){
            client = new YTCryptoAuth();
        }
        return client;
    }

    public String getDigest(String dataB64){

        return port.getDigest(dataB64);
    }

    public String verifyDigest(String digestHex, String signatureHex, String signerCertB64){

        return port.verifyDigest(digestHex, signatureHex, signerCertB64);
    }

    public String verifySignature(String textB64, String signatureHex, String signerCertB64){

        return port.verifySignature(textB64, signatureHex, signerCertB64);
    }

}
