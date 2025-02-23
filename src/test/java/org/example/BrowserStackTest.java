package org.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Set;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.qameta.allure.Step;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.example.Config;
import io.appium.java_client.MobileBy;

import static org.testng.Assert.assertEquals;

public class BrowserStackTest {

  private AndroidDriver driver;

  @AndroidFindBy( id = "activity_main_title")
  WebElement mainScreenTitle;

  @AndroidFindBy( id = "activity_main_webview_sample_button")
  WebElement webView;

  @FindBy(xpath = "//android.view.View[@content-desc=\"UI Automator\"]")
  WebElement uiAutomator;

  @FindBy(xpath = "//android.view.View[@content-desc=\"Espresso\"]/android.widget.TextView")
  WebElement espresso;

  @FindBy(xpath = "//android.view.View[@content-desc=\"Synchronization capabilities\"]/android.widget.TextView")
  WebElement targetAudienceBtn;

  @BeforeMethod
  public void beforeTest() throws MalformedURLException {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    HashMap<String, Object> bstackOptions = new HashMap<>();

    // Учетные данные BrowserStack
    bstackOptions.put("userName", Config.getValue("loginBrowserStack"));
    bstackOptions.put("accessKey", Config.getValue("keyBrowserStack"));

    // Параметры BrowserStack
    bstackOptions.put("appiumVersion", "1.20.2");
    bstackOptions.put("projectName", "BrowserStack_example");
    bstackOptions.put("buildName", "Java Android");
    bstackOptions.put("sessionName", "first_test");
    bstackOptions.put("debug", "true");
    bstackOptions.put("video", "false");
    bstackOptions.put("appiumLogs", "false");

    // Параметры устройства и приложения
    capabilities.setCapability("platformName", "android");
    capabilities.setCapability("appium:platformVersion", "13.0");
    capabilities.setCapability("appium:deviceName", "Samsung Galaxy S23 Ultra");
    capabilities.setCapability("appium:app", "bs://9a779874bfe244df3d37e2f0ee70d341243b6d75");

    // Параметры Appium
    capabilities.setCapability("chromedriverExecutable", "/path/to/chromedriver");
    capabilities.setCapability("appium:automationName", "UIAutomator2");
    capabilities.setCapability("appium:language", "en");
    capabilities.setCapability("appium:locale", "ru");

    // Троттлинг сети: эмуляция 3G
    bstackOptions.put("networkProfile", "3g-good"); // Используем профиль "3g-good"

    // Ориентация устройства: альбомный режим
    bstackOptions.put("deviceOrientation", "landscape");

    // Добавляем bstackOptions в capabilities
    capabilities.setCapability("bstack:options", bstackOptions);



    // Инициализация драйвера
    driver = new AndroidDriver(new URL("http://hub-cloud.browserstack.com/wd/hub"), capabilities);

    // Инициализация PageFactory
    PageFactory.initElements(new AppiumFieldDecorator(driver), this);
  }

  @AfterMethod
  public void afterTest(ITestResult result) {
    updateTestBrowserStack(result);
    driver.quit();
  }
    
  @Test
  public void sampleAppTest() {

      System.out.println("Тест запущен!");

    // Ожидание появления главного экрана
    getWait10();

    // Инициализация элементов PageFactory в нативном контексте
    PageFactory.initElements(new AppiumFieldDecorator(driver), this);

    // Проверка, что заголовок отображается
    mainScreenTitle.isDisplayed();
    Assert.assertTrue(mainScreenTitle.isDisplayed());

    webView.isDisplayed();
    webView.click();

    espresso.isDisplayed();
    espresso.click();


    targetAudienceBtn.isDisplayed();
    targetAudienceBtn.click();

    driver.navigate().back();
    driver.navigate().back();

    uiAutomator.isDisplayed();
    uiAutomator.click();

    swipeDown(3);

  }

  public void updateTestBrowserStack(ITestResult result) {
    String testName = result.getMethod().getMethodName();
    driver.executeScript("browserstack_executor: {\"action\": \"setSessionName\", \"arguments\": {\"name\":\"" + testName + "\" }}");

    if (!result.isSuccess()) {
      driver.executeScript("browserstack_executor: " +
              "{\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + result.getThrowable().getLocalizedMessage() + "\"}}");
    } else {
      driver.executeScript("browserstack_executor:" +
              " {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"\"}}");
    }
  }

  public WebDriverWait getWait10() {
    return new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void switchToNativeContext() {
    driver.context("NATIVE_APP");
    System.out.println("Переключились на NATIVE_APP");
  }

  public void switchToWebViewContext() {
    // Ожидание появления WEBVIEW_chrome
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // Переключаемся на WEBVIEW_chrome
    driver.context("WEBVIEW_chrome");
    System.out.println("Переключились на WEBVIEW_chrome");
  }

  public void swipeDown(int times) {
    // Получаем размеры экрана
    Dimension size = driver.manage().window().getSize();

    // Вычисляем координаты для свайпа
    int startX = size.width / 2; // Центр экрана по горизонтали
    int startY = (int) (size.height * 0.8); // Начальная точка свайпа (80% от высоты экрана)
    int endY = (int) (size.height * 0.2); // Конечная точка свайпа (20% от высоты экрана)

    // Создаем объект TouchAction
    TouchAction action = new TouchAction(driver);

    // Выполняем свайп указанное количество раз
    for (int i = 0; i < times; i++) {
      action.press(PointOption.point(startX, startY)) // Нажимаем на начальную точку
              .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500))) // Ждем 500 мс
              .moveTo(PointOption.point(startX, endY)) // Перемещаемся к конечной точке
              .release() // Отпускаем
              .perform(); // Выполняем действие
    }
  }


}
