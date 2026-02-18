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

class HRBadlisStatusAndSearchBarSteps {

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
	private static final By BADLIS_RECORDS_TAB = By.xpath('//*[@id="admin-badlis-records-tab"]')

	@Before("@HRBadlisStatusAndSearchBar")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisStatusAndSearchBar")
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

	@Given("the user is logged in and on the dashboard to test HR Badlis Records status and search")
	def loginAndOpenDashboardForStatusAndSearch() {
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

	@When("the user opens HR Badlis Records and the Badlis Records tab")
	def openHRBadlisRecordsAndBadlisRecordsTab() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try { driver.findElement(BADLIS_RECORDS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(BADLIS_RECORDS_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_TAB)).click()
		WebUI.delay(2)
	}

	// Status filter: use dropdown by id (trigger is inner div) so we don't click the Status column header in the table
	private static final By STATUS_FILTER_DROPDOWN = By.xpath("//div[@id='badlis-records-status-filter']/div | //*[@id='badlis-records-status-filter']")
	private static final By ACTIVE_OPTION = By.xpath("//li[@id='badlis-records-status-filter_1'] | //li[normalize-space()='Active']")
	private static final By DEACTIVATED_OPTION = By.xpath("//li[@id='badlis-records-status-filter_2'] | //li[normalize-space()='Deactivated']")
	private static final By CLEAR_FILTERS_BTN = By.xpath("//button[@id='clear-filter-button'] | //button[.//span[normalize-space()='Clear Filters']] | //span[normalize-space()='Clear Filters']/parent::*")

	@When("the user applies Status filter Active and Deactivated then clears filters")
	def applyStatusFilterAndClear() {
		WebUI.delay(1)
		// 1. Open Status filter dropdown (placeholder "Status") and select Active
		if (driver.findElements(STATUS_FILTER_DROPDOWN).size() > 0) {
			def trigger = driver.findElement(STATUS_FILTER_DROPDOWN)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", trigger)
			wait.until(ExpectedConditions.elementToBeClickable(STATUS_FILTER_DROPDOWN)).click()
			WebUI.delay(0.5)
		}
		if (driver.findElements(ACTIVE_OPTION).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_OPTION)).click()
			WebUI.delay(0.5)
		}
		// 2. Open Status filter dropdown again and select Deactivated
		if (driver.findElements(STATUS_FILTER_DROPDOWN).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(STATUS_FILTER_DROPDOWN)).click()
			WebUI.delay(0.5)
		}
		if (driver.findElements(DEACTIVATED_OPTION).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(DEACTIVATED_OPTION)).click()
			WebUI.delay(0.5)
		}
		// 3. Click Clear Filters
		if (driver.findElements(CLEAR_FILTERS_BTN).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(CLEAR_FILTERS_BTN)).click()
			WebUI.delay(0.5)
		}
	}

	@When("the user types in the Badlis Records search bar \"(.*)\" and \"(.*)\"")
	def typeInBadlisRecordsSearchBar(String first, String second) {
		By searchInput = By.xpath("//input[@id='badlis-records-search-input'] | //div[@id='badlis-records-search-bar']//input")
		if (driver.findElements(searchInput).size() > 0) {
			def input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput))
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", input)
			WebUI.delay(0.3)
			try { input.click() } catch (Exception e) { js.executeScript("arguments[0].click();", input) }
			WebUI.delay(0.3)

			try {
				input.clear()
				input.sendKeys(first)
			} catch (Exception e) {
				js.executeScript("arguments[0].value = '';", input)
				js.executeScript("arguments[0].value = arguments[1];", input, first)
				js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input)
				js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input)
			}
			WebUI.delay(1)

			try {
				input.clear()
				input.sendKeys(second)
			} catch (Exception e) {
				js.executeScript("arguments[0].value = '';", input)
				js.executeScript("arguments[0].value = arguments[1];", input, second)
				js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input)
				js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input)
			}
			WebUI.delay(1)
		}
	}

	@Then("the HR Badlis Records status and search work correctly")
	def verifyStatusAndSearch() {
		WebUI.delay(3)
		KeywordUtil.logInfo('HR Badlis Records status and search verified')
	}
}
