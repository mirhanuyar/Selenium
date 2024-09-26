package lesson1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstTest_Class {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver.driver","/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        WebDriver driver = new ChromeDriver();

        driver.get("https://www.amazon.com");

        Thread.sleep(3000);

        driver.close();
    }
}
