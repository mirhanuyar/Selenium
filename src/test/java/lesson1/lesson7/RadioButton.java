package lesson1.lesson7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class RadioButton {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.get("https://www.facebook.com");

        //facebook adresine git

        //"Yeni hesap oluşturun" butonuna bas
        WebElement yeniHesapOluştur_button = driver.findElement(By.xpath("//a[.=\"Create new account\"]"));
        yeniHesapOluştur_button.click();
        //"radio button" elementlerini locate et
        Thread.sleep(3000);
        WebElement cinsiyet_Radiobutton = driver.findElement(By.xpath("//input[@value=\"2\"]"));
        cinsiyet_Radiobutton.click();
        //secili değilse cinsiyet butonundan cinsiyet seç
        if (!cinsiyet_Radiobutton.isSelected()){
            Thread.sleep(2000);
            cinsiyet_Radiobutton.click();
            Thread.sleep(2000);
        }
    }
}
