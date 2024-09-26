package lesson5;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class practise_1 {
    public static void main(String[] args) throws InterruptedException {


        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();

        //https://www.amazon.com.tr// URL'e git
        driver.get("https://amazon.com.tr//");
        //çerezleri kabul et
        driver.findElement(By.id("sp-cc-accept")).click();
        // arama butonuna iphone 11 yaz ve arama işlemini yap
        driver.findElement(By.name("field-keywords")).sendKeys("iphone 11", Keys.ENTER);//burada veriyi girip direkt entera basar
        // arama sonucunun iphone 11 olup olmadığını kontrol et
            String aramaSonucuTest =
                    driver.findElement(By.xpath("(//div[@class='a-section a-spacing-small a-spacing-top-small']/span)[1]")).getText()+
                    driver.findElement(By.xpath("(//div[@class='a-section a-spacing-small a-spacing-top-small']/span)[3]")).getText();

        System.out.println("Arama Sonucu : " + aramaSonucuTest);
        if (aramaSonucuTest.contains("iphone 11")){
            System.out.println("test başarılı");
        }else {
            System.out.println("test başarısız");
        }
        // ilk ürünün fiyatını console yazdırın
        System.out.println("ilk ürün fiyatı " + driver.findElement(By.xpath("(//span[@class=\"a-price-whole\"])[1]")).getText());
        // 2 saniye bekletin ve browserı kapatın
        driver.quit();
    }
}