package uz.maroqand.ecology.cabinet.util;

import java.io.ByteArrayOutputStream;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.URL;  
  
public class ImageUtil {  
  
      
    public static byte[] getImageFromNetByUrl(String strUrl) {  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5 * 1000);  
            InputStream inStream = conn.getInputStream();
            byte[] btImg = readInputStream(inStream);
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
      
    public byte[] getImageFromLocalByUrl(String strUrl) {  
        try {  
            File imageFile = new File(strUrl);  
            InputStream inStream = new FileInputStream(imageFile);  
            byte[] btImg = readInputStream(inStream);
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
      
    private static byte[] readInputStream(InputStream inStream) throws IOException {  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[10240];  
        int len = 0;  
        while ((len = inStream.read(buffer)) != -1) {  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
  
    }  

}  