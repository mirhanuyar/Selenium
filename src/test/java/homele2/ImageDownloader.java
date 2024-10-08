package homele2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageDownloader {
    public static void downloadSliderImages(WebDriver driver, String downloadBasePath) {
        Set<String> downloadedImages = new HashSet<>();
        int imageCount = 1;

        List<WebElement> elements = driver.findElements(By.cssSelector(".property-slider-nav > div"));
        for (WebElement element : elements) {
            try {
                WebElement img = element.findElement(By.xpath(".//img"));
                if (img == null) {
                    continue;
                }
                String imageUrl = img.getAttribute("src");

                if (downloadedImages.contains(imageUrl)) {
                    System.out.println("Son fotoğrafa ulaşıldı.");
                    continue;
                }
                downloadedImages.add(imageUrl);
                String fileName = "image_" + imageCount + ".jpg";
                downloadImage(imageUrl, downloadBasePath, fileName);
                imageCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String downloadImageInElement(WebDriver driver, String downloadPath, String fileName, String cssSelector) {
        WebElement webElement = driver.findElement(By.cssSelector(cssSelector));
        if (webElement != null) {
            ImageDownloader.downloadImage(webElement.getDomAttribute("src"), downloadPath, fileName);
            return downloadPath + "/" + fileName;
        }

        return null;
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
}
