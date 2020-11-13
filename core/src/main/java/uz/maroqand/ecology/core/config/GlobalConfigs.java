package uz.maroqand.ecology.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Data
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "globals")
public class GlobalConfigs {

    private String IsTesting;
    private String UploadedFilesFolder;
    private String ServerIp;
    private String LocalIp;

    /*public String getIsTesting() {
        return IsTesting;
    }

    public void setIsTesting(String isTesting) {
        IsTesting = isTesting;
    }

    public String getUploadedFilesFolder() {
        return UploadedFilesFolder;
    }

    public void setUploadedFilesFolder(String uploadedFilesFolder) {
        UploadedFilesFolder = uploadedFilesFolder;
    }*/
}
