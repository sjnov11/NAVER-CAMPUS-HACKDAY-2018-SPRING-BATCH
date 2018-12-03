package com.sjnov11.springbatch.Job;

import com.sjnov11.springbatch.Domain.ImageResource;
import com.sjnov11.springbatch.Domain.Keyword;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CrawlingProcessor implements ItemProcessor<Keyword, ImageResource> {
    private WebDriver webDriver;
    private Map<String, Boolean> checker;
    private static final String webDriverPath = "webdriver/chromedriver.exe";
    private static final String imgPath = "C:\\Users\\u_nov\\Desktop\\img";
    private static final String urlHeader = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=";
    private static final String areaHeader = "div[data-dss-logarea=";

    @Resource
    Environment env;

    public CrawlingProcessor(Map<String, Boolean> checker) {
        this.checker = checker;
        // Initialize Chrome Driver
        System.setProperty("webdriver.chrome.driver",webDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-fullscreen");
        options.addArguments("headless");
        webDriver = new ChromeDriver(options);
    }

    @Override
    public ImageResource process(Keyword keyword) throws Exception {
        ImageResource imageResource = crawling(keyword);

        return imageResource;
    }

    @AfterStep
    private void tearDown() {
        webDriver.quit();
    }

    private ImageResource crawling(Keyword keyword) throws Exception {
        String area = keyword.getArea();
        String query = keyword.getQuery();
        String type = keyword.getType();
        int queryRank = keyword.getQueryRank();
        List<String> imageResourcesPathList = new ArrayList<>();

        String url = urlHeader + query;
        String targetArea = areaHeader + area;
        String fileName = area + "_" + type;

        // Check (area + type) is processed or not
        if (checker.containsKey(fileName))
            return null;

        webDriver.get(url);
        List<WebElement> elems = webDriver.findElements(By.cssSelector(targetArea));

        if (elems.size() == 0) return null;
        checker.put(fileName, Boolean.TRUE);

        // Save screenshots on local and Insert local path to DB
        for (int i = 0; i < elems.size(); i++) {
            String filePath = imgPath + "\\" + fileName + "_" + i + ".png";
            File screenshotLocation = new File(filePath);
            File screenshot = elems.get(i).getScreenshotAs(OutputType.FILE);
            BufferedImage bufferedImage = ImageIO.read(screenshot);
            ImageIO.write(bufferedImage, "png", screenshotLocation);

            imageResourcesPathList.add(filePath);
        }

        ImageResource imageResource = new ImageResource(area, query, type, queryRank, imageResourcesPathList);

        return imageResource;
    }

}
