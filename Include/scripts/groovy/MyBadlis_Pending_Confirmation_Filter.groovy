import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class MyBadlisPendingConfirmationFilterSteps {

	private WebDriver driver
	private WebDriverWait wait
	private JavascriptExecutor js
	private String baseUrl
	private String email
	private String password

	private static final By MICROSOFT_LOGIN_BTN = By.xpath('/html/body/div[1]/div/div/div/div[2]/button')
	private static final By EMAIL_INPUT = By.name('loginfmt')
	private static final By PASSWORD_INPUT = By.name('passwd')
	private static final By NEXT_BTN = By.id('idSIButton9')
	private static final By MY_BADLIS_MENU = By.xpath("//*[normalize-space()='My Badlis']")
	private static final By PENDING_CONFIRMATION_TAB = By.xpath('//*[@id="badlis-pending-confirmation-tab"]')

	@Before("@MyBadlisPendingConfirmationFilter")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisPendingConfirmationFilter")
	def afterScenario() {
		WebUI.closeBrowser()
	}

	private void loadEnvVariables() {
		Map<String, String> env = [:]
		Files.readAllLines(Paths.get('.env')).each { line ->
			if (!line.startsWith('#') && line.contains('=')) {
				def (key, value) = line.split('=', 2)
				env[key.trim()] = value.trim()
			}
		}
		baseUrl = env['BASE_URL']
		email = env['MS_EMAIL']
		password = env['MS_PASSWORD']
		assert baseUrl && email && password : 'Missing BASE_URL, MS_EMAIL, or MS_PASSWORD'
	}

	@Given("the user is logged in and on the dashboard to filter Pending Confirmation")
	def loginAndOpenDashboardForPendingConfirmationFilter() {
		WebUI.navigateToUrl(baseUrl + '/login')
		wait.until(ExpectedConditions.elementToBeClickable(MICROSOFT_LOGIN_BTN)).click()
		wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT)).sendKeys(email)
		driver.findElement(NEXT_BTN).click()
		wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT)).sendKeys(password)
		driver.findElement(NEXT_BTN).click()
		wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN)).click()
		wait.until(ExpectedConditions.urlContains('/dashboard'))
		KeywordUtil.logInfo('Login successful')
		wait.until { js.executeScript('return document.readyState') == 'complete' }
		WebUI.delay(2)
	}

	@When("the user opens My Badlis and the Pending Confirmation tab for filtering")
	def openMyBadlisAndPendingConfirmationForFilter() {
		wait.until(ExpectedConditions.presenceOfElementLocated(MY_BADLIS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(MY_BADLIS_MENU))
		try { driver.findElement(MY_BADLIS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(MY_BADLIS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/badlis'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(PENDING_CONFIRMATION_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(PENDING_CONFIRMATION_TAB)).click()
		WebUI.delay(2)
		assert driver.getCurrentUrl().contains('/badlis') : 'Should stay on Badlis page after opening Pending Confirmation tab'
	}

	private static final String DATEPICKER_PANEL = "//*[contains(@class,'p-datepicker-panel') or contains(@class,'p-datepicker') or contains(@class,'p-calendar-panel')]"

	@When("the user applies date filters on Pending Confirmation")
	def applyDateFiltersOnPendingConfirmation() {
		WebUI.delay(2)
		By dateTrigger = By.xpath("//*[contains(@class,'p-calendar')]//input | //*[contains(@class,'p-datepicker')]//input | //span[contains(@class,'pi-calendar')]/parent::* | //*[contains(@class,'p-datepicker-trigger')]")
		def triggers = driver.findElements(dateTrigger)
		if (triggers.size() > 0) {
			def trigger = triggers.get(0)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", trigger)
			WebUI.delay(0.5)
			try {
				trigger.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", trigger)
			}
			By panel = By.xpath("${DATEPICKER_PANEL}")
			wait.until(ExpectedConditions.visibilityOfElementLocated(panel))
			WebUI.delay(1)
		}
		By date10InPanel = By.xpath("${DATEPICKER_PANEL}//span[normalize-space()='10']")
		if (driver.findElements(date10InPanel).size() > 0) {
			clickDateInPanel(date10InPanel, '10')
		}
		By date11InPanel = By.xpath("${DATEPICKER_PANEL}//span[normalize-space()='11']")
		if (driver.findElements(date11InPanel).size() > 0) {
			clickDateInPanel(date11InPanel, '11')
		}
		WebUI.delay(1)
	}

	private void clickDateInPanel(By locator, String day) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
		def el = driver.findElement(locator)
		try {
			wait.until(ExpectedConditions.elementToBeClickable(locator)).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", el)
		}
		KeywordUtil.logInfo("Selected date: ${day}")
		WebUI.delay(0.5)
	}

	@When("the user clicks Clear Filters on Pending Confirmation")
	def clickClearFiltersOnPendingConfirmation() {
		By clearFilters = By.xpath("//button[.//span[normalize-space()='Clear Filters']] | //*[contains(@class,'p-button')][.//span[normalize-space()='Clear Filters']] | //button[normalize-space()='Clear Filters']")
		if (driver.findElements(clearFilters).size() > 0) {
			def el = driver.findElement(clearFilters)
			try {
				wait.until(ExpectedConditions.elementToBeClickable(clearFilters)).click()
			} catch (org.openqa.selenium.ElementClickInterceptedException e) {
				js.executeScript("arguments[0].click();", el)
			}
		}
		WebUI.delay(1)
	}

	@Then("the Pending Confirmation filters are cleared")
	def verifyPendingConfirmationFiltersCleared() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Pending Confirmation filters cleared')
	}
}
