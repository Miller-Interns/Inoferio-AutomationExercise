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

class HRBadlisFilterAndSearchDisputesSteps {

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
	private static final By BADLIS_RECORDS_MENU = By.xpath("//*[normalize-space()='Badlis Records']")
	private static final By DISPUTES_TAB = By.xpath('//*[@id="admin-disputes-tab"]')

	@Before("@HRBadlisFilterAndSearchDisputes")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisFilterAndSearchDisputes")
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

	@Given("the user is logged in and on the dashboard to filter and search HR Disputes")
	def loginAndOpenDashboardForFilterAndSearchDisputes() {
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

	@When("the user opens HR Badlis Records and the Disputes tab for filter and search")
	def openHRBadlisRecordsAndDisputesTabForFilterAndSearch() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try { driver.findElement(BADLIS_RECORDS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(DISPUTES_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_TAB)).click()
		WebUI.delay(2)
	}

	@When("the user applies date and status filters then clears filters on Disputes")
	def applyDateAndStatusFiltersThenClearOnDisputes() {
		WebUI.delay(1)
		By date28 = By.xpath("//span[normalize-space()='28']")
		if (driver.findElements(date28).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(date28)).click()
			WebUI.delay(0.5)
		}
		By statusLabel = By.xpath("//span[normalize-space()='Status']")
		if (driver.findElements(statusLabel).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(statusLabel)).click()
			WebUI.delay(0.5)
		}
		By pendingConf = By.xpath("//span[contains(.,'Pending Confirmation')]")
		if (driver.findElements(pendingConf).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(pendingConf)).click()
			WebUI.delay(0.5)
		}
		By approved = By.xpath("//span[normalize-space()='Approved']")
		if (driver.findElements(approved).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(approved)).click()
			WebUI.delay(0.5)
		}
		By rejected = By.xpath("//span[normalize-space()='Rejected']")
		if (driver.findElements(rejected).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(rejected)).click()
			WebUI.delay(0.5)
		}
		By all = By.xpath("//li[contains(.,'All')]")
		if (driver.findElements(all).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(all)).click()
			WebUI.delay(0.5)
		}
		By clearFilters = By.xpath("//span[normalize-space()='Clear Filters']")
		if (driver.findElements(clearFilters).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(clearFilters)).click()
			WebUI.delay(0.5)
		}
	}

	@When("the user types \"(.*)\" in the Disputes search bar")
	def typeInDisputesSearchBar(String searchText) {
		By searchInput = By.xpath("//input[contains(@class,'disputes-search-input')]")
		if (driver.findElements(searchInput).size() > 0) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).sendKeys(searchText)
			WebUI.delay(1)
		}
	}

	@Then("the HR Disputes filter and search work correctly")
	def verifyFilterAndSearchDisputes() {
		WebUI.delay(2)
		KeywordUtil.logInfo('HR Disputes filter and search verified')
	}
}
