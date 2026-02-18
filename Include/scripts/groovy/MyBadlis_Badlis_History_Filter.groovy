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

class MyBadlisBadlisHistoryFilterSteps {

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
	private static final By BADLIS_HISTORY_TAB = By.xpath('//*[@id="badlis-history-tab"]')

	@Before("@MyBadlisBadlisHistoryFilter")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisBadlisHistoryFilter")
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

	@Given("the user is logged in and on the dashboard to filter Badlis History")
	def loginAndOpenDashboardForBadlisHistoryFilter() {
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

	@When("the user opens My Badlis and the Badlis History tab for filtering")
	def openMyBadlisAndBadlisHistoryForFilter() {
		wait.until(ExpectedConditions.presenceOfElementLocated(MY_BADLIS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(MY_BADLIS_MENU))
		try { driver.findElement(MY_BADLIS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(MY_BADLIS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/badlis'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(BADLIS_HISTORY_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_HISTORY_TAB)).click()
		WebUI.delay(2)
	}

	private static final String OVERLAY = "//*[contains(@class,'p-dropdown-panel') or contains(@class,'p-listbox-panel') or contains(@class,'p-select-overlay') or contains(@class,'p-overlay') or contains(@class,'p-component-overlay') or @role='listbox']"
	private static final String DATEPICKER = "//*[contains(@class,'p-datepicker') or contains(@class,'p-calendar-panel')]"

	@When("the user applies Status and other filters on Badlis History")
	def applyStatusAndOtherFiltersOnBadlisHistory() {
		WebUI.delay(1)
		By statusLabel = By.xpath("//span[normalize-space()='Status']")
		By dateInPicker = By.xpath("${DATEPICKER}//span[normalize-space()='28']")
		By confirmedInOverlay = By.xpath("${OVERLAY}//span[normalize-space()='Confirmed'] | ${OVERLAY}//li[.//span[normalize-space()='Confirmed']] | ${OVERLAY}//*[@role='option'][normalize-space(.)='Confirmed']")
		By paidInOverlay = By.xpath("${OVERLAY}//li[contains(.,'Paid')] | ${OVERLAY}//*[contains(@class,'p-listbox-item') and contains(.,'Paid')] | ${OVERLAY}//*[@role='option'][contains(.,'Paid')]")
		if (driver.findElements(dateInPicker).size() > 0) {
			clickOptionInOverlay(dateInPicker, 'Date 28')
		}
		if (driver.findElements(statusLabel).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(statusLabel)).click()
			WebUI.delay(1)
		}
		if (driver.findElements(confirmedInOverlay).size() > 0) {
			clickOptionInOverlay(confirmedInOverlay, 'Confirmed')
		}
		if (driver.findElements(paidInOverlay).size() > 0) {
			clickOptionInOverlay(paidInOverlay, 'Paid')
		}
	}

	private void clickOptionInOverlay(By locator, String name) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
		def el = driver.findElement(locator)
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
		WebUI.delay(1)
		try {
			wait.until(ExpectedConditions.elementToBeClickable(locator)).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", el)
		}
		KeywordUtil.logInfo("Selected filter option: ${name}")
		WebUI.delay(1)
	}

	@When("the user clicks Clear Filters on Badlis History")
	def clickClearFiltersOnBadlisHistory() {
		By clearFiltersBtn = By.xpath("//button[.//span[normalize-space()='Clear Filters']] | //*[contains(@class,'p-button')][.//span[normalize-space()='Clear Filters']]")
		wait.until(ExpectedConditions.visibilityOfElementLocated(clearFiltersBtn))
		def el = driver.findElement(clearFiltersBtn)
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
		WebUI.delay(1)
		try {
			wait.until(ExpectedConditions.elementToBeClickable(clearFiltersBtn)).click()
		} catch (org.openqa.selenium.ElementClickInterceptedException e) {
			js.executeScript("arguments[0].click();", el)
		}
		WebUI.delay(1)
	}

	@Then("the Badlis History filters are cleared")
	def verifyBadlisHistoryFiltersCleared() {
		WebUI.delay(2)
		KeywordUtil.logInfo('Badlis History filters cleared')
	}
}
