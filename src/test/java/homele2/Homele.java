package homele2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Homele {




    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        try {
            Advert advert = new Advert(driver);
            advert.scrapeAllAdverts();
        } finally {
            driver.quit();
        }
    }
}
