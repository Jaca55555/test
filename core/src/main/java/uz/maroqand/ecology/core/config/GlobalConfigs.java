package uz.maroqand.ecology.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "globals")
public class GlobalConfigs {

    private String UploadedFilesFolder;

    public String getUploadedFilesFolder() {
        return UploadedFilesFolder;
    }

    public void setUploadedFilesFolder(String uploadedFilesFolder) {
        UploadedFilesFolder = uploadedFilesFolder;
    }
}
