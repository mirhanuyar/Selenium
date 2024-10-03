package homele2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;
import java.util.Set;

public class ImageDownloader {
    public static void downloadSliderImages(WebDriver driver, String sliderClass, String downloadBasePath, WebDriverWait wait, String ilanTitle) {
        Set<String> downloadedImages = new HashSet<>();
        boolean hasNextImage = true;
        int imageCount = 1;

        while (hasNextImage) {
            try {
                WebElement currentImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("." + sliderClass + " img")));
                String imageUrl = currentImage.getAttribute("src");

                if (downloadedImages.contains(imageUrl)) {
                    hasNextImage = false;
                    System.out.println("Son fotoğrafa ulaşıldı.");
                    break;
                }

                downloadedImages.add(imageUrl);
                String fileName = "image_" + imageCount + ".jpg";
                downloadImage(imageUrl, downloadBasePath, fileName);
                imageCount++;

                WebElement nextButton = driver.findElement(By.cssSelector(".slick-next"));
                wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
                Thread.sleep(3000);

            } catch (Exception e) {
                System.out.println("Fotoğraf bulunamadı: " + e.getMessage());
                hasNextImage = false;
            }
        }
    }

    public static void downloadImage(String imageUrl, String downloadPath, String fileName) {
        Json.downloadImage(imageUrl, downloadPath, fileName);
    }
}
