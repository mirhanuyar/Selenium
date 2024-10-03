package homele;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Homele1 {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        driver.manage().window().maximize();
        String baseUrl = "https://homele.com/tr/properties?page=";

        int pageNumber = 1;
        boolean hasNextPage = true;
        List<Map<String, Object>> allProperties = new ArrayList<>();

        while (hasNextPage) {
            String url = baseUrl + pageNumber;
            System.out.println("Sayfa URL: " + url);
            driver.get(url);

            List<WebElement> ilanlar = driver.findElements(By.xpath("//a[contains(@class, 'property-image')]"));

            if (ilanlar.isEmpty()) {
                System.out.println("Son sayfaya ulaşıldı.");
                hasNextPage = false;
                break;
            }

            for (WebElement ilan : ilanlar) {
                String ilanUrl = ilan.getAttribute("href");
                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", ilanUrl);
                driver.switchTo().window(driver.getWindowHandles().stream().reduce((first, second) -> second).orElse(null));

                Map<String, String> ilanDetaylari = new HashMap<>();
                try {
                    WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".title.fs-5.fw-bold")));
                    String ilanTitle = titleElement.getText().trim();
                    ilanDetaylari.put("ilan başlığı:", ilanTitle);

                    List<WebElement> listItems = driver.findElements(By.xpath("//li[contains(@class, 'list-group-item')]"));
                    for (WebElement listItem : listItems) {
                        String key = listItem.findElements(By.tagName("span")).get(0).getText().trim();
                        String value = listItem.findElements(By.tagName("span")).get(1).getText().trim();
                        ilanDetaylari.put(key, value);
                    }

                    System.out.println("Fotoğraflar indiriliyor...");
                    downloadSliderImages(driver, "item.mx-2.slick-slide.slick-current.slick-active.slick-center", "/Users/mirhanuyar/Desktop/homele/Büyük", wait, ilanTitle);
                    downloadSliderImages(driver, "property-slider-nav\"", "/Users/mirhanuyar/Desktop/homele/Küçük", wait, ilanTitle);

                    System.out.println("İlan Detayları:");
                    for (Map.Entry<String, String> entry : ilanDetaylari.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    System.out.println("=================================");

                } catch (Exception e) {
                    System.out.println("Bilgiler alınamadı: " + e.getMessage());
                }

                driver.close();
                driver.switchTo().window(driver.getWindowHandles().iterator().next());
            }

            pageNumber++;
            Thread.sleep(3000);
        }
        driver.quit();
    }

    public static void downloadSliderImages(WebDriver driver, String sliderClass, String downloadBasePath, WebDriverWait wait, String ilanTitle) {
        Set<String> downloadedImages = new HashSet<>(); // Benzersiz fotoğraf URL'lerini saklamak için
        boolean hasNextImage = true;
        int imageCount = 1;

        // Her ilan için başlığa göre bir klasör oluştur
        String ilanFolderPath = downloadBasePath + File.separator + formatFileName(ilanTitle);
        try {
            Files.createDirectories(Paths.get(ilanFolderPath));
            System.out.println("Klasör oluşturuldu: " + ilanFolderPath);
        } catch (IOException e) {
            System.out.println("Klasör oluşturulamadı: " + e.getMessage());
            return;
        }

        while (hasNextImage) {
            try {
                // Şu anki resmi bul
                WebElement currentImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("." + sliderClass + " img")));
                String imageUrl = currentImage.getAttribute("src");

                // Eğer URL daha önce indirildiyse döngüyü sonlandır
                if (downloadedImages.contains(imageUrl)) {
                    hasNextImage = false;
                    System.out.println("Son fotoğrafa ulaşıldı.");
                    break;
                }

                // Fotoğrafı indir ve URL'yi listeye ekle
                downloadedImages.add(imageUrl);
                String fileName = "image_" + imageCount + ".jpg";
                downloadImage(imageUrl, ilanFolderPath, fileName); // Her ilanın klasörüne indir
                imageCount++;

                // 'Next' butonuna tıkla, bir sonraki fotoğrafa geç
                WebElement nextButton = driver.findElement(By.cssSelector(".slick-next"));
                if (nextButton.isEnabled()) {
                    wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
                    Thread.sleep(7000);
                } else {
                    hasNextImage = false;
                }

            } catch (Exception e) {
                System.out.println("Fotoğraf bulunamadı: " + e.getMessage());
                hasNextImage = false;
            }
        }
    }

    public static String formatFileName(String title) {
        return title.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
    }

    public static void downloadImage(String imageUrl, String downloadPath, String fileName) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.createDirectories(Paths.get(downloadPath));
            Files.copy(in, Paths.get(downloadPath + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("İndirilen fotoğraf: " + downloadPath + File.separator + fileName);
        } catch (IOException e) {
            System.out.println("Fotoğraf indirilemedi: " + e.getMessage());
        }
    }
}
