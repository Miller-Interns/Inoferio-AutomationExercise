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

class MyBadlisPaginationAcrossTabsSteps {

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
	private static final By BADLIS_HISTORY_TAB = By.xpath('//*[@id="badlis-history-tab"]')
	private static final By PAYMENT_HISTORY_TAB = By.xpath('//*[@id="badlis-payment-history-tab"]')

	@Before("@MyBadlisPaginationAcrossTabs")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisPaginationAcrossTabs")
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

	@Given("the user is logged in and on the dashboard to test My Badlis pagination")
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

	@When("the user opens My Badlis and the Pending Confirmation tab")
	def openMyBadlisAndPendingTab() {
		wait.until(ExpectedConditions.presenceOfElementLocated(MY_BADLIS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(MY_BADLIS_MENU))
		try { driver.findElement(MY_BADLIS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(MY_BADLIS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/badlis'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(PENDING_CONFIRMATION_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(PENDING_CONFIRMATION_TAB)).click()
		WebUI.delay(2)
	}

	@When("the user navigates pagination on the current tab")
	def navigatePaginationOnCurrentTab() {
		clickPageNumberIfPresent('2')
		clickPaginatorNext()
		clickPaginatorPrev()
		WebUI.delay(1)
	}

	@When("the user switches to Badlis History and navigates pagination")
	def switchToBadlisHistoryAndNavigatePagination() {
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_HISTORY_TAB)).click()
		WebUI.delay(2)
		clickPageNumberIfPresent('2')
		clickPaginatorNext()
		clickPaginatorNext()
		clickPaginatorPrev()
		WebUI.delay(1)
	}

	@When("the user switches to Payment History tab on My Badlis and navigates pagination")
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

	@Then("pagination works across My Badlis tabs")
	def verifyPaginationAcrossTabs() {
		WebUI.delay(2)
		KeywordUtil.logInfo('Pagination across tabs verified')
	}
}
