package homele;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.Duration;


public class Homele1 {
    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();

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

                    WebElement titleElement = driver.findElement(By.cssSelector(".title.fs-5.fw-bold"));
                    String ilanTitle = titleElement.getText().trim();
                    ilanDetaylari.put("ilan başlığı :", ilanTitle);

                    List<WebElement> listItems = driver.findElements(By.xpath("//li[contains(@class, 'list-group-item')]"));

                    for (WebElement listItem : listItems) {

                        String key = listItem.findElements(By.tagName("span")).get(0).getText().trim();

                        String value = listItem.findElements(By.tagName("span")).get(1).getText().trim();

                        ilanDetaylari.put(key, value);
                    }

                    List<WebElement> amenitiesItems = driver.findElements(By.className("amenities-container"));

                    if (!amenitiesItems.isEmpty()) {
                        for (WebElement amenity : amenitiesItems) {
                            String amenityText = amenity.getText().trim();
                            ilanDetaylari.put("Amenities", amenityText);
                        }
                    }

                    System.out.println("İlan Detayları:");
                    for (Map.Entry<String, String> entry : ilanDetaylari.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    System.out.println("=================================");

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // 10 saniye bekleme süresi
                    WebElement agentNameElement = wait.until(ExpectedConditions.elementToBeClickable(By.className("agent-name")));
                    agentNameElement.click();

                    //müşteri temsilcisi Detayları yazdırma
                    WebElement agentDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("agency-detail")));
                    System.out.println("Agent Detayları: " + agentDetails.getText());

                } catch (Exception e) {
                    System.out.println("Bilgiler alınamadı: " + e.getMessage());
                }

                driver.close();

                driver.switchTo().window(driver.getWindowHandles().iterator().next());
            }

            pageNumber++;

            Thread.sleep(7000);
        }
        driver.quit();
    }
}
