package lesson5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Scanner;

public class Practise_2 {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        //amazon web sayfasına gidin

        driver.get("http://www.amazon.com/");
        System.out.println("Captcha'yı çözün ve Enter'a basın.");
        new Scanner(System.in).nextLine();

        //search kutusuna "laptop" yazıp aratın

        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys("laptop");
        searchBox.submit();

        //amazonda görüntülenen ilgili sonuçların sayısını yazdırın

        WebElement aramaSonucuElement = driver.findElement(By.xpath("(//div[@class= \"a-section a-spacing-small a-spacing-top-small\"]/span)[1]"));
        System.out.println("Arama sonucu : " + aramaSonucuElement.getText());

        //sonra karşınıza çıkan ilk sonucun resmine tıklayın

        WebElement firstPicture = driver.findElement(By.xpath("(//img[@class=\"s-image\"][1])"));
        firstPicture.click();

        //2 sn bekletin ve browseri kapatın

        Thread.sleep(7000);
        driver.quit();
    }
}
