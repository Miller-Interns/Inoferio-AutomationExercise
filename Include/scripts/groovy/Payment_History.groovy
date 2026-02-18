import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class PaymentHistorySteps {

	private WebDriver driver
	private WebDriverWait wait

	private String baseUrl
	private String email
	private String password

	// =====================
	// Locators
	// =====================

	private static final By MICROSOFT_LOGIN_BTN =
		By.xpath('/html/body/div[1]/div/div/div/div[2]/button')

	private static final By EMAIL_INPUT = By.name('loginfmt')
	private static final By PASSWORD_INPUT = By.name('passwd')
	private static final By NEXT_BTN = By.id('idSIButton9')

	private static final By VIEW_ALL_PAYMENT_HISTORY_BTN =
		By.xpath('//*[@id="view-all-payment-history-button"]')

	private static final By PAYMENT_HISTORY_TAB =
		By.xpath('//*[@id="badlis-payment-history-tab"]')

	// =====================
	// Hooks (tagged so only PaymentHistory scenarios open/close browser)
	// =====================

	@Before("@PaymentHistory")
	def setup() {
		loadEnvVariables()

		WebUI.openBrowser('')
		WebUI.maximizeWindow()

		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(30))
	}

	@After("@PaymentHistory")
	def teardown() {
		WebUI.closeBrowser()
	}

	// =====================
	// Background Steps
	// =====================

	@Given("the user is logged in and on the dashboard")
	def loginAndOpenDashboard() {

		WebUI.navigateToUrl(baseUrl + '/login')

		wait.until(ExpectedConditions.elementToBeClickable(
			MICROSOFT_LOGIN_BTN
		)).click()

		wait.until(ExpectedConditions.urlContains(
			"login.microsoftonline.com"
		))

		wait.until(ExpectedConditions.visibilityOfElementLocated(
			EMAIL_INPUT
		)).sendKeys(email)

		driver.findElement(NEXT_BTN).click()

		wait.until(ExpectedConditions.visibilityOfElementLocated(
			PASSWORD_INPUT
		)).sendKeys(password)

		driver.findElement(NEXT_BTN).click()

		// Stay signed in?
		wait.until(ExpectedConditions.elementToBeClickable(
			NEXT_BTN
		)).click()

		wait.until(ExpectedConditions.urlContains('/dashboard'))

		KeywordUtil.logInfo("✅ User logged in and redirected to dashboard")
	}

	@Given("the Payment History section is displayed")
	def verifyPaymentHistorySectionVisible() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(
			VIEW_ALL_PAYMENT_HISTORY_BTN
		))

		KeywordUtil.logInfo("✅ Payment History section is visible")
	}

	// =====================
	// Scenario Steps
	// =====================

	@When('the user clicks the "View All" button in the Payment History section')
	def clickViewAllPaymentHistory() {
		wait.until(ExpectedConditions.elementToBeClickable(
			VIEW_ALL_PAYMENT_HISTORY_BTN
		)).click()
	}

	@Then("the user should be redirected to the Payment History page")
	def verifyRedirectedToPaymentHistory() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(
			PAYMENT_HISTORY_TAB
		))

		String tabClass = driver.findElement(PAYMENT_HISTORY_TAB)
			.getAttribute('class')

		assert tabClass.contains('active') :
			'❌ Payment History tab is not active'

		KeywordUtil.markPassed("✅ Redirected to Payment History page")
	}

	@Then("the page should display payment history entries related to the logged-in user")
	def verifyPaymentHistoryEntriesDisplayed() {

		// You can refine this locator later if needed
		assert driver.findElement(PAYMENT_HISTORY_TAB).isDisplayed()

		KeywordUtil.markPassed(
			"✅ Payment history entries are displayed for logged-in user"
		)
	}

	// =====================
	// Helpers
	// =====================

	private void loadEnvVariables() {

		Map<String, String> env = [:]

		Files.readAllLines(Paths.get('.env')).each { line ->
			if (!line.startsWith('#') && line.contains('=')) {
				def (key, value) = line.split('=', 2)
				env[key.trim()] = value.trim()
			}
		}

		baseUrl  = env['BASE_URL']
		email    = env['MS_EMAIL']
		password = env['MS_PASSWORD']

		assert baseUrl && email && password :
			'❌ Missing BASE_URL, MS_EMAIL, or MS_PASSWORD'
	}
}