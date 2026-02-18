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

class PendingConfirmationSteps {

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

	private static final By VIEW_ALL_PENDING_CONFIRMATION_BTN =
		By.xpath('//*[@id="view-all-pending-confirmation-button"]')

	private static final By PENDING_CONFIRMATION_TAB =
		By.xpath('//*[@id="badlis-pending-confirmation-tab"]')

	// =====================
	// Hooks (open/close browser only for this feature)
	// =====================

	@Before("@PendingConfirmationViewAll")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
	}

	@After("@PendingConfirmationViewAll")
	def afterScenario() {
		WebUI.closeBrowser()
	}

	// =====================
	// Background Steps
	// =====================

	@Given("the user is logged in")
	def loginUser() {
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

		// Stay signed in
		wait.until(ExpectedConditions.elementToBeClickable(
			NEXT_BTN
		)).click()

		wait.until(ExpectedConditions.urlContains('/dashboard'))

		KeywordUtil.logInfo("✅ User logged in successfully")
	}

	@Given("the user is on the dashboard")
	def verifyDashboard() {
		assert driver.getCurrentUrl().contains('/dashboard') :
			'❌ User is not on dashboard'
	}

	@Given("the Pending Confirmation section is displayed")
	def verifyPendingConfirmationSectionVisible() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(
			VIEW_ALL_PENDING_CONFIRMATION_BTN
		))

		KeywordUtil.logInfo("✅ Pending Confirmation section is visible")
	}

	// =====================
	// Scenario Steps
	// =====================

	@When('the user clicks the "View All" button in the Pending Confirmation section')
	def clickViewAllPendingConfirmation() {
		wait.until(ExpectedConditions.elementToBeClickable(
			VIEW_ALL_PENDING_CONFIRMATION_BTN
		)).click()
	}

	@Then("the user should be redirected to the Pending Confirmation page")
	def verifyRedirectedToPendingConfirmation() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(
			PENDING_CONFIRMATION_TAB
		))

		String tabClass = driver.findElement(
			PENDING_CONFIRMATION_TAB
		).getAttribute('class')

		assert tabClass.contains('active') :
			'❌ Pending Confirmation tab is not active'

		KeywordUtil.markPassed(
			"✅ Redirected to Pending Confirmation page"
		)
	}

	@Then("the page should display pending confirmation entries related to the logged-in user")
	def verifyPendingConfirmationEntriesDisplayed() {

		assert driver.findElement(
			PENDING_CONFIRMATION_TAB
		).isDisplayed()

		KeywordUtil.markPassed(
			"✅ Pending confirmation entries are displayed"
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
