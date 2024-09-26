package lesson4;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriveManageMethods {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        //1.youtube sayfasına gidelim
        driver.get("https://www.youtube.com");
        //2.sayfadan konumunu ve boyutlarını yazdırın
        System.out.println("sayfanın konumu : " + driver.manage().window().getPosition());
        System.out.println("sayfanın boyutu : " + driver.manage().window().getSize());
        //3.sayfayı simge durumuna getirin
        driver.manage().window().minimize();
        //4.simge durumunda 3 saniye bekleyip sayfayı maximize yapın
        Thread.sleep(3000);
        driver.manage().window().maximize();
        //5.sayfanın konumununu ve boyutlarını maximize durumunda yazdırın
        System.out.println("max sayfanın konumu : " + driver.manage().window().getPosition());
        System.out.println("max sayfanın boyutu : " + driver.manage().window().getSize());
        //6.sayfayı fullscreen yapın
        driver.manage().window().fullscreen();
        //7.sayfanın konumunu ve boyutlarını fullscreen durumunda yazdırın
        System.out.println("fullscreen sayfanın konumu : " + driver.manage().window().getPosition());
        System.out.println("fullscreen sayfanın boyutu : " + driver.manage().window().getSize());
        //8.sayfayı kapatın
        driver.close();


    }
}
