package readinglist;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebIntegrationTest(randomPort=true)
public class ServerWebTests {
    private static ChromeDriver browser;
    @Value("${local.server.port}")
    private int port;

    //@BeforeClass
    public static void openBrowser() {
        String chromebin = "C:/xxx/chrome.exe";//chrome启动文件路径
        String chromedriver = "E:/xxx/chromedriver.exe";//chromedriver文件路径
        System.setProperty("webdriver.chrome.bin", chromebin);
        System.setProperty("webdriver.chrome.driver", chromedriver);

        browser = new ChromeDriver();
        browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    //@AfterClass
    public static void closeBrowser() {
        browser.quit();
    }

    //@Test
    public void addBookToEmptyList() {
        
        String baseUrl = "http://localhost:" + port;
        browser.get(baseUrl);
        assertEquals("You have no books in your book list",browser.findElementByTagName("div").getText());
        browser.findElementByName("title").sendKeys("BOOK TITLE");
        browser.findElementByName("author").sendKeys("BOOK AUTHOR");
        browser.findElementByName("isbn").sendKeys("1234567890");
        browser.findElementByName("description").sendKeys("DESCRIPTION");
        browser.findElementByTagName("form").submit();
        
        WebElement dl = browser.findElementByCssSelector("dt.bookHeadline");
        assertEquals("BOOK TITLE by BOOK AUTHOR (ISBN: 1234567890)",dl.getText());
        WebElement dt = browser.findElementByCssSelector("dd.bookDescription");
        assertEquals("DESCRIPTION", dt.getText());
    }
}