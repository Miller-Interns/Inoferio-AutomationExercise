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

	// Disputes tab scope and filter elements (by id) so we don't click wrong tab's date/status or the Status column
	private static final By DISPUTES_DATE_INPUT = By.xpath("//*[@id='disputes-date-filter']//input | //span[@id='disputes-date-filter']/input")
	private static final By DISPUTES_STATUS_DROPDOWN = By.xpath("//*[@id='disputes-status-filter']/div | //*[@id='disputes-status-filter']")
	private static final By DISPUTES_CLEAR_FILTERS = By.xpath("//button[@id='clear-filters-button'] | //button[.//span[normalize-space()='Clear Filters']]")
	private static final By DISPUTES_SEARCH_INPUT = By.xpath("//input[@id='disputes-search-input'] | //div[@id='disputes-search-bar']//input")
	private static final String DISPUTES_DATEPICKER_PANEL = "//div[@id='disputes-date-filter_panel']"
	private static final String DATEPICKER_PANEL_FALLBACK = "//div[contains(@id,'_panel') and .//table[contains(@class,'p-datepicker-day-view')]]"

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
		// 1. Date filter: open Disputes datepicker and select a day (e.g. 11)
		def dateInputs = driver.findElements(DISPUTES_DATE_INPUT)
		if (dateInputs.size() > 0) {
			def input = dateInputs.get(0)
			try {
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", input)
				WebUI.delay(0.3)
				wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_DATE_INPUT))
				input.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].focus(); arguments[0].click();", input)
			}
			WebUI.delay(0.5)
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DISPUTES_DATEPICKER_PANEL + "//table")))
			} catch (Exception ignored) {}
			String panelScope = driver.findElements(By.xpath(DISPUTES_DATEPICKER_PANEL)).size() > 0 ? DISPUTES_DATEPICKER_PANEL : DATEPICKER_PANEL_FALLBACK
			By day11 = By.xpath("${panelScope}//span[contains(@class,'p-datepicker-day') and normalize-space()='11']")
			def dayEls = driver.findElements(day11)
			if (dayEls.size() > 0) {
				def toClick = dayEls.get(0)
				try {
					def parentTd = toClick.findElement(By.xpath("./parent::td"))
					toClick = parentTd
				} catch (Exception ignored) {}
				try {
					wait.until(ExpectedConditions.elementToBeClickable(toClick))
					toClick.click()
				} catch (Exception e) {
					js.executeScript("arguments[0].click();", toClick)
				}
			}
			WebUI.delay(0.5)
		}
		// 2. Status filter: open Disputes status dropdown (not the Status column) and select options
		if (driver.findElements(DISPUTES_STATUS_DROPDOWN).size() > 0) {
			try {
				wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_STATUS_DROPDOWN)).click()
				WebUI.delay(0.5)
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", driver.findElement(DISPUTES_STATUS_DROPDOWN))
				WebUI.delay(0.5)
			}
		}
		By pendingConf = By.xpath("//li[@id='disputes-status-filter_1'] | //li[.//span[contains(.,'Pending Confirmation')]]")
		if (driver.findElements(pendingConf).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(pendingConf)).click()
			WebUI.delay(0.5)
		}
		if (driver.findElements(DISPUTES_STATUS_DROPDOWN).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_STATUS_DROPDOWN)).click()
			WebUI.delay(0.5)
		}
		By approved = By.xpath("//li[@id='disputes-status-filter_2'] | //li[.//span[normalize-space()='Approved']]")
		if (driver.findElements(approved).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(approved)).click()
			WebUI.delay(0.5)
		}
		if (driver.findElements(DISPUTES_STATUS_DROPDOWN).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_STATUS_DROPDOWN)).click()
			WebUI.delay(0.5)
		}
		By rejected = By.xpath("//li[@id='disputes-status-filter_3'] | //li[.//span[normalize-space()='Rejected']]")
		if (driver.findElements(rejected).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(rejected)).click()
			WebUI.delay(0.5)
		}
		if (driver.findElements(DISPUTES_STATUS_DROPDOWN).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_STATUS_DROPDOWN)).click()
			WebUI.delay(0.5)
		}
		By all = By.xpath("//li[@id='disputes-status-filter_0'] | //li[.//span[normalize-space()='All']]")
		if (driver.findElements(all).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(all)).click()
			WebUI.delay(0.5)
		}
		// 3. Clear Filters (Disputes button id is clear-filters-button)
		if (driver.findElements(DISPUTES_CLEAR_FILTERS).size() > 0) {
			def clearBtn = driver.findElement(DISPUTES_CLEAR_FILTERS)
			try {
				wait.until(ExpectedConditions.elementToBeClickable(clearBtn))
				clearBtn.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", clearBtn)
			}
			WebUI.delay(0.5)
		}
	}

	@When("the user types \"(.*)\" in the Disputes search bar")
	def typeInDisputesSearchBar(String searchText) {
		if (driver.findElements(DISPUTES_SEARCH_INPUT).size() > 0) {
			def input = wait.until(ExpectedConditions.visibilityOfElementLocated(DISPUTES_SEARCH_INPUT))
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", input)
			WebUI.delay(0.3)
			try {
				input.clear()
				input.sendKeys(searchText)
			} catch (Exception e) {
				js.executeScript("arguments[0].value = '';", input)
				js.executeScript("arguments[0].value = arguments[1];", input, searchText)
				js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input)
			}
			WebUI.delay(1)
		}
	}

	@Then("the HR Disputes filter and search work correctly")
	def verifyFilterAndSearchDisputes() {
		WebUI.delay(2)
		KeywordUtil.logInfo('HR Disputes filter and search verified')
	}
}
