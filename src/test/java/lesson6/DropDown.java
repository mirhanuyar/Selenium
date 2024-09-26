package lesson6;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Scanner;

public class DropDown {
    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();

        driver.get("https://www.amazon.com");

        System.out.println("Captcha'yı çözün ve Enter'a basın.");
        new Scanner(System.in).nextLine();

        //1.işlem Dropdown locate işlemi yapılır
        WebElement dropDownElement = driver.findElement(By.id("searchDropdownBox"));

        //2.işlem select classından bir parametreli(dropdown elementi olacak ) obje oluşturuyorum
        Select select = new Select(dropDownElement);

        //1.yöntem
        select.selectByIndex(3);
        System.out.println("3.indexteki eleman : " + select.getFirstSelectedOption().getText()); //getText dediğimiz zaman direkt stringe çevirir
        Thread.sleep(2000);
        //3.yöntem2.yöntem
        select.selectByValue("search-alias=stripbooks-intl-ship");
        System.out.println("Value : " + select.getFirstSelectedOption().getText());//books çıktısı gelmeli burada
        Thread.sleep(2000);

        //3.yöntem
        select.selectByVisibleText("Digital Music");
        System.out.println("Visible Text : " + select.getFirstSelectedOption().getText());//digital music
        Thread.sleep(2000);

        //tüm optionları getirsin istiyorum
        List<WebElement> optionList = select.getOptions();
        int sayac = 0;
        System.out.println("/n/");

        for (WebElement w : optionList) {
            System.out.println(sayac+" : " + w.getText());
            sayac++;
        }
        driver.close();
    }
}
