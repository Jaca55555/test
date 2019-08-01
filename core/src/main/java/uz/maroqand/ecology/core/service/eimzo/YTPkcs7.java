package uz.maroqand.ecology.core.service.eimzo;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.service.integration.eimzo.yt_pkcs7.Pkcs7;
import uz.maroqand.ecology.core.service.integration.eimzo.yt_pkcs7.Pkcs7Service;

/**
 * Created by a.ruzmetov on 30.03.2017.
 */
@Service
public class YTPkcs7 {

    private static YTPkcs7 client = null;
    private Pkcs7Service service;
    private Pkcs7 port;

    public YTPkcs7() {

        /*service = new Pkcs7Service();
        port = service.getPkcs7Port();*/

        // in not working
        initialize();
    }

    private void initialize() {
        try {
            service = new Pkcs7Service();
            port = service.getPkcs7Port();
        } catch (Exception e) {
            e.getStackTrace();
            service = null;
            port = null;
        }
    }

    public static synchronized YTPkcs7 getInstance(){
        if (client == null) {
            client = new YTPkcs7();
        }
        return client;
    }

    public String createPkcs7(byte[] document, String apiKey) {
        if (service == null || port == null) initialize();
        return port.createPkcs7(document, apiKey);
    }

    public String verifyPkcs7(String pkcs7B64) {
        if (service == null || port == null) initialize();
        return port.verifyPkcs7(pkcs7B64);
    }

}
