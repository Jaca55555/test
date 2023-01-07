package uz.maroqand.ecology.ecoexpertise.constant.id_egov;

import lombok.Getter;

@Getter
public enum CabinetType {
    BACK_OFFICE("e-ngo_uz", "ngo_minjust_uz", "ZAdqXmvG42ut863TbKJBDdnt");

    //UzJqmfISjtaXD9rW2O7sT4gB

    private final String client_id;
    private final String scope;
    private final String secret;

    CabinetType(String client_id, String scope, String secret) {
        this.client_id = client_id;
        this.scope = scope;
        this.secret = secret;
    }

}