package uz.maroqand.ecology.core.service.eimzo;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.integration.eimzo.yt_tsaproxy.TsaProxy;
import uz.maroqand.ecology.core.integration.eimzo.yt_tsaproxy.TsaProxyService;

/**
 * Created by a.ruzmetov on 30.03.2017.
 */
@Service
public class YTProxy {

    private static YTProxy client = null;
    private TsaProxyService service;
    private TsaProxy port;

    public YTProxy() {
        try {
            service = new TsaProxyService();
            port = service.getTsaProxyPort();
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public static synchronized YTProxy getInstance(){
        if (client==null){
            client = new YTProxy();
        }
        return client;
    }

    public String getTimeStampTokenForSignature(String signatureHex){
        return port.getTimeStampTokenForSignature(signatureHex);
    }
}
