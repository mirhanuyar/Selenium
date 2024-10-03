package homele;

import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.json.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Manager {
    static Gson gson = new Gson();
    static String AGENCY_FILE_PATH = "/Users/mirhanuyar/Desktop/work/agency/__AGENCY_ID__";
    static String ADVERT_FILE_PATH = "/Users/mirhanuyar/Desktop/work/advert/__ADVERT_ID__";

    public static void main(String[] args) throws InterruptedException {

        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://homele.com/tr/properties");

        //TODO: 1.ilana tıkla
        //TODO:      1.a: ilan numarasını oku
        String advertId = "4324t215435";

        //TODO: 2.agency sayfasına git
        Agency agency = createAgencyIfNeeded(webDriver);

    }

    public static Agency createAgencyIfNeeded(WebDriver webDriver) {

        //TODO: agencynin idsini al
        String agencyId = "234";
        String agencyPath = AGENCY_FILE_PATH.replace("__AGENCY_ID__", agencyId);
        Agency agency = readAgency(agencyPath + "/detail.json");
        if (agency != null) {
            return agency;
        }

        agency = new Agency();
        agency.setAgents(new ArrayList<>());

        WebElement agencyNameElement = webDriver.findElement(By.xpath("//h2[@class='h4']"));
        String agencyName = agencyNameElement.getText().trim();

        WebElement logoElement = webDriver.findElement(By.xpath("//img[@class='img-fluid p-3']"));
        String logoUrl = logoElement.getAttribute("src");

        WebElement phoneElement1 = webDriver.findElement(By.xpath("//a[contains(@href, 'tel:+9647732324884')]"));
        String phone1 = phoneElement1.getText().trim();

        WebElement phoneElement2 = webDriver.findElement(By.xpath("//a[contains(@href, 'tel:+9647506074884')]"));
        String phone2 = phoneElement2.getText().trim();

        agency.setId(agencyId);
        agency.setName(agencyName);
        agency.setLogoUrl(logoUrl);
        agency.setPhoneNumbers(List.of(phone1, phone2));

        //TODO: Şu an agency sayfasında olmasılın
        // Tum agent idlerini al
        List<String> agentIds = List.of("123", "432", "456", "345");
        for (String agentId : agentIds) {
            yeniAgentEkle(agency, webDriver, agencyPath, agentId);
        }

        writeAgency(agency, agencyPath);

        return agency;
    }

    public static void yeniAgentEkle(Agency agency, WebDriver webDriver, String agencyPath, String agentId) {
        // TODO: agentId yi kullanarak agent sayfasına git, ve aşağıdaki gerekli bilgileri webdriverdan oku

        Agent yeniAgent = new Agent();
        yeniAgent.setId(agentId);
        yeniAgent.setName("İsmail Şahin");
        yeniAgent.setEmail("ismail@gmail.com");
        yeniAgent.setPhoneNumber("544444444");
        yeniAgent.setLanguages("ingilizce,arapça,kürtçe,türkçe");
        yeniAgent.setPhotoUrl(agencyPath + "/agent_" + agentId);
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
