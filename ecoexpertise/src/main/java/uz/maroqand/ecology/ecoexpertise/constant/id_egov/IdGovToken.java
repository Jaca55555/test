package uz.maroqand.ecology.ecoexpertise.constant.id_egov;

import lombok.Data;

@Data
public class IdGovToken {
    private String scope;
    private Long expires_in;
    private String token_type;
    private String refresh_token;
    private String access_token;
}