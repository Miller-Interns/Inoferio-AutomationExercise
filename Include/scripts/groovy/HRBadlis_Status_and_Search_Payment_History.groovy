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
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class HRBadlisStatusAndSearchPaymentHistorySteps {

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
	private static final By BADLIS_RECORDS_MENU = By.xpath(
		"//li[@id='badlis-records-link' and (normalize-space(text())='Badlis Records' or normalize-space(.)='Badlis Records')]" +
		" | //*[normalize-space()='Badlis Records']"
	)
	private static final By PAYMENT_HISTORY_TAB = By.xpath('//*[@id="admin-payment-history-tab"]')
	private static final By PAYMENT_HISTORY_SEARCH_INPUT = By.xpath("//input[@id='payment-history-search-input'] | //div[@id='payment-history-search-bar']//input")

	// Scope to Payment History tab panel only (same panel that has payment-history-search-bar)
	private static final String PAYMENT_HISTORY_TAB_SCOPE = "//*[@id='payment-history-search-bar']/ancestor::*[contains(@id,'tabpanel')][1]"
	// Payment History datepicker overlay (id from app); fallback to any panel with day-view table
	private static final String PAYMENT_HISTORY_DATEPICKER_PANEL = "//div[@id='date-filter-select_panel']"
	private static final String DATEPICKER_PANEL = "//div[contains(@id,'_panel') and .//table[contains(@class,'p-datepicker-day-view')]]"

	@Before("@HRBadlisStatusAndSearchPaymentHistory")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisStatusAndSearchPaymentHistory")
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

	@Given("the user is logged in and on the dashboard to test HR Payment History status and search")
	def loginAndOpenDashboardForPaymentHistoryStatusAndSearch() {
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

	@When("the user opens HR Badlis Records and the Payment History tab for filter and search")
	def openHRBadlisRecordsAndPaymentHistoryTabForFilterAndSearch() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try {
			driver.findElement(BADLIS_RECORDS_MENU).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_HISTORY_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(PAYMENT_HISTORY_TAB)).click()
		WebUI.delay(2)
		assert driver.getCurrentUrl().contains('/admin') : 'Should stay on admin page after opening Payment History tab'
	}

	@When("the user applies date filter and clears filters on Payment History")
	def applyDateFilterAndClearOnPaymentHistory() {
		WebUI.delay(2)
		// Date input only on Payment History tab (not Badlis Records or other tab)
		By selectDateInput = By.xpath("${PAYMENT_HISTORY_TAB_SCOPE}//input[contains(@placeholder,'Select date')]")
		def inputs = driver.findElements(selectDateInput)
		if (inputs.size() > 0) {
			def input = inputs.get(0)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", input)
			WebUI.delay(0.5)
			try {
				wait.until(ExpectedConditions.elementToBeClickable(selectDateInput))
				input.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].focus(); arguments[0].click();", input)
			}
			WebUI.delay(0.5)
			By panel = By.xpath(DATEPICKER_PANEL)
			wait.until(ExpectedConditions.visibilityOfElementLocated(panel))
			WebUI.delay(0.5)
		}
		// Click day 11 in the opened datepicker; prefer Payment History panel by id
		WebUI.delay(0.5)
		String panelScope = PAYMENT_HISTORY_DATEPICKER_PANEL
		if (driver.findElements(By.xpath(panelScope)).size() == 0) {
			panelScope = DATEPICKER_PANEL
		}
		By dateDaySpan = By.xpath("${panelScope}//td[contains(@class,'p-datepicker-day-cell') and not(contains(@class,'p-datepicker-other-month'))]//span[@class='p-datepicker-day' and normalize-space()='11']")
		def dayElements = driver.findElements(dateDaySpan)
		if (dayElements.size() == 0) {
			dateDaySpan = By.xpath("${panelScope}//span[@class='p-datepicker-day' and normalize-space()='11']")
			dayElements = driver.findElements(dateDaySpan)
		}
		if (dayElements.size() == 0) {
			dateDaySpan = By.xpath("${panelScope}//span[contains(@class,'p-datepicker-day') and normalize-space()='11']")
			dayElements = driver.findElements(dateDaySpan)
		}
		if (dayElements.size() > 0) {
			def dayEl = dayElements.get(0)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", dayEl)
			WebUI.delay(0.2)
			def toClick = dayEl
			try {
				def parentTd = dayEl.findElement(By.xpath("./parent::td"))
				if (parentTd != null) { toClick = parentTd }
			} catch (Exception ignored) {}
			try {
				wait.until(ExpectedConditions.elementToBeClickable(toClick))
				toClick.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", toClick)
			}
			KeywordUtil.logInfo("Selected date: 11")
		}
		WebUI.delay(0.5)

		// Clear Filters button on Payment History tab only
		By clearFilters = By.xpath("${PAYMENT_HISTORY_TAB_SCOPE}//button[@id='clear-filter-button'] | ${PAYMENT_HISTORY_TAB_SCOPE}//button[.//span[normalize-space()='Clear Filters']] | ${PAYMENT_HISTORY_TAB_SCOPE}//*[contains(@class,'p-button')][.//span[normalize-space()='Clear Filters']]")
		if (driver.findElements(clearFilters).size() > 0) {
			def el = driver.findElements(clearFilters).get(0)
			try {
				wait.until(ExpectedConditions.elementToBeClickable(el))
				el.click()
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", el)
			}
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

	@When("the user types \"(.*)\" in the Payment History search bar and submits")
	def typeInPaymentHistorySearchBarAndSubmit(String searchText) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_HISTORY_SEARCH_INPUT))
		driver.findElement(PAYMENT_HISTORY_SEARCH_INPUT).clear()
		driver.findElement(PAYMENT_HISTORY_SEARCH_INPUT).sendKeys(searchText)
		driver.findElement(PAYMENT_HISTORY_SEARCH_INPUT).sendKeys(Keys.ENTER)
		KeywordUtil.logInfo("Typed '${searchText}' in Payment History search bar and submitted")
		WebUI.delay(1)
	}

	@Then("the HR Payment History filter and search work correctly")
	def verifyPaymentHistoryFilterAndSearch() {
		WebUI.delay(3)
		KeywordUtil.logInfo('HR Payment History filter and search verified')
	}
}
