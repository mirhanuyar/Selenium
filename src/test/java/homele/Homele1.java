package homele;

import com.google.gson.Gson;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Homele1 {
    private static final Logger log = LoggerFactory.getLogger(Homele2.class);

    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

        driver.manage().window().maximize();
        String baseUrl = "https://homele.com/tr/properties?page=";

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

                    String ilanId = ilanUrl.substring(ilanUrl.lastIndexOf('/') + 1);
                    System.out.println("Ilan ID: " + ilanId);

                    WebElement agencyScriptElement = driver.findElement(By.xpath("//script[contains(text(), 'agency_id')]"));
                    String scriptContent = agencyScriptElement.getAttribute("innerHTML");

                    String agencyId = scriptContent.split("'agency_id': '")[1].split("'")[0];
                    System.out.println("Agency ID: " + agencyId);

                    WebElement agentScriptElement = driver.findElement(By.xpath("//script[contains(text(), 'agent_id')]"));
                    String agentScriptContent = agentScriptElement.getAttribute("innerHTML");

                    String agentId = agentScriptContent.split("'agent_id': '")[1].split("'")[0];
                    System.out.println("Agent ID: " + agentId);

                    List<WebElement> listItems = driver.findElements(By.xpath("//li[contains(@class, 'list-group-item')]"));

                    for (WebElement listItem : listItems) {
                        String key = listItem.findElements(By.tagName("span")).get(0).getText().trim();
                        String value = listItem.findElements(By.tagName("span")).get(1).getText().trim();
                        ilanDetaylari.put(key, value);
                    }

                    String ilanKlasorPath = "/Users/mirhanuyar/Desktop/homele/adverts/" + ilanId;
                    Files.createDirectories(Paths.get(ilanKlasorPath));
                    System.out.println("Fotoğraflar indiriliyor...");
                    downloadSliderImages(driver, "item.mx-2.slick-slide.slick-current.slick-active.slick-center", ilanKlasorPath, wait, ilanTitle);

                    String agentFolderPath = "/Users/mirhanuyar/Desktop/homele/agency/" + agencyId;
                    Files.createDirectories(Paths.get(agentFolderPath));

                    WebElement agentPhotoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'agent-photo col-auto')]/img")));
                    String agentPhotoUrl = agentPhotoElement.getAttribute("src");
                    downloadImage(agentPhotoUrl, agentFolderPath, "agent_" + agentId + ".jpg");

                    WebElement agencyLogoElement = driver.findElement(By.cssSelector(".agency-logo img"));
                    String agencyLogoUrl = agencyLogoElement.getAttribute("src");
                    downloadImage(agencyLogoUrl, agentFolderPath, "agency_" + agencyId + ".jpg");

                    System.out.println("Ajans logosu indirildi: " + agencyLogoUrl);

                    String agencyDetailUrl = "https://homele.com/tr/agency-detail/" + ilanId;
                    ilanDetaylari.put("agency_detail_url", agencyDetailUrl);

                    Gson gson = new Gson();
                    try (FileWriter advertWriter = new FileWriter(ilanKlasorPath + "/advert_details.json")) {
                        gson.toJson(ilanDetaylari, advertWriter);
                        System.out.println("Advert JSON dosyası yazıldı: " + ilanKlasorPath + "/advert_details.json");
                    }

                    try (FileWriter agentWriter = new FileWriter(agentFolderPath + "/agent_details.json")) {
                        gson.toJson(ilanDetaylari, agentWriter);
                        System.out.println("Agent JSON dosyası yazıldı: " + agentFolderPath + "/agent_details.json");
                    }

                    try {
                        WebElement contactInfoSection = driver.findElement(By.cssSelector(".agent-contact-info.rtl.col.d-flex.justify-content-center.flex-column"));
                        WebElement contactLink = contactInfoSection.findElement(By.tagName("a")); // Bu sınıfın altındaki ilk linki buluyoruz
                        String contactLinkUrl = contactLink.getAttribute("href");
                        System.out.println("Agent Contact Link: " + contactLinkUrl);

                        driver.get(contactLinkUrl);
                        System.out.println("Linke yönlendirildi: " + contactLinkUrl);

                        WebElement yeniSayfaBasligi = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".new-title")));
                        String yeniBaslik = yeniSayfaBasligi.getText();
                        System.out.println("Yeni sayfa başlığı: " + yeniBaslik);

                    } catch (Exception e) {
                        System.out.println("'agent-contact-info' altında link bulunamadı: " + e.getMessage());
                    }

                    System.out.println("İlan Detayları:");
                    for (Map.Entry<String, String> entry : ilanDetaylari.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    System.out.println("=================================");

                } catch (Exception e) {
                    System.out.println("Bilgiler alınamadı: " + e.getMessage());
                    e.printStackTrace();
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
                downloadImage(imageUrl, downloadBasePath, fileName);
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
