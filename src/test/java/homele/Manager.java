package homele;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.json.TypeToken;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class Manager {
    static Gson gson = new Gson();
    static String AGENCY_FILE_PATH = "/Users/mirhanuyar/Desktop/work/agency/__AGENCY_ID__";
    static String ADVERT_FILE_PATH = "/Users/mirhanuyar/Desktop/work/advert/__ADVERT_ID__";

    private static WebDriver driver;
    private static WebDriverWait wait;
    
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver.driver", "/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(50));

        String baseUrl = "https://homele.com/tr/properties?page=";
        int pageNumber = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            driver.get(baseUrl + pageNumber);
            List<WebElement> ilanlar = driver.findElements(By.xpath("//a[contains(@class, 'property-image')]"));
            String lastTab = getLastTabId();
            if (ilanlar.isEmpty()) {
                System.out.println("Son sayfaya ulaşıldı.");
                hasNextPage = false;
                break;
            }

            for (WebElement ilan : ilanlar) {
                String ilanUrl = ilan.getAttribute("href");
                createAdvert(ilanUrl);
                driver.switchTo().window(lastTab);
            }

            pageNumber++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void createAdvert(String ilanUrl) {
        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", ilanUrl);
        String lastTab = getLastTabId();
        driver.switchTo().window(lastTab);

        try {
            String ilanId = ilanUrl.substring(ilanUrl.lastIndexOf('/') + 1);
            System.out.println("Ilan ID: " + ilanId);

            // todo: aşağıdaki fonksiyonu çağırmadan önce, advert bilgilerini oku ve oluştur


            Agency agency = createAgencyIfNeeded();

            driver.switchTo().window(lastTab);
            driver.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Agency createAgencyIfNeeded() {
        String agencyId = getFromScript("agency_id");
        String agencyPath = AGENCY_FILE_PATH.replace("__AGENCY_ID__", agencyId);
        String detailsPath = agencyPath + "/details.json";
        Agency agency = readAgency(detailsPath);
        if (agency != null) {
            return agency;
        }


        agency = new Agency();
        agency.setAgents(new ArrayList<>());

        WebElement webElement = driver.findElement(By.cssSelector(".agency-logo > a"));
        String agencyDetailUrl = webElement.getAttribute("href");

        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", agencyDetailUrl);
        String lastTab = getLastTabId();
        driver.switchTo().window(lastTab);

        WebElement agencyNameElement = driver.findElement(By.xpath("//h2[@class='h4']"));
        String agencyName = agencyNameElement.getText().trim();

        WebElement logoElement = driver.findElement(By.xpath("//img[@class='img-fluid p-3']"));
        String logoUrl = logoElement.getAttribute("src");

        List<WebElement> contactDiv = driver.findElements(By.cssSelector(".btn-contacts > div > a"));

        List<String> emails = new ArrayList<String>();
        List<String> phones = new ArrayList<String>();
        for (WebElement element : contactDiv) {
            if (element.getAttribute("onclick").contains("call")) {
                phones.add(element.getDomAttribute("href").replace("tel:", ""));
            } else {
                String mailto = element.getDomAttribute("href");
                String mail = mailto.substring(0, mailto.indexOf("?")).replace("mailto:", "");
                emails.add(mail);
            }
        }

        agency.setId(agencyId);
        agency.setName(agencyName);
        agency.setLogoUrl(logoUrl);
        agency.setPhoneNumbers(phones);
        agency.setEmails(emails);

        List<WebElement> webElements = driver.findElements(By.cssSelector(".card-agent > a"));

        List<String> agentUrls = new ArrayList<String>();
        for (WebElement element : webElements) {
            agentUrls.add(element.getAttribute("href"));
        }

        for (String agentUrl : agentUrls) {
            yeniAgentEkle(agency, agencyPath, agentUrl);
            driver.switchTo().window(lastTab);
        }

        writeAgency(agency, detailsPath);

        driver.close();

        return agency;
    }

    public static void yeniAgentEkle(Agency agency, String agencyPath, String agentUrl) {

        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", agentUrl);
        driver.switchTo().window(getLastTabId());
        Agent agent = new Agent();

        List<WebElement> phones = driver.findElements(By.cssSelector(".btn-contacts .dropdown .dropdown-menu a"));
        for (WebElement phone : phones) {
            agent.getPhoneNumbers().add(phone.getText());
        }

        List<WebElement> details = driver.findElements(By.cssSelector(".agency-stats > div"));
        for (WebElement detail : details) {

            if(!detail.findElements(By.cssSelector(".icon-language")).isEmpty()) {
                agent.setLanguages(List.of(detail.getText().replace(" ve ", ", ").split(", ")));
            } else if(!detail.findElements(By.cssSelector(".icon-email")).isEmpty()) {
                agent.setEmails(List.of(detail.getText().replace("M: ", "")));
            }
        }
        String agentId = agentUrl.substring(agentUrl.lastIndexOf('/') + 1);

        agent.setId(agentId);
        agent.setName("İsmail Şahin");
        agent.setPhotoUrl(agencyPath + "/agent_" + agentId);
        agency.getAgents().add(agent);

        driver.close();
    }

    public static void writeAgency(Agency agency, final String path) {

        String json = gson.toJson(agency);
        try {
            FileUtils.writeStringToFile(new File(path), json, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    private static String getFromScript(String key) {
        WebElement agencyScriptElement = driver.findElement(By.xpath("//script[contains(text(), '" + key + "')]"));
        String scriptContent = agencyScriptElement.getAttribute("innerHTML");
        return scriptContent.split("'" + key+ "': '")[1].split("'")[0];
    }

    public static String getLastTabId() {
        ArrayList<String> list = new ArrayList(driver.getWindowHandles());
        return list.get(list.size() - 1);
    }

    /*
    private static void scrapeAndSaveDetails(Map<String, String> ilanDetaylari, Map<String, String> agentDetaylari, String ilanId, String ilanTitle) {
        String ilanKlasorPath = "/Users/mirhanuyar/Desktop/homele/adverts/" + ilanId;
        Json.createDirectory(ilanKlasorPath);
        ImageDownloader.downloadSliderImages(driver, "item.mx-2.slick-slide.slick-current.slick-active.slick-center", ilanKlasorPath, wait, ilanTitle);

        Json.writeJsonToFile(ilanDetaylari, ilanKlasorPath + "/" + ilanId + "_advert_details.json");

        Agent agent = new Agent(driver, wait);
        agent.scrapeAgentDetails(ilanDetaylari, agentDetaylari, ilanId);
    }*/
}
