package uz.maroqand.ecology.core.dto.id_egov;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class IdGovService {
    private static final String AUTHORIZATION_URL = "https://sso.egov.uz/sso/oauth/Authorization.do";
    private final RestTemplate restTemplate;

    @Autowired
    public IdGovService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public IdGovToken getToken(CabinetType cabinetType, String code) {
        String getAccessTokenUrl = AUTHORIZATION_URL +
                "?grant_type=one_authorization_code" +
                "&client_id=" + cabinetType.getClient_id() +
                "&client_secret=" + cabinetType.getSecret() +
                "&code=" + code;
        System.out.println("getAccessTokenUrl=" + getAccessTokenUrl);
        ResponseEntity<IdGovToken> tokenResponse = restTemplate.exchange(getAccessTokenUrl, HttpMethod.POST, getEntity(), IdGovToken.class);
        if (!tokenResponse.getStatusCode().equals(HttpStatus.OK))
            throw new ApiException(ResponseStatus.OAUTH_INVALID_ID_EGOV_TOKEN);

        return tokenResponse.getBody();
    }

    public IdGovResponseDto getUserInfo(CabinetType cabinetType, String access_token) {
        String get_user_info_url = AUTHORIZATION_URL +
                "?grant_type=one_access_token_identify" +
                "&client_id=" + cabinetType.getClient_id() +
                "&client_secret=" + cabinetType.getSecret() +
                "&scope=" + cabinetType.getSecret() +
                "&access_token=" + access_token;
        Gson gson = new Gson();
        ResponseEntity<String> idGovResponse = restTemplate.exchange(get_user_info_url, HttpMethod.POST, getEntity(), String.class);
        log.info("Id egov, url:{}, response:{}", get_user_info_url, gson.toJson(idGovResponse));
        if (!idGovResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new ApiException(ResponseStatus.OAUTH_INVALID_ID_EGOV_TOKEN);
        }

//        return gson.fromJson("{\"legal_info\":[{\"le_tin\":\"208001604\",\"tin\":\"208001604\",\"le_name\":\"\\\"DEVHUB\\\" MChJ\",\"acron_UZ\":\"\\\"DEVHUB\\\" MChJ\",\"is_basic\":true}],\"birth_date\":\"1990-05-18\",\"ctzn\":\"Ð£ÐÐÐÐÐÐ¡Ð¢ÐÐ\",\"per_adr\":\"Ð¨ÐÐ¤ÐÐÐÐÐÒÐ ÐÐ¤Ð, ÐÐ£ÐÐÐÐ, 4 Ð¢ÐÐ  ÐÐÐ§ÐÐ¡Ð,  uy:12\",\"tin\":\"498296084\",\"pport_issue_place\":\"Ð£Ð§Ð¢ÐÐÐÐÐ¡ÐÐÐ Ð Ð£ÐÐ ÐÐÐ ÐÐÐ Ð¢ÐÐ¨ÐÐÐÐ¢Ð\",\"sur_name\":\"XAKIMJONOV\",\"gd\":\"1\",\"natn\":\"Ð£ÐÐÐÐ/Ð£ÐÐÐÐ§ÐÐ\",\"pport_issue_date\":\"2015-04-07\",\"_pport_issue_date\":\"2015-07-04\",\"pport_expr_date\":\"2025-04-06\",\"_pport_expr_date\":\"2025-06-04\",\"pport_no\":\"AA9174291\",\"pin\":\"31805900171580\",\"mob_phone_no\":\"+998909822237\",\"user_id\":\"nhakimjonov\",\"email\":\"nhakimjonov@yahoo.com\",\"birth_place\":\"TOSHKENT\",\"mid_name\":\"NABIJON OâGâLI\",\"valid\":\"true\",\"user_type\":\"L\",\"sess_id\":\"769dc830-97f9-4c6d-8798-7c987fdf05fb\",\"ret_cd\":\"0\",\"first_name\":\"NAIMJON\",\"full_name\":\"XAKIMJONOV NAIMJON NABIJON OâGâLI\"}", IdGovResponseDto.class);
        return gson.fromJson(idGovResponse.getBody(), IdGovResponseDto.class);

    }

    private HttpEntity<String> getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(headers);
    }

}
