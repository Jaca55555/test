package uz.maroqand.ecology.core.service.integration.common;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Utkirbek Boltaev on 21.08.2019.
 * (uz)
 * (ru)
 */
@Service
public class EcoGovService {

    public String getEcoGovApi(){
        String url = "http://eco.gov.uz/uz/api/post/get";
        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return response.toString();
    }

}
