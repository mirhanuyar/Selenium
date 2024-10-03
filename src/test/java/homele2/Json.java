package homele2;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Json {

    public static void createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Klasör oluşturulamadı: " + e.getMessage());
        }
    }

    public static void downloadImage(String imageUrl, String downloadPath, String fileName) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.createDirectories(Paths.get(downloadPath));
            Files.copy(in, Paths.get(downloadPath + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("İndirilen fotoğraf: " + downloadPath + "/" + fileName);
        } catch (IOException e) {
            System.out.println("Fotoğraf indirilemedi: " + e.getMessage());
        }
    }

    public static void writeJsonToFile(Map<String, String> data, String filePath) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
            System.out.println("JSON dosyası yazıldı: " + filePath);
        } catch (IOException e) {
            System.out.println("JSON dosyası yazılamadı: " + e.getMessage());
        }
    }
}
