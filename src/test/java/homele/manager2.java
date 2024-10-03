package homele;

import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.json.TypeToken;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;


public class manager2 {
    static Gson gson = new Gson();

    public static void main(String[] args) throws InterruptedException {


        WebDriver webDriver = new ChromeDriver();

        webDriver.get("https://homele.com/tr/properties");
        webDriver.get("https://homele.com/tr/agency-detail/1073");

        Thread.sleep(6000);


        String filePath = "/Users/mirhanuyar/Desktop/untitled folder/details.json";
        Agency agency = readAgency(filePath);

        if (agency == null ) {
            agency = yeniAgencyOlustur(webDriver);
        }

        yeniAgentEkle(agency, webDriver);

        writeAgency(agency,filePath);
    }

    public static Agency yeniAgencyOlustur(WebDriver webDriver) {
        Agency agency = new Agency();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));

        WebElement agencyNameElement = webDriver.findElement(By.xpath("//h2[@class='h4']"));
        String agencyName = agencyNameElement.getText().trim();

        WebElement logoElement = webDriver.findElement(By.xpath("//img[@class='img-fluid p-3']"));
        String logoUrl = logoElement.getAttribute("src");

        WebElement phoneElement1 = webDriver.findElement(By.xpath("//a[contains(@href, 'tel:+9647732324884')]"));
        String phone1 = phoneElement1.getText().trim();

        WebElement phoneElement2 = webDriver.findElement(By.xpath("//a[contains(@href, 'tel:+9647506074884')]"));
        String phone2 = phoneElement2.getText().trim();

        agency.setName(agencyName);
        agency.setLogoUrl(logoUrl);
        agency.setPhoneNumbers(Arrays.asList(phone1, phone2));

        agency.setAgents(new ArrayList<>());

        return agency;
    }


    public static void yeniAgentEkle(Agency agency, WebDriver webDriver) {
        Agent yeniAgent = new Agent();
        yeniAgent.setId("12345");
        yeniAgent.setName("İsmail Şahin");
        yeniAgent.setEmail("ismail@gmail.com");
        yeniAgent.setPhoneNumber("544444444");
        yeniAgent.setLanguages("ingilizce,arapça,kürtçe,türkçe");
        yeniAgent.setPhotoUrl("http://photo.url");
        agency.getAgents().add(yeniAgent);
    }

    public static void writeAgency(Agency agency, final String path) {
        try(FileWriter writer = new FileWriter(path)){
            gson.toJson(agency, writer);
        }catch (Exception e){
            e.getMessage();
        }
    }

    public static Agency readAgency(final String path) {
        try (FileReader reader = new FileReader(path)) {
            Type agencyType = new TypeToken<Agency>() {}.getType();
            return gson.fromJson(reader, agencyType);
        } catch (Exception e) {
            return null;
        }
    }
}
