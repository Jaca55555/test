package uz.maroqand.ecology.core.service.eimzo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.eimzo.YTErrorType;
import uz.maroqand.ecology.core.constant.eimzo.YTWSResponseFields;
import uz.maroqand.ecology.core.dto.eimzo.VerifyPkcs7Dto;
import uz.maroqand.ecology.core.util.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by a.ruzmetov on 31.03.2017.
 */
@Service
public class EimzoService {

    @Autowired
    YTPkcs7 ytPkcs7;

    Gson gson = new Gson();
    private Logger logger = LogManager.getLogger(EimzoService.class);

    DateFormat ecpDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public VerifyPkcs7Dto checkVerifyPkcs7(String pkcs7B64) {
        logger.info(" checkVerifyPkcs7 : " + pkcs7B64);
        VerifyPkcs7Dto verifyPkcs7Dto = new VerifyPkcs7Dto();
        try {
            String checkParams = ytPkcs7.verifyPkcs7(pkcs7B64);
            //String checkParams = ytPkcs7.getInstance().verifyPkcs7(pkcs7B64);

            logger.info("RESPONSE : " + checkParams);

            JsonObject jsonObject = gson.fromJson(checkParams, JsonObject.class);

            System.out.println(" object  : " + jsonObject);

            if (jsonObject.has(YTWSResponseFields.SUCCESS)) {
                boolean success = jsonObject.get(YTWSResponseFields.SUCCESS).getAsBoolean();
                logger.info(" param success : " + success);
                verifyPkcs7Dto.setReturnSuccess(success);
            }

            if (jsonObject.has(YTWSResponseFields.PKCS7_INFO)) {
                JsonObject jsonObjectPkcs7Info = jsonObject.getAsJsonObject(YTWSResponseFields.PKCS7_INFO);
                logger.info(" json object pkcs7 info : " + jsonObjectPkcs7Info);

                // documentBase64 => not using

                if (jsonObjectPkcs7Info.has(YTWSResponseFields.SIGNERS)) {
                    JsonArray jsonArraySigner = jsonObjectPkcs7Info.getAsJsonArray(YTWSResponseFields.SIGNERS);
                    logger.info(" json array signer : " + jsonArraySigner);
                    logger.debug(" signer size : " + jsonArraySigner.size());
                    if (jsonArraySigner.size() > 0) {
                        JsonObject jsonObjectSigner = jsonArraySigner.get(0).getAsJsonObject();
                        logger.info(" object signer : " + jsonObjectSigner);

                        if (jsonObjectSigner.has(YTWSResponseFields.VERIFIED)) {
                            boolean verified = jsonObjectSigner.get(YTWSResponseFields.VERIFIED).getAsBoolean();
                            verifyPkcs7Dto.setVerified(verified);
                        }
                        if (jsonObjectSigner.has(YTWSResponseFields.CERTIFICATE_VERIFIED)) {
                            boolean certVer = jsonObjectSigner.get(YTWSResponseFields.CERTIFICATE_VERIFIED).getAsBoolean();
                            verifyPkcs7Dto.setCertificateVerified(certVer);
                        }
                        if(jsonObjectSigner.has(YTWSResponseFields.SIGNING_TIME)) {
                            String signingTime = jsonObjectSigner.get(YTWSResponseFields.SIGNING_TIME).getAsString();

                            try {
                                verifyPkcs7Dto.setSigningTime(ecpDateFormat.parse(signingTime));
                            } catch (Exception e) {
                            }
                        }

                        if (jsonObjectSigner.has(YTWSResponseFields.CERTIFICATE)) {
                            JsonArray jsonArrayCertificate = jsonObjectSigner.getAsJsonArray(YTWSResponseFields.CERTIFICATE);
                            logger.info(" certificate : " + jsonArrayCertificate);
                            logger.info(" certificate size : " + jsonArrayCertificate.size());
                            if (jsonArrayCertificate.size() > 0) {
                                JsonObject objectCertificate = jsonArrayCertificate.get(0).getAsJsonObject();

                                if (objectCertificate.has(YTWSResponseFields.SERIAL_NUMBER)) {
                                    String serialNumber = objectCertificate.get(YTWSResponseFields.SERIAL_NUMBER).getAsString();
                                    verifyPkcs7Dto.setCertificateSerialNumber(serialNumber);
                                }

                                if (objectCertificate.has(YTWSResponseFields.SUBJECT_NAME)) {
                                    String name = objectCertificate.get(YTWSResponseFields.SUBJECT_NAME).getAsString();
                                    verifyPkcs7Dto.setCertificateSubjectName(name);
                                }

                                if (objectCertificate.has(YTWSResponseFields.VALID_FROM)) {
                                    String from = objectCertificate.get(YTWSResponseFields.VALID_FROM).getAsString();
                                    try {
                                        verifyPkcs7Dto.setCertificateValidFrom(ecpDateFormat.parse(from));
                                    } catch (Exception e) {
                                    }
                                }

                                if (objectCertificate.has(YTWSResponseFields.VALID_TO)) {
                                    String to = objectCertificate.get(YTWSResponseFields.VALID_TO).getAsString();
                                    try {
                                        verifyPkcs7Dto.setCertificateValidTo(ecpDateFormat.parse(to));
                                    } catch (Exception e) {
                                    }
                                }

                                if (objectCertificate.has(YTWSResponseFields.PUBLIC_KEY)) {
                                    JsonObject objectPublicKey = objectCertificate.getAsJsonObject(YTWSResponseFields.PUBLIC_KEY);
                                    if (objectPublicKey.has(YTWSResponseFields.PUBLIC_KEY)) {
                                        String publicKey = objectPublicKey.get(YTWSResponseFields.PUBLIC_KEY).getAsString();
                                        verifyPkcs7Dto.setCertificatePublicKey(publicKey);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (jsonObject.has(YTWSResponseFields.REASON)) {
                String reason = jsonObject.get(YTWSResponseFields.REASON).getAsString();
                verifyPkcs7Dto.setErrorReason(reason);
            }


            if (verifyPkcs7Dto.getReturnSuccess()) {

                if (!verifyPkcs7Dto.getVerified() || !verifyPkcs7Dto.getCertificateVerified()) {
                    verifyPkcs7Dto.setErrorType(YTErrorType.CertificateInActive);
                } else {
                    // success
                    verifyPkcs7Dto.setResponseCode(Common.RESPONSE_OK);
                }

            } else {
                verifyPkcs7Dto.setErrorType(YTErrorType.NotFound);
            }

        } catch (Exception e) {
            logger.error("checkVerifyPkcs7 : ", e);
        }
        return verifyPkcs7Dto;
    }


}
