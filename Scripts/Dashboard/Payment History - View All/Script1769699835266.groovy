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

String email = env['MS_EMAIL']

String password = env['MS_PASSWORD']

assert ((baseUrl != null) && (email != null)) && (password != null) : 'Missing required environment variables'

WebUI.openBrowser('')

WebUI.maximizeWindow()

WebUI.navigateToUrl(baseUrl + '/login')

WebDriver driver = DriverFactory.getWebDriver()

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30))

// Microsoft login button
By microsoftLoginBtn = By.xpath('/html/body/div[1]/div/div/div/div[2]/button')

wait.until(ExpectedConditions.elementToBeClickable(microsoftLoginBtn)).click()

// Microsoft login page
wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))

// Email
wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('loginfmt'))).sendKeys(email)

driver.findElement(By.id('idSIButton9')).click()

// Password
wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('passwd'))).sendKeys(password)

driver.findElement(By.id('idSIButton9')).click()

// Stay signed in?
wait.until(ExpectedConditions.elementToBeClickable(By.id('idSIButton9'))).click()

wait.until(ExpectedConditions.urlContains('/dashboard'))

KeywordUtil.logInfo('Redirected to dashboard successfully')

String currentUrl = driver.getCurrentUrl()

assert currentUrl.contains('/dashboard') : 'Failed to redirect to dashboard after login'

WebUI.delay(5)
// Click "Create Post" button
By payhistBtn = By.xpath('//*[@id="view-all-payment-history-button"]')

wait.until(ExpectedConditions.elementToBeClickable(payhistBtn)).click()

By payhisttab = By.xpath('//*[@id="badlis-payment-history-tab"]')
 
 wait.until(ExpectedConditions.visibilityOfElementLocated(payhisttab))
 
 String tabClass = driver.findElement(payhisttab)
	 .getAttribute('class')
 
 assert tabClass.contains('active') : '❌ Payment History tab is not active'

WebUI.delay(3)
WebUI.closeBrowser()
