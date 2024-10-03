package lesson1.lesson4;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriveManageMethods2 {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        //1.amazon sayfasına gidelim . http://www.amazon.com/
        driver.get("http://www.amazon.com");
        Thread.sleep(3000);
        //2.sayfanın konumunu ve boyutlarını yazdıralım
        System.out.println("sayfanın konumu" + driver.manage().window().getPosition());
        System.out.println("sayfanın boyutu : " + driver.manage().window().getSize());
        Thread.sleep(3000);
        //3.sayfanın konumunu ve boyutunu istediğimiz şekilde ayarlayalım
        driver.manage().window().setPosition(new Point(0,0));
        driver.manage().window().setSize(new Dimension(60,60));
        //4.sayfanın istediiğmiz konuma ve boyuta gelmesini test edelim
        int xKoor = driver.manage().window().getPosition().getX();
        int ykoor = driver.manage().window().getPosition().getY();
        int width = driver.manage().window().getSize().getWidth();
        int height = driver.manage().window().getSize().getHeight();

        if (xKoor == 0 && ykoor == 0 && width == 60 && height == 60) {
            System.out.println("Konum ve boyut testi başarılı");
        }else{
            System.out.println("konum ve boyut testi başarısız ");
        }
        //5.sayfayı kapatın
        driver.close();
    }
}
