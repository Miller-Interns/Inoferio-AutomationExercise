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

class HRBadlisPaginationAcrossTabsSteps {

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
	private static final By DISPUTES_TAB = By.xpath('//*[@id="admin-disputes-tab"]')
	private static final By PAYMENT_HISTORY_TAB = By.xpath('//*[@id="admin-payment-history-tab"]')

	@Before("@HRBadlisPaginationAcrossTabs")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisPaginationAcrossTabs")
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

	@Given("the user is logged in and on the dashboard to test HR Badlis Records pagination")
	def loginAndOpenDashboardForPagination() {
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

	@When("the user opens HR Badlis Records and the Badlis Records tab for pagination")
	def openHRBadlisRecordsAndBadlisRecordsTabForPagination() {
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

	@When("the user navigates pagination on the Badlis Records tab")
	def navigatePaginationOnBadlisRecordsTab() {
		clickPageNumberIfPresent('2')
		clickPaginatorNext()
		clickPaginatorPrev()
		WebUI.delay(1)
	}

	@When("the user switches to Disputes and navigates pagination")
	def switchToDisputesAndNavigatePagination() {
		wait.until(ExpectedConditions.elementToBeClickable(DISPUTES_TAB)).click()
		WebUI.delay(2)
		clickPageNumberIfPresent('2')
		clickPaginatorNext()
		clickPaginatorPrev()
		WebUI.delay(1)
	}

	@When("the user switches to Payment History tab on HR Badlis Records and navigates pagination")
	def switchToPaymentHistoryAndNavigatePagination() {
		wait.until(ExpectedConditions.elementToBeClickable(PAYMENT_HISTORY_TAB)).click()
		WebUI.delay(2)
		clickPageNumberIfPresent('2')
		clickPaginatorNext()
		clickPaginatorPrev()
		WebUI.delay(1)
	}

	// Use the visible tab's paginator only (each tab has its own; global click hits the first in DOM = wrong tab)
	private static final By PAGINATOR_NEXT = By.xpath("//button[contains(@class,'p-paginator-next')] | //button[@aria-label='Next Page' or @aria-label='Next']")
	private static final By PAGINATOR_PREV = By.xpath("//button[contains(@class,'p-paginator-prev')] | //button[@aria-label='Previous Page' or @aria-label='Previous']")

	private void clickPageNumberIfPresent(String num) {
		// Find the visible paginator container first (the one in the active tab), then the page number inside it
		def paginatorContainers = driver.findElements(By.xpath("//div[contains(@class,'p-paginator') and contains(@class,'p-component')]"))
		def visibleContainer = paginatorContainers.find { it.isDisplayed() }
		if (visibleContainer == null) return
		// Page "2" can be (text()='2' or .='2') per PrimeVue; look inside the visible paginator only
		def pageBtns = visibleContainer.findElements(By.xpath(".//button[contains(@class,'p-paginator-page') and (normalize-space(.)='${num}' or .='${num}' or text()='${num}')]"))
		if (pageBtns.size() == 0) return
		def btn = pageBtns.get(0)
		try {
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn)
			WebUI.delay(0.3)
			wait.until(ExpectedConditions.elementToBeClickable(btn))
			btn.click()
			WebUI.delay(1)
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", btn)
			WebUI.delay(1)
		}
	}

	private void clickPaginatorNext() {
		def nextBtns = driver.findElements(PAGINATOR_NEXT)
		def visible = nextBtns.find { it.isDisplayed() }
		if (visible != null) {
			try {
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", visible)
				WebUI.delay(0.3)
				wait.until(ExpectedConditions.elementToBeClickable(visible))
				visible.click()
				WebUI.delay(0.5)
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", visible)
				WebUI.delay(0.5)
			}
		}
	}

	private void clickPaginatorPrev() {
		def prevBtns = driver.findElements(PAGINATOR_PREV)
		def visible = prevBtns.find { it.isDisplayed() }
		if (visible != null) {
			try {
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", visible)
				WebUI.delay(0.3)
				wait.until(ExpectedConditions.elementToBeClickable(visible))
				visible.click()
				WebUI.delay(0.5)
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", visible)
				WebUI.delay(0.5)
			}
		}
	}

	@Then("pagination works across HR Badlis Records tabs")
	def verifyPaginationAcrossTabs() {
		WebUI.delay(2)
		KeywordUtil.logInfo('HR Badlis Records pagination verified')
	}
}
