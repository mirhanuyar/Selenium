package homele;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;

public class Homele2 {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        driver.manage().window().maximize();
        String baseUrl = "https://homele.com/tr/properties?page=";

        // Klasör yolunu buraya yazabilirsiniz
        String anaKlasor = "C:/Users/Kullanici/indirilenler/ilanlar"; // İstediğiniz klasör yolunu girin

        int pageNumber = 1;
        boolean hasNextPage = true;

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

                    // İlan ID'sini URL'den çekme
                    String ilanId = ilanUrl.substring(ilanUrl.lastIndexOf('/') + 1);

                    // Agent ID ve Agency ID çekme
                    WebElement agentIdElement = driver.findElement(By.xpath("//element[@class='agent-id']")); // XPath'i güncelleyin
                    String agentId = agentIdElement.getText().trim();
                    WebElement agencyIdElement = driver.findElement(By.xpath("//element[@class='agency-id']")); // XPath'i güncelleyin
                    String agencyId = agencyIdElement.getText().trim();

                    // Fotoğraf çekme
                    WebElement agentPhotoElement = driver.findElement(By.xpath("//img[@class='agent-photo']"));
                    String agentPhotoUrl = agentPhotoElement.getAttribute("src");
                    WebElement agencyPhotoElement = driver.findElement(By.xpath("//img[@class='agency-photo']"));
                    String agencyPhotoUrl = agencyPhotoElement.getAttribute("src");

                    // Fotoğrafları indir - İlan klasörünü oluşturma
                    String ilanKlasorPath = anaKlasor + "/" + ilanId;
                    downloadSliderImages(driver, "item.mx-2.slick-slide.slick-current.slick-active.slick-center", ilanKlasorPath, wait, ilanTitle);

                    // Ajans klasörü için fotoğrafları kaydetme
                    String agencyFolderPath = anaKlasor + "/ajans/";
                    Files.createDirectories(Paths.get(agencyFolderPath));

                    downloadImage(agentPhotoUrl, agencyFolderPath, agentId + ".jpg");
                    downloadImage(agencyPhotoUrl, agencyFolderPath, agencyId + ".jpg");

                    // JSON Dosyasını yazma
                    Map<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("item_id", ilanId);
                    jsonMap.put("agent_id", agentId);
                    jsonMap.put("agency_id", agencyId);

                    Gson gson = new Gson();
                    try (FileWriter writer = new FileWriter(agencyFolderPath + "data_" + ilanId + ".json")) {
                        gson.toJson(jsonMap, writer);
                        System.out.println("JSON dosyası yazıldı: " + agencyFolderPath + "data_" + ilanId + ".json");
                    }

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
        Set<String> downloadedImages = new HashSet<>();
        boolean hasNextImage = true;
        int imageCount = 1;

        String ilanFolderPath = downloadBasePath;
        try {
            Files.createDirectories(Paths.get(ilanFolderPath));
            System.out.println("Klasör oluşturuldu: " + ilanFolderPath);
        } catch (IOException e) {
            System.out.println("Klasör oluşturulamadı: " + e.getMessage());
            return;
        }

        while (hasNextImage) {
            try {
                WebElement currentImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("." + sliderClass + " img")));
                String imageUrl = currentImage.getAttribute("src");

                if (downloadedImages.contains(imageUrl)) {
                    hasNextImage = false;
                    System.out.println("Son fotoğrafa ulaşıldı.");
                    break;
                }

                downloadedImages.add(imageUrl);
                String fileName = "image_" + imageCount + ".jpg";
                downloadImage(imageUrl, ilanFolderPath, fileName);
                imageCount++;

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
