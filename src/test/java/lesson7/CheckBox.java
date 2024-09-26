package lesson7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CheckBox {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        String url = "https://the-internet.herokuapp.com/checkboxes";
        driver.get(url);

        // Checbox1 ve checbox2 elementlerini locate edin
        WebElement checkbox1 = driver.findElement(By.xpath("(//input[@type='checkbox'])[1]"));
        WebElement checkbox2 = driver.findElement(By.xpath("(//input[@type='checkbox'])[2]"));
        // Checbox1 seçili değilse onay kutusuna tıklayın
        if (!checkbox1.isSelected()) {
            Thread.sleep(3000);
            checkbox1.click();
        }
        // Checbox2 seçili değilse onay kutusuna tıklayın
        if (!checkbox2.isSelected()) {
            Thread.sleep(3000);
            checkbox2.click();
        }

        Thread.sleep(3000);
        driver.quit();
    }
}
