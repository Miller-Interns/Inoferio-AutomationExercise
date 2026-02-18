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

class LoginWithInvalidCredentials {

    private WebDriver driver
    private WebDriverWait wait

    private String baseUrl
    private String validEmail

    // =====================
    // Locators
    // =====================

    private static final By MICROSOFT_LOGIN_BTN =
        By.xpath('/html/body/div[1]/div/div/div/div[2]/button')

    private static final By EMAIL_INPUT = By.name('loginfmt')
    private static final By PASSWORD_INPUT = By.name('passwd')
    private static final By NEXT_BTN = By.id('idSIButton9')

    private static final By INCORRECT_PASSWORD_ERROR =
        By.xpath("//*[contains(text(),'incorrect')]")

    private static final By BLANK_EMAIL_ERROR =
        By.xpath("//*[contains(text(),'Enter a valid email address')]")

    // =====================
    // Hooks
    // =====================

    @Before("@LoginInvalid")
    def beforeScenario() {
        loadEnvVariables()

        WebUI.openBrowser('')
        WebUI.maximizeWindow()

        driver = DriverFactory.getWebDriver()
        wait = new WebDriverWait(driver, Duration.ofSeconds(40))
    }

    @After("@LoginInvalid")
    def afterScenario() {
        WebUI.closeBrowser()
    }

    // =====================
    // Background
    // =====================

    @Given("the user is on the application login page")
    def openAppLoginPage() {
        WebUI.navigateToUrl(baseUrl + '/login')
        waitForPageReady()
    }

    @Given("the user opens the Microsoft login page")
    def openMicrosoftLogin() {
        clickWithFallback(MICROSOFT_LOGIN_BTN)
        wait.until(ExpectedConditions.urlContains("login.microsoftonline.com"))
    }

    // =====================
    // Steps
    // =====================

    @When("the user clicks the Next button")
    def clickNextButton() {
        clickWithFallback(NEXT_BTN)
    }

    @When("the user clicks the Next button without entering email")
    def clickNextWithoutEmail() {
        clickWithFallback(NEXT_BTN)
    }

    // 🔹 RENAMED STEP (Option 2)
    @When("the user enters a valid Microsoft email for invalid login")
    def enterValidEmailForInvalidLogin() {
        typeText(EMAIL_INPUT, validEmail)
    }

    @When("the user enters an invalid Microsoft email")
    def enterInvalidEmail() {
        typeText(EMAIL_INPUT, "invalid@email.com")
    }

    @When("the user enters an invalid Microsoft password")
    def enterInvalidPassword() {
        typeText(PASSWORD_INPUT, "INVALID_PASS_123")
    }

    @When("the user submits the login form")
    def submitLogin() {
        clickWithFallback(NEXT_BTN)
    }

    // =====================
    // Assertions
    // =====================

    @Then("an incorrect password error message should be displayed")
    def verifyIncorrectPasswordError() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            INCORRECT_PASSWORD_ERROR
        ))
        KeywordUtil.markPassed("✅ Login blocked due to invalid password")
    }

    @Then("the user should remain on the Microsoft login page")
    def verifyStillOnMicrosoftLogin() {
        assert driver.getCurrentUrl().contains("login.microsoftonline.com")
        KeywordUtil.markPassed("✅ Invalid email did not allow login")
    }

    @Then("a blank email validation message should be displayed")
    def verifyBlankEmailValidation() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            BLANK_EMAIL_ERROR
        ))
        KeywordUtil.markPassed("✅ Blank email validation works")
    }

    // =====================
    // Helpers
    // =====================

    private void loadEnvVariables() {
        Map<String, String> env = [:]

        Files.readAllLines(Paths.get('.env')).each { line ->
            if (!line.startsWith('#') && line.contains('=')) {
                def (k, v) = line.split('=', 2)
                env[k.trim()] = v.trim()
            }
        }

        baseUrl    = env['BASE_URL']
        validEmail = env['MS_EMAIL']

        assert baseUrl && validEmail :
            '❌ Missing BASE_URL or MS_EMAIL in .env'
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

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click()
        } catch (Exception e) {
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
