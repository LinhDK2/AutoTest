package AutoTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AutoTest {
    public WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    @Test
    public void validLogin() {
        driver.get("http://localhost:8080/");

        waitForElement(By.xpath("//a[text()='Form Authentication']")).click();
        waitForElement(By.name("username")).sendKeys("tomsmith");
        waitForElement(By.name("password")).sendKeys("SuperSecretPassword!");
        waitForElement(By.xpath("//button[@type='submit']")).click();

        WebElement successMessage = waitForVisibility(By.id("flash"));
        Assert.assertTrue(successMessage.isDisplayed(), "Login success message is not displayed.");
        Assert.assertTrue(successMessage.getText().contains("You logged into a secure area!"),
                "The success message does not contain expected text.");

        WebElement secureText = waitForVisibility(By.xpath("//h4[text()='Welcome to the Secure Area. When you are done click logout below.']"));
        Assert.assertTrue(secureText.isDisplayed(), "Secure area text is not displayed.");
        Assert.assertEquals(secureText.getText(), "Welcome to the Secure Area. When you are done click logout below.",
                "Secure area description text does not match.");

        WebElement logoutButton = waitForClickable(By.xpath("//a[@href='/logout']"));
        Assert.assertTrue(logoutButton.isDisplayed(), "Logout button is not displayed.");
        Assert.assertEquals(logoutButton.getText(), "Logout", "Logout button text does not match.");
    }

    @Test
    public void invalidLogin() {
        driver.get("http://localhost:8080/");

        waitForElement(By.xpath("//a[text()='Form Authentication']")).click();
        waitForElement(By.name("username")).sendKeys("username");
        waitForElement(By.name("password")).sendKeys("password");
        waitForElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = waitForVisibility(By.id("flash"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message is not displayed.");
        Assert.assertTrue(errorMessage.getText().contains("Your username is invalid!"),
                "Error message does not contain expected text.");
    }

    @Test
    public void checkBox() {
        driver.get("http://localhost:8080/");
        waitForElement(By.xpath("//a[@href='/checkboxes']")).click();

        List<WebElement> checkboxes = driver.findElements(By.xpath("//form[@id='checkboxes']//input[@type='checkbox']"));

        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                checkbox.click();
                Assert.assertFalse(checkbox.isSelected(), "Checkbox vẫn đang được chọn sau khi bỏ check!");
            }

            checkbox.click();
            Assert.assertTrue(checkbox.isSelected(), "Checkbox không được chọn sau khi click!");
        }
    }

    @Test
    public void dropdown() {
        driver.get("http://localhost:8080/");
        waitForElement(By.xpath("//a[@href='/dropdown']")).click();

        WebElement dropdownElement = waitForVisibility(By.id("dropdown"));
        List<WebElement> options = dropdownElement.findElements(By.tagName("option"));

        for (WebElement option : options) {
            String optionText = option.getText();
            option.click();
            System.out.println("Đã chọn tùy chọn: " + optionText);
            WebElement selectedOption = dropdownElement.findElement(By.cssSelector("option:checked"));
            Assert.assertEquals(selectedOption.getText(), optionText, "Tùy chọn không khớp!");
        }
    }

    @Test
    public void fileUpload() {
        driver.get("http://localhost:8080/");
        waitForElement(By.xpath("//a[@href='/upload']")).click();

        WebElement uploadInput = waitForVisibility(By.id("file-upload"));
        String filePath = "/Users/macbook/Downloads/Hinh-Nen-Meo-Ngao-38.jpg";
        uploadInput.sendKeys(filePath);

        waitForElement(By.id("file-submit")).click();

        WebElement uploadMessage = waitForVisibility(By.id("uploaded-files"));
        Assert.assertTrue(uploadMessage.isDisplayed(), "File upload failed.");
        Assert.assertEquals(uploadMessage.getText(), "Hinh-Nen-Meo-Ngao-38.jpg", "Uploaded file name doesn't match.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
