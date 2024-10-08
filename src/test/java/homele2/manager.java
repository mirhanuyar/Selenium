package homele2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class manager {
    public static void main(String[] args) {
        ChromeDriver driver = new ChromeDriver();
        String filePath = "/Users/mirhanuyar/Desktop/dosyaurl/list.txt";

        List<String> ilanLinkleri = new ArrayList<>();
        int pageNumber = 1;
        boolean hasNextPage = true;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            while (hasNextPage) {
                String url = "https://homele.com/tr/properties?page=" + pageNumber;
                driver.get(url);


                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".property-listing-card")));
                List<WebElement> ilanKartlari = driver.findElements(By.cssSelector(".property-listing-card"));

                writer.write("Sayfa " + pageNumber + " için bulunan ilan sayısı: " + ilanKartlari.size());
                writer.newLine();

                if (ilanKartlari.isEmpty()) {
                    hasNextPage = false;
                    writer.write("İlan bulunamadı. İşlem tamamlandı.");
                    writer.newLine();
                    System.out.println("İlan bulunamadı. İşlem tamamlandı.");
                    break;
                }

                for (WebElement ilan : ilanKartlari) {
                    WebElement ilanLinkElement = ilan.findElement(By.cssSelector("a[href*='/property-details/']"));
                    String ilanLink = ilanLinkElement.getAttribute("href");
                    ilanLinkleri.add(ilanLink);
                    writer.write(ilanLink);
                    writer.newLine();
                    System.out.println(ilanLink);
                }

                pageNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
