package uz.maroqand.ecology.core.service.gnk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.dto.gnk.GnkResponseObject;
import uz.maroqand.ecology.core.dto.gnk.GnkRootResponseObject;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.sys.GNKSoatoService;
import uz.maroqand.ecology.core.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 17.07.2019.
 * (uz)
 * (ru)
 */
@Service
public class GnkService {

    private GNKSoatoService gnkSoatoService;
    private OpfService opfService;

    private static final String url_getIndividualInfo = "https://api.soliq.uz/iservices/wsgnkapi2?action=get_Recvisits&tin=";
    private final String username        = "u4mobile";
    private final String password    = "u4$mgnk";

    private Gson gson = new Gson();
    private Logger logger = LogManager.getLogger(GnkService.class);

    @Autowired
    public GnkService(GNKSoatoService gnkSoatoService, OpfService opfService) {
        this.gnkSoatoService = gnkSoatoService;
        this.opfService = opfService;
    }

    public GnkResponseObject getLegalEntityByTin( Integer tin ) {

        GnkResponseObject result = null;
        try {
            result = Authorized_Get(url_getIndividualInfo + tin.toString(), GnkResponseObject.class);
        }catch (Exception e){e.printStackTrace();}

        logger.info("GNK response result=", result);

        if(result!=null && result.getRoot()!=null){
            for (GnkRootResponseObject object: result.getRoot()){
                String subRegion;
                if(object.getRayon_code().length()==2){
                    subRegion = object.getObl_code()+""+object.getRayon_code();
                }else {
                    subRegion = object.getObl_code()+"0"+object.getRayon_code();
                }
                object.setSubRegioId(gnkSoatoService.getSoatoId(Parser.stringToInteger(subRegion)));
                if(!object.getAname().equals("-")){
                    String[] aname = object.getAname().split("\"");
                    object.setName(aname[1]);
                    object.setOpfId(opfService.getByNameRu(aname[0].substring(0, 1)+aname[0].substring(1, aname[0].length()).toLowerCase()));
                }
            }
        }
        return result;
    }

    /*
        C   O   M   M   O   N
    */
    public <T extends Object> T Authorized_Post(String serviceUrl, List<NameValuePair> urlParameters, Class<T> cls) throws IOException {
        String resultBytes = RestExchange(serviceUrl, urlParameters, HttpMethod.POST, AuthorizationHeader(username, password));
        System.out.println("resultBytes="+resultBytes);
        if (resultBytes != null) {
            return gson.fromJson(resultBytes, cls);
        }
        return null;
    }

    public <T extends Object> T Authorized_Get(String serviceUrl, Class<T> cls) throws IOException {
        String resultBytes = RestExchange(serviceUrl, null, HttpMethod.GET, AuthorizationHeader(username, password));
        if (resultBytes != null) {
            System.out.println("resultBytes1="+resultBytes);
            return gson.fromJson(resultBytes, cls);
        }
        return null;
    }

    public static String AuthorizationHeader(final String username, final String password) {
        if (username != null && !username.isEmpty() && password!= null && !password.isEmpty()) {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
            return "Basic " + new String(encodedAuth);
        }else{
            return null;
        }
    }

    private static String RestExchange(String url, List<NameValuePair> urlParameters, HttpMethod httpMethod, String authorizationHeader) throws IOException {
        if (httpMethod == HttpMethod.POST) {
            return Post(url, urlParameters, "UTF-8", authorizationHeader);
        } else {
            return Get(url, authorizationHeader);
        }
    }

    public static <T extends Object> T Convert(byte[] json, Class<T> cls) throws IOException {
        return GetMapper().readValue(json, cls);
    }

    public static ObjectMapper GetMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static String Post(
            String url,
            List<NameValuePair> urlParameters,
            String encoding,
            String authorizationHeader
    ) throws UnsupportedEncodingException, IOException {
        if (urlParameters == null) urlParameters = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(urlParameters, encoding));

        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            request.addHeader("Authorization", authorizationHeader);
        }

        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String result = null;

        if (entity != null) {
            try {
                InputStream instream = entity.getContent();
                BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(instream, "UTF-8"));

                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                result = buffer.toString();
                System.out.println("result:"+result);
                return result;
            } catch (RuntimeException ex) {
                request.abort();
                throw ex;
            } finally {
                httpClient.close();
            }
        }
        return null;
    }

    public static String Get(
            String url,
            String authorizationHeader
    ) throws UnsupportedEncodingException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            request.addHeader("Authorization", authorizationHeader);
        }

        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String result = null;

        if (entity != null) {
            try {
                InputStream instream = entity.getContent();
                BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(instream, "UTF-8"));

                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                result = buffer.toString();
                System.out.println("result:"+result);
                return result;
            } catch (RuntimeException ex) {
                request.abort();
                throw ex;
            } finally {
                httpClient.close();
            }
        }
        return null;
    }
}
