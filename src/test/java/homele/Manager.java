package homele;

import com.google.gson.Gson;
import homele2.ImageDownloader;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Manager {
    static Gson gson = new Gson();
    static String AGENCY_FILE_PATH = "/Users/mirhanuyar/Desktop/work/agency/__AGENCY_ID__";
    static String ADVERT_FILE_PATH = "/Users/mirhanuyar/Desktop/work/advert/__ADVERT_ID__";
    static String FILE_PATH = "/Users/mirhanuyar/Desktop/list.txt";

    private static WebDriver driver;
    private static WebDriverWait wait;
    
    public static void main(String[] args) throws IOException {
        System.setProperty("webdriver.chrome.driver.driver", "/Users/mirhanuyar/Desktop/seleniumProject/selenium-java-4.25.0//chromedriver");

        driver = new ChromeDriver();

        List<String> fileContent = new ArrayList<>(Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            String ilanUrl = fileContent.get(i);
            try {
                if (ilanUrl.contains("__DONE__")) {
                    continue;
                }
                boolean result = createAdvert(ilanUrl);
                if (result) {
                    fileContent.set(i, ilanUrl + " __DONE__");
                } else {
                    fileContent.set(i, ilanUrl + " __DONE__ __ERRORED__");
                }
                Files.write(Path.of(FILE_PATH), fileContent, StandardCharsets.UTF_8);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileContent.set(i, ilanUrl + " __DONE__ __ERRORED__");
                Files.write(Path.of(FILE_PATH), fileContent, StandardCharsets.UTF_8);
            }
        }

    }

    public static boolean createAdvert(String ilanUrl) {
        driver.get(ilanUrl);
        String lastTab = getLastTabId();

        try {
            String ilanId = ilanUrl.substring(ilanUrl.lastIndexOf('/') + 1);
            String advertPath = ADVERT_FILE_PATH.replace("__ADVERT_ID__", ilanId);
            System.out.println("Ilan ID: " + ilanId);
            try (FileReader reader = new FileReader(advertPath + "/details.json")) {
                Agency agency = createAgencyIfNeeded();
                if (agency == null) {

                }
                driver.switchTo().window(lastTab);
                return true;
            } catch (Exception e) {

            }


            Map<String, String> keyValues = new HashMap<>();

            readSpanPairs(keyValues, ".list-group-flush.list-group.list-key-value > li");
            readSpanPairs(keyValues, ".amenities-container .rooms .col .content > div > div");
            readSpanPairs(keyValues, ".amenities-container .features .col .content > div > div");

            WebElement element = driver.findElement(By.cssSelector(".section-overview"));
            if (element != null) {
                String description = element.getText().trim();
                keyValues.put("description", description);
            }
            element = driver.findElement(By.cssSelector(".map-image"));
            if (element != null) {
                String latitude = element.getDomAttribute("data-latitude");
                String longitude = element.getDomAttribute("data-longitude");
                keyValues.put("latitude", latitude);
                keyValues.put("longitude", longitude);
            }

            keyValues.put("advertId", ilanId);

            String category = getFromScript("category");
            keyValues.put("category", category);

            String advertTitle = getFromScript("item_name");
            keyValues.put("advertTitle", advertTitle);

            String agencyId = getFromScript("agency_id");
            keyValues.put("agencyId", agencyId);

            String agentId = getFromScript("agent_id");
            keyValues.put("agentId", agentId);

            String priceInUsd = getFromScript("price_in_usd");
            keyValues.put("priceInUsd", priceInUsd);

            String city = getFromScript("city");
            keyValues.put("city", city);

            String area = getFromScript("area");
            keyValues.put("area", area);

            String propertyType = getFromScript("property_type");
            keyValues.put("propertyType", propertyType);

            writeAdvert(keyValues, advertPath + "/details.json");
            ImageDownloader.downloadSliderImages(driver, advertPath);


            Agency agency = createAgencyIfNeeded();

            driver.switchTo().window(lastTab);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static Agency createAgencyIfNeeded() {
        String agencyId = getFromScript("agency_id");
        if (agencyId == null) {
            return null;
        }
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

        List<String> phones = tryReadPhones2();
        if (phones == null) {
            phones = tryReadPhones1();
        }
        agency.setPhoneNumbers(phones);

        List<String> emails = new ArrayList<String>();
        List<WebElement> contactDiv = driver.findElements(By.cssSelector(".btn-contacts > div > a"));
        for (WebElement element : contactDiv) {
            if (element.getAttribute("onclick") != null && element.getDomAttribute("href").contains("mailto")) {
                String mailto = element.getDomAttribute("href");
                String mail = mailto.substring(0, mailto.indexOf("?")).replace("mailto:", "");
                emails.add(mail);
            }
        }

        String fileName = "agency_" + agencyId + ".jpg";
        String imageUrl = ImageDownloader.downloadImageInElement(driver, agencyPath, fileName, ".image-section > div > img");
        agency.setLogoUrl(imageUrl);

        agency.setId(agencyId);
        agency.setName(agencyName);
        agency.setPhoneNumbers(phones);
        agency.setEmails(emails);

        List<WebElement> webElements = driver.findElements(By.cssSelector(".card-agent > a"));

        List<String> agentUrls = new ArrayList<String>();
        for (WebElement element : webElements) {
            agentUrls.add(element.getAttribute("href"));
        }

        for (String agentUrl : agentUrls) {
            Agent agent = createNewAgent(agencyPath, agentUrl);
            agency.getAgents().add(agent);
            driver.switchTo().window(lastTab);
        }

        writeAgency(agency, detailsPath);

        driver.close();

        return agency;
    }

    public static Agent createNewAgent(String agencyPath, String agentUrl) {
        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0]);", agentUrl);
        driver.switchTo().window(getLastTabId());
        Agent agent = new Agent();

        List<String> phones = tryReadPhones2();
        if (phones == null) {
            phones = tryReadPhones1();
        }
        agent.setPhoneNumbers(phones);

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

        WebElement webElement = driver.findElement(By.cssSelector(".agency-name > h2"));
        agent.setName(webElement.getText());

        String fileName = "agent_" + agentId + ".jpg";
        String imageUrl = ImageDownloader.downloadImageInElement(driver, agencyPath, fileName, ".image-section > div > img");
        agent.setPhotoUrl(imageUrl);

        driver.close();

        return agent;
    }

    public static void writeAgency(Agency agency, final String path) {
        String json = gson.toJson(agency);
        try {
            FileUtils.writeStringToFile(new File(path), json, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeAdvert(Map<String, String> advert, final String path) {
        String json = gson.toJson(advert);
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
        try {
            WebElement agencyScriptElement = driver.findElement(By.xpath("//script[contains(text(), '" + key + "')]"));
            String scriptContent = agencyScriptElement.getAttribute("innerHTML");
            return scriptContent.split("'" + key+ "': '")[1].split("'")[0];
        } catch (Exception e) {
            return null;
        }

    }

    public static String getLastTabId() {
        ArrayList<String> list = new ArrayList(driver.getWindowHandles());
        return list.get(list.size() - 1);
    }

    private static List<String> tryReadPhones1() {
        try {
            List<WebElement> contactDiv = driver.findElements(By.cssSelector(".btn-contacts > div > a"));

            List<String> phones = new ArrayList<String>();
            for (WebElement element : contactDiv) {
                if (element.getAttribute("onclick") != null && element.getDomAttribute("href").contains("tel")) {
                    phones.add(element.getDomAttribute("href").replace("tel:", ""));
                }
            }
            return phones;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> tryReadPhones2() {
        try {
            List<String> phones = new ArrayList<String>();
            List<WebElement> elements = driver.findElements(By.cssSelector(".btn-contacts .dropdown .dropdown-menu a"));
            for (WebElement phone : elements) {
                phones.add(phone.getText());
            }
            return phones;
        } catch (Exception e) {
            return null;
        }
    }

    private static void readSpanPairs(Map<String, String> keyValues, String cssSelector) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
            for (WebElement element : elements) {
                try {
                    List<WebElement> spans = element.findElements(By.xpath(".//span"));
                    if (spans.size() == 2) {
                        String key = spans.get(0).getText().trim();
                        String value = spans.get(1).getText().trim();
                        keyValues.put(key, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
