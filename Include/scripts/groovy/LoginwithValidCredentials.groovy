import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class LoginwithValidCredentials {

    private WebDriver driver
    private WebDriverWait wait

    private String baseUrl
    private String email
    private String password

    // =====================
    // Locators
    // =====================

    private static final By MICROSOFT_LOGIN_BTN =
        By.xpath('/html/body/div[1]/div/div/div/div[2]/button')

    private static final By MS_EMAIL_INPUT = By.name('loginfmt')
    private static final By MS_PASSWORD_INPUT = By.name('passwd')
    private static final By MS_NEXT_BTN = By.id('idSIButton9')

    // =====================
    // Hooks
    // =====================

    @Before("@LoginValid")
    def beforeScenario() {
        loadEnvVariables()

        WebUI.openBrowser('')
        WebUI.maximizeWindow()

        driver = DriverFactory.getWebDriver()
        wait = new WebDriverWait(driver, Duration.ofSeconds(40))
    }

    @After("@LoginValid")
    def afterScenario() {
        WebUI.closeBrowser()
    }

    // =====================
    // Steps
    // =====================

    @Given("the user is on the login page")
    def openLoginPage() {
        WebUI.navigateToUrl(baseUrl + '/login')
        waitForPageReady()
    }

    @When('the user clicks the "Login with Microsoft" button')
    def clickMicrosoftLogin() {
        clickWithFallback(MICROSOFT_LOGIN_BTN)
    }

    @Then("the user is redirected to the Microsoft login page")
    def verifyMicrosoftPage() {
        wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))
    }

    @When("the user enters a valid Microsoft email")
    def enterEmail() {
        typeText(MS_EMAIL_INPUT, email)
    }

    @When('the user clicks the "Next" button')
    def clickNext() {
        clickWithFallback(MS_NEXT_BTN)
    }

    @When("the user enters a valid Microsoft password")
    def enterPassword() {
        typeText(MS_PASSWORD_INPUT, password)
    }

    @When('the user clicks the "Sign in" button')
    def clickSignIn() {
        clickWithFallback(MS_NEXT_BTN)
    }

    @When('the user confirms "Stay signed in"')
    def confirmStaySignedIn() {
        clickWithFallback(MS_NEXT_BTN)
    }

    @Then("the user should be redirected to the dashboard")
    def verifyDashboard() {
        wait.until(ExpectedConditions.urlContains('/dashboard'))
        KeywordUtil.logInfo("✅ Successfully logged in and redirected to dashboard")
    }

    // =====================
    // Helper methods
    // =====================

    private void loadEnvVariables() {
        Map<String, String> env = [:]

        Files.readAllLines(Paths.get('.env')).each { line ->
            if (!line.startsWith('#') && line.contains('=')) {
                def (k, v) = line.split('=', 2)
                env[k.trim()] = v.trim()
            }
        }

        baseUrl  = env['BASE_URL']
        email    = env['MS_EMAIL']
        password = env['MS_PASSWORD']

        assert baseUrl && email && password :
            '❌ Missing required environment variables (BASE_URL, MS_EMAIL, MS_PASSWORD)'
    }

    private void waitForPageReady() {
        wait.until { drv ->
            ((JavascriptExecutor) drv)
                .executeScript("return document.readyState") == "complete"
        }
    }

    private void clickWithFallback(By locator) {
        WebElement element = wait.until(
            ExpectedConditions.presenceOfElementLocated(locator)
        )

        wait.until(ExpectedConditions.visibilityOf(element))

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click()
        } catch (Exception e) {
            KeywordUtil.logInfo("⚠️ Normal click failed, using JS click")
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element)
        }
    }

    private void typeText(By locator, String value) {
        WebElement input = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        )
        input.clear()
        input.sendKeys(value)
    }
}
