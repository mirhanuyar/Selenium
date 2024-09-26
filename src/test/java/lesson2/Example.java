package lesson2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Example {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        //1.amazon sayfasına gidelim https://www.amazon.com/
        driver.get("https://www.amazon.com");
        Thread.sleep(3000);
        //2.sayfa başlığını (title) yazdırın
        System.out.println("sayfanın başlığı : " + driver.getTitle());
        String actualTitle = driver.getTitle();
        //3. sayfa başlığının Amazon içerdiğini test edin
        String expectedTitle = "Amazon.com";
        if (actualTitle.equals(expectedTitle)) {
            System.out.println("Başarılı: Sayfa başlığı doğru");
        } else {
            System.out.println("Başarısız: Sayfa başlığı yanlış");
        }
        //4.sayfa adresini (url) yazdırın
        System.out.println("sayfanın url : " + driver.getCurrentUrl());
        //5.sayfa url'nin "amazon" içerdiğini test edin
        String actualUrl = driver.getCurrentUrl();
        String expectedUrl = "https://www.amazon.com";
        if (actualUrl.contains(expectedUrl)) {
            System.out.println("Başarılı: Sayfa url doğru");
        } else {
            System.out.println("Başarısız: Sayfa url yanlış");
        }
        //6.sayfa handle değerini yazdırın
        System.out.println("sayfanın handle değeri : " + driver.getWindowHandle());
        //7.sayfa HTML kodlarında "shopping" kelimesinin geçtiğini test edin
        String pageSource = driver.getPageSource();
        if (pageSource.contains("shopping")) {
            System.out.println("Başarılı: Sayfa HTML'da 'shopping' geçer");
        } else {
            System.out.println("Başarısız: Sayfa HTML'da 'shopping' geçmedi");
        }
        //sayfayı kapatın
        driver.close();
    }

}
