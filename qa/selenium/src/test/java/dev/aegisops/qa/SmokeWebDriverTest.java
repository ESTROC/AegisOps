package dev.aegisops.qa;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

class SmokeWebDriverTest {
 @Test void loginAndOpenTicketDialog() {
  String user = System.getenv("TEST_ADMIN_USERNAME");
  String pass = System.getenv("TEST_ADMIN_PASSWORD");
  if (user == null || pass == null) throw new IllegalStateException("Set synthetic TEST_ADMIN_* credentials");
  ChromeOptions options = new ChromeOptions();
  options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
  WebDriver driver = new ChromeDriver(options);
  try {
   driver.get(System.getenv().getOrDefault("APP_BASE_URL", "http://127.0.0.1:8088"));
   driver.findElement(By.cssSelector("input[autocomplete='username']")).clear();
   driver.findElement(By.cssSelector("input[autocomplete='username']")).sendKeys(user);
   driver.findElement(By.cssSelector("input[autocomplete='current-password']")).sendKeys(pass);
   driver.findElement(By.cssSelector("form button.primary")).click();
   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
   wait.until(d -> d.findElements(By.cssSelector("button.create-btn")).size() > 0);
   driver.findElement(By.cssSelector("button.create-btn")).click();
   wait.until(d -> d.findElements(By.cssSelector("[role='dialog'][aria-label='Create ticket']")).size() > 0);
   assertTrue(driver.findElement(By.cssSelector("[role='dialog']")).isDisplayed());
  } finally { driver.quit(); }
 }
}
