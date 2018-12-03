package com.sjnov11.springbatch;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlingTests {
    private WebDriver webDriver;
    private static final String webDriverPath = "webdriver/chromedriver.exe";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver",webDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        //options.addArguments("headless");
        webDriver = new ChromeDriver();
    }
    @After
    public void tearDown() {
        webDriver.quit();
    }

    @Test
    public void crawlerTest() throws Exception {
        webDriver.get("https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=김태희");
        WebElement elem = webDriver.findElement(By.cssSelector("div[data-dss-logarea='x29'"));
        File screenshotLocation = new File("C:\\Users\\u_nov\\Desktop\\result_2.png");

        // Get entire page screenshot
        File screenshot = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);

        // Get the location of element on the page
        Point point = elem.getLocation();

        // Get width and height of the element
        int elemWidth = elem.getSize().getWidth();
        int elemHeight = elem.getSize().getHeight();

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage elemScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
                elemWidth, elemHeight);
        ImageIO.write(elemScreenshot, "png", screenshotLocation);

    }
}
