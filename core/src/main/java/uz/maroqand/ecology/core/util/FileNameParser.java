package uz.maroqand.ecology.core.util;

/**
 * Created by Siroj Matchanov on 12/12/16.
 */
public class FileNameParser {
    public static String getExtensionFromFileName(String fileName) {
        String extension = fileName;
        int i = extension.lastIndexOf('.');
        if (i >= 0) {
            extension = extension.substring(i + 1);
        } else {
            extension = "";
        }
        return extension;
    }
}
