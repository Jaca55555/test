package uz.maroqand.ecology.core.integration.sms;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uz.maroqand.ecology.core.constant.sys.SmsSendStatus;
import uz.maroqand.ecology.core.dto.sms.AuthTokenInfo;
import uz.maroqand.ecology.core.dto.sms.ContactDto;
import uz.maroqand.ecology.core.dto.sms.ContactNumberDto;
import uz.maroqand.ecology.core.dto.sms.SmsSendDto;
import uz.maroqand.ecology.core.entity.sys.SmsSend;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by Utkirbek Boltaev on 10.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class SmsSendOauth2Service {

    private final String URL_SMS_BOX = "https://auth.smsbox.uz";
    private final String URL_OAUTH_TOKEN = URL_SMS_BOX + "/oauth/token";
    private final String URL_OAUTH_AUTHORIZATION = URL_SMS_BOX + "/oauth/authorize";
    private final String URL_SEND_TASKS = "https://api.smsbox.uz/api/tasks/send";
    private final String URL_CONTACT_CREATE = "https://api.smsbox.uz/api/phonebook/contacts";

    private final String OAUTH_TOKEN_CLIENT_ID        = "clientroom";
    private final String OAUTH_TOKEN_CLIENT_SECRET    = "S3cr3tpa55w0rd";
    private final String QPM_PASSWORD_GRANT           = "?grant_type=password&username=sattarov086@gmail.com&password=FTFpmBuI";

    private final String RESPONSE_OK = "OK";
    private final String RESPONSE_ERROR = "ERROR";
    private Logger logger = LogManager.getLogger(SmsSendOauth2Service.class);

    @Bean
    public static RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                return true;
            }
        };

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    /*
     * Prepare HTTP Headers.
     */
    public static HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */

    public HttpHeaders getHeadersWithClientCredentials(){
        //String plainClientCredentials=OAUTH_TOKEN_P_CLIENT_ID+":"+OAUTH_TOKEN_P_CLIENT_SECRET;
        String plainClientCredentials=OAUTH_TOKEN_CLIENT_ID+":"+OAUTH_TOKEN_CLIENT_SECRET;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        HttpHeaders headers = getHeaders();
        System.out.println("base64ClientCredentials="+base64ClientCredentials);
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    public static HttpHeaders getHeadersWithAccessToken(String accessToken){
        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }

    public void createContact(AuthTokenInfo authTokenInfo, SmsSend smsSend){
        System.out.println("createContact");
        System.out.println("Access_token="+authTokenInfo.getAccess_token());
        try {
            RestTemplate restTemplate = restTemplate();
            ContactDto contactDto = new ContactDto();
            ContactNumberDto contactNumberDto = new ContactNumberDto();
            contactNumberDto.setMsisdn(smsSend.getPhone());
            contactDto.setName(smsSend.getFullName());
            contactDto.setNumber(contactNumberDto);
            contactDto.setTag("XXX");
//            contactDto.setGender("M");
            contactDto.setOccupation("Eco");
//            contactDto.setAge(30);
//            contactDto.setNote("Tuman nomi");

            Gson gson = new Gson();
            System.out.println("gson="+gson.toJson(contactDto));
            HttpEntity<ContactDto> request = new HttpEntity<>(contactDto, getHeadersWithAccessToken(authTokenInfo.getAccess_token()));

            System.out.println("URL_CONTACT_CREATE="+URL_CONTACT_CREATE);
            ResponseEntity<Object> response = restTemplate.exchange(URL_CONTACT_CREATE, HttpMethod.POST, request, Object.class);
            System.out.println("createContact response="+response);
            logger.info("  create payer response : " + response);
            logger.info("  create payer response status : " + response.getStatusCode());
            logger.info("  create payer response body        : " + response.getBody());

        }catch ( HttpServerErrorException e){
            System.out.println("Exception="+e);
            System.out.println("Exception="+e.getMessage());
            System.out.println("Exception="+e.getResponseBodyAsString());
            e.getStackTrace();
        }catch (Exception e){
            System.out.println("Exception="+e);
            System.out.println("Exception="+e.getMessage());
            System.out.println("Exception="+e.getMessage());
            e.getStackTrace();
        }
    }

    /*
     * Send a POST request [on /oauth/token] to get an access-token, which will then be send with each request.
     */

    public SmsSend createSendTask(AuthTokenInfo authTokenInfo, SmsSend smsSend){
        logger.info("createSendTask ");
        try {
            RestTemplate restTemplate = restTemplate();
            SmsSendDto smsSendDto = new SmsSendDto();
            smsSendDto.setDestAddr(smsSend.getPhone());
            smsSendDto.setText(smsSend.getMessage());
            HttpEntity<SmsSendDto> request = new HttpEntity<>(smsSendDto, getHeadersWithAccessToken(authTokenInfo.getAccess_token()));
            System.out.println("URL_SEND_TASKS="+URL_SEND_TASKS);
            ResponseEntity<Object> response = restTemplate.exchange(URL_SEND_TASKS, HttpMethod.POST, request, Object.class);
            System.out.println("createSendTask response="+response);
            logger.info("response : " + response);

            smsSend.setSentAt(new Date());
            if (response.getStatusCode() == HttpStatus.OK){
                smsSend.setStatus(SmsSendStatus.ENROUTE);

                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
                logger.debug("  create payer info : " + map );
                if (map != null){
                    Integer sentId = (Integer) map.get("id");
                    smsSend.setSentId(sentId);
                    logger.debug("  create payer id : " + map.get("id"));
                }
            }else {
                smsSend.setStatus(SmsSendStatus.UNKNOWN);
            }

        }catch (Exception e){
            System.out.println("Exception="+e);
            e.getStackTrace();
        }
        return smsSend;
    }

    private static AuthTokenInfo authToken;
    private static Long tokenRefreshTime = 0L;

    public AuthTokenInfo getAccessTokenCheck() {
        Calendar calendar = Calendar.getInstance();
        if (authToken == null) {
            createToken();
        } else {
            Long difference = calendar.getTimeInMillis()/1000-tokenRefreshTime;
            logger.info("  token expires_in: {} , difference: {} ", authToken.getExpires_in(), difference);
            if(authToken.getExpires_in()<2 || authToken.getExpires_in()-difference<2){
                try {
                    Thread.sleep(1000L);
                    createToken();
                }catch (Exception e){
                    createToken();
                }
            }
        }

        if(authToken == null || authToken.getResponseCode().equals("ERROR")){
            createToken();
        }

        System.out.println("expires_in="+authToken.getExpires_in());
        return authToken;
    }

    private synchronized void createToken(){
        logger.info("       createToken     ");
        Calendar calendar = Calendar.getInstance();
        AuthTokenInfo authTokenInfo = sendTokenRequest();
        if(!authTokenInfo.getResponseCode().equals("OK")){
            authTokenInfo = sendTokenRequest();
        }
        if(authTokenInfo.getResponseCode().equals("OK")){
            authToken = authTokenInfo;
            tokenRefreshTime = calendar.getTimeInMillis()/1000;
        }
    }

    private AuthTokenInfo sendTokenRequest(){
        System.out.println("sendTokenRequest");
        AuthTokenInfo tokenInfo = new AuthTokenInfo();
        try {
            RestTemplate restTemplate = restTemplate();
            HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
            ResponseEntity<Object> response = restTemplate.exchange(URL_OAUTH_TOKEN+QPM_PASSWORD_GRANT, HttpMethod.POST, request, Object.class);
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
            if(map!=null){
                tokenInfo = new AuthTokenInfo();
                tokenInfo.setAccess_token((String)map.get("access_token"));
                tokenInfo.setToken_type((String) map.get("token_type"));
                tokenInfo.setRefresh_token((String) map.get("refresh_token"));
                tokenInfo.setExpires_in((int) map.get("expires_in"));
                tokenInfo.setScope((String) map.get("scope"));
                tokenInfo.setResponseCode(RESPONSE_OK);
            }else {
                tokenInfo.setResponseCode(RESPONSE_ERROR);
            }

        }catch (Exception e){
            System.out.println("sendTokenRequest Exception="+e);
            tokenInfo.setResponseCode(RESPONSE_ERROR);
        }
        return tokenInfo;
    }


}
