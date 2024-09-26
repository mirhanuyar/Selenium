package lesson2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class İkinciDers {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        driver.get("https://www.amazon.com");// belirtilen urle gider

        System.out.println("Amazon URL: " + driver.getCurrentUrl()); // tarayıcadaki o an aktif olan url'i getirir

        System.out.println("Amazon Title" + driver.getTitle()); // sayfanı titlenı getirir

        driver.getPageSource(); // tüm HTML kodlarını getirir

        System.out.println(driver.getWindowHandle()); //unic değerleri alır

        System.out.println("Driver class Type : " + driver.getClass()); // hangi driver olduğunu bulabiliriz
    }
}
