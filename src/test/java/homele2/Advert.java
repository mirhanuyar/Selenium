package homele2;

import com.google.gson.Gson;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Advert {
    private WebDriver driver;
    private WebDriverWait wait;

    public Advert(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
    }

    public void scrapeAllAdverts() throws InterruptedException {
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
                scrapeAdvertDetails(ilanUrl);
            }

            pageNumber++;
            Thread.sleep(3000);
        }
    }

    public void scrapeAdvertDetails(String ilanUrl) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", ilanUrl);
        driver.switchTo().window(driver.getWindowHandles().stream().reduce((first, second) -> second).orElse(null));

        Map<String, String> ilanDetaylari = new HashMap<>();
        Map<String, String> agentDetaylari = new HashMap<>();

        try {
            WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".title.fs-5.fw-bold")));
            String ilanTitle = titleElement.getText().trim();
            ilanDetaylari.put("ilan başlığı:", ilanTitle);

            String ilanId = ilanUrl.substring(ilanUrl.lastIndexOf('/') + 1);
            System.out.println("Ilan ID: " + ilanId);

            scrapeAndSaveDetails(ilanDetaylari, agentDetaylari, ilanId, ilanTitle);


        } catch (Exception e) {
            System.out.println("Bilgiler alınamadı: " + e.getMessage());
            e.printStackTrace();
        }

        driver.close();
        driver.switchTo().window(driver.getWindowHandles().iterator().next());
    }

    private void scrapeAndSaveDetails(Map<String, String> ilanDetaylari, Map<String, String> agentDetaylari, String ilanId, String ilanTitle) {
        String ilanKlasorPath = "/Users/mirhanuyar/Desktop/homele/adverts/" + ilanId;
        Json.createDirectory(ilanKlasorPath);
        ImageDownloader.downloadSliderImages(driver, "item.mx-2.slick-slide.slick-current.slick-active.slick-center", ilanKlasorPath, wait, ilanTitle);

        Gson gson = new Gson();
        Json.writeJsonToFile(ilanDetaylari, ilanKlasorPath + "/" + ilanId + "_advert_details.json");

        Agent agent = new Agent(driver, wait);
        agent.scrapeAgentDetails(ilanDetaylari, agentDetaylari, ilanId);
    }
}
