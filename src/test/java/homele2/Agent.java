package homele2;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

public class Agent {
    private WebDriver driver;
    private WebDriverWait wait;

    public Agent(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void scrapeAgentDetails(Map<String, String> ilanDetaylari, Map<String, String> agentDetaylari, String ilanId) {
        try {
            System.out.println("Ajans detaylarını taramaya başlıyor...");

            WebElement agencyLogoElement = driver.findElement(By.cssSelector(".agency-logo img"));
            String agencyLogoUrl = agencyLogoElement.getAttribute("src");

            WebElement agencyScriptElement = driver.findElement(By.xpath("//script[contains(text(), 'agency_id')]"));
            String scriptContent = agencyScriptElement.getAttribute("innerHTML");
            String agencyId = scriptContent.split("'agency_id': '")[1].split("'")[0];

            String agentFolderPath = "/Users/mirhanuyar/Desktop/homele/agency/" + agencyId;
            Json.createDirectory(agentFolderPath);

            ImageDownloader.downloadImage(agencyLogoUrl, agentFolderPath, "agency_logo.jpg");

            WebElement agentPhotoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".agent-photo img")));
            String agentPhotoUrl = agentPhotoElement.getAttribute("src");
            ImageDownloader.downloadImage(agentPhotoUrl, agentFolderPath, "agent_photo_" + agencyId + ".jpg");
            System.out.println("Ajan fotoğrafı indirildi: " + agentPhotoUrl);

            WebElement contactInfoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".agent-contact-info a")));
            String agentDetailUrl = contactInfoElement.getAttribute("href");
            System.out.println("Ajan detay linki: " + agentDetailUrl);

            driver.get(agentDetailUrl);
            System.out.println("Ajan detay sayfasına yönlendirildi: " + agentDetailUrl);
            Thread.sleep(5000);

            Map<String, String> agentDetails = new HashMap<>();

            extractAgentDetails(agentDetails);

            extractAgencyDetails(ilanDetaylari, agencyId, agentFolderPath);

            Json.writeJsonToFile(agentDetails,agentFolderPath + "/" + agencyId + "details.json");
            System.out.println("Ajan bilgileri başarıyla JSON'a kaydedildi.");


        } catch (Exception e) {
            System.out.println("Ajan bilgileri alınamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void extractAgentDetails(Map<String, String> agentDetails) {
        try {
            System.out.println("Ajan ismi alınıyor...");
            WebElement agentNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".h4.mb-4")));
            String agentName = agentNameElement.getText().trim();
            agentDetails.put("Ajan İsmi", agentName);
            System.out.println("Ajan ismi: " + agentName);

            System.out.println("İlan sayısı alınıyor...");
            WebElement ilanSayisiElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='col-12']")));
            String ilanSayisi = ilanSayisiElement.getText().trim().replace("İlanlar", "").trim();
            agentDetails.put("İlan Sayısı", ilanSayisi);
            System.out.println("İlan sayısı: " + ilanSayisi);

            System.out.println("Konuşulan diller alınıyor...");
            WebElement languagesElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-12 mt-3']")));
            String languages = languagesElement.getText().trim();
            agentDetails.put("Konuşulan Diller", languages);
            System.out.println("Konuşulan diller: " + languages);

            System.out.println("E-posta adresi alınıyor...");
            WebElement emailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-12 mt-2']")));
            String email = emailElement.getText().trim();
            agentDetails.put("E-posta", email);
            System.out.println("E-posta: " + email);

        } catch (Exception e) {
            System.out.println("Agent details extraction failed: " + e.getMessage());
        }
    }

    private void extractAgencyDetails(Map<String, String> agencyDetails, String agencyId, String agentFolderPath) {
        try {
            String agencyUrl = "https://homele.com/tr/agency-detail/" + agencyId;
            driver.get(agencyUrl);
            System.out.println("Ajans sayfasına gidildi: " + agencyUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Sayfa tamamen yüklendi.");

            WebElement agencyNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@class='h4']")));
            String agencyName = agencyNameElement.getText().trim();
            agencyDetails.put("Ajans Adı", agencyName);
            System.out.println("Ajans adı: " + agencyName);

           /* WebElement agencyDescriptionElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[2]/p[1]")));
            String agencyDescription = agencyDescriptionElement.getText().trim();
            agencyDetails.put("Ajans Açıklaması", agencyDescription);
            System.out.println("Ajans açıklaması: " + agencyDescription);

            WebElement agencyCityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]")));
            String agencyCity = agencyCityElement.getText().trim();
            agencyDetails.put("Ajans Şehri", agencyCity);
            System.out.println("Ajans şehri: " + agencyCity);

            WebElement saleCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[3]/a[1]")));
            String saleCount = saleCountElement.getText().trim();
            agencyDetails.put("Satılık İlan Sayısı", saleCount);
            System.out.println("Satılık ilan sayısı: " + saleCount);

            WebElement customerRepElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[2]")));
            String customerReps = customerRepElement.getText().trim();
            agencyDetails.put("Müşteri Temsilcileri", customerReps);
            System.out.println("Müşteri temsilcisi sayısı: " + customerReps);

            WebElement rentCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[4]/a[1]")));
            String rentCount = rentCountElement.getText().trim();
            agencyDetails.put("Kiralık İlan Sayısı", rentCount);
            System.out.println("Kiralık ilan sayısı: " + rentCount);*/

        } catch (TimeoutException e) {
            System.out.println("Timeout: Element bulunamadı, bekleme süresi yetersiz: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element bulunamadı: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ajans bilgileri alınamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
