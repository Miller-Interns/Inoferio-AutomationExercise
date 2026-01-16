import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

Map<String, String> env = [:]
Files.readAllLines(Paths.get('.env')).each { line ->
    if (!line.startsWith('#') && line.contains('=')) {
        def (key, value) = line.split('=', 2)
        env[key.trim()] = value.trim()
    }
}

String baseUrl = env['BASE_URL']
String validEmail = env['MS_EMAIL']

assert baseUrl && validEmail : "❌ Missing required values in .env"

WebUI.openBrowser('')
WebUI.maximizeWindow()

WebDriver driver = DriverFactory.getWebDriver()
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30))

void openMicrosoftLogin(String baseUrl, WebDriverWait wait) {
    WebUI.navigateToUrl(baseUrl + '/login')

    wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//button")
    )).click()

    wait.until(ExpectedConditions.urlContains("login.microsoftonline.com"))
}

/* =====================================================
   TEST CASE 1: VALID EMAIL + INVALID PASSWORD
===================================================== */

KeywordUtil.logInfo("🧪 Test 1: Valid email + invalid password")

openMicrosoftLogin(baseUrl, wait)

wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("loginfmt")))
    .sendKeys(validEmail)

driver.findElement(By.id("idSIButton9")).click()

wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("passwd")))
    .sendKeys("INVALID_PASS_123")

driver.findElement(By.id("idSIButton9")).click()

assert wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//*[contains(text(),'incorrect')]")
)).isDisplayed()

KeywordUtil.markPassed("✅ Login blocked with invalid password")

/* =====================================================
   TEST CASE 2: INVALID EMAIL
===================================================== */

KeywordUtil.logInfo("🧪 Test 2: Invalid email")

openMicrosoftLogin(baseUrl, wait)

wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("loginfmt")))
    .sendKeys("invalid@email.com")

driver.findElement(By.id("idSIButton9")).click()

WebUI.delay(3)

// Assert user is in Microsoft login
String currentUrl = driver.getCurrentUrl()

assert currentUrl.contains("login.microsoftonline.com") :
    "❌ Unexpected redirect outside Microsoft login"

KeywordUtil.markPassed("✅ Invalid email did not allow app login")


/* =====================================================
   TEST CASE 3: BLANK INPUT
===================================================== */

KeywordUtil.logInfo("🧪 Test 3: Blank input")

openMicrosoftLogin(baseUrl, wait)

driver.findElement(By.id("idSIButton9")).click()

assert wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//*[contains(text(),'enter')]")
)).isDisplayed()

KeywordUtil.markPassed("✅ Blank input validation works")

WebUI.delay(3)
WebUI.closeBrowser()
