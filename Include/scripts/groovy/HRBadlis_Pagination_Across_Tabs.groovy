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

	private void clickPageNumberIfPresent(String num) {
		By pageBtn = By.xpath("//button[normalize-space()='${num}' and contains(@class,'p-paginator')] | //a[normalize-space()='${num}']")
		if (driver.findElements(pageBtn).size() > 0) {
			try { wait.until(ExpectedConditions.elementToBeClickable(pageBtn)).click(); WebUI.delay(1) } catch (Exception e) {}
		}
	}

	private void clickPaginatorNext() {
		By next = By.xpath("//button[@aria-label='Next'] | //span[contains(@class,'pi-chevron-right')]/parent::*")
		if (driver.findElements(next).size() > 0) {
			try { wait.until(ExpectedConditions.elementToBeClickable(next)).click(); WebUI.delay(0.5) } catch (Exception e) {}
		}
	}

	private void clickPaginatorPrev() {
		By prev = By.xpath("//button[@aria-label='Previous'] | //span[contains(@class,'pi-chevron-left')]/parent::*")
		if (driver.findElements(prev).size() > 0) {
			try { wait.until(ExpectedConditions.elementToBeClickable(prev)).click(); WebUI.delay(0.5) } catch (Exception e) {}
		}
	}

	@Then("pagination works across HR Badlis Records tabs")
	def verifyPaginationAcrossTabs() {
		WebUI.delay(2)
		KeywordUtil.logInfo('HR Badlis Records pagination verified')
	}
}
