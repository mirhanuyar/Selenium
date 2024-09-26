package lesson3;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverNavigateMethods {
    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        //1.amazon sayfasına gidelim
        driver.navigate().to("http://www.amazon.com");
        Thread.sleep(3000);
        //2.youtube sayfasına gidelim
        driver.navigate().to("http://www.youtube.com");
        Thread.sleep(3000);
        //3.tekrar amazon sayfasına dönelim
        driver.navigate().back(); //böyle dediğimizde bir önceki yere geliyor !
        Thread.sleep(3000);
        //4.yeniden youtube sayfasına gidelim
        driver.navigate().forward();//böyle dediğimizde bir ileriye gidiyor !
        Thread.sleep(3000);
        //5.sayfayı refresh edelim
        driver.navigate().refresh();
        Thread.sleep(3000);
        //6.sayfayı kapatalım
        driver.quit(); // driver'ı kapatır.'

    }
}
