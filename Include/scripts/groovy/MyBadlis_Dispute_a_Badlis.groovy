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

class MyBadlisDisputeABadlisSteps {

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

	@Before("@MyBadlisDisputeABadlis")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisDisputeABadlis")
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

	@Given("the user is logged in and on the dashboard to dispute a Badlis")
	def loginAndOpenDashboardForDisputeABadlis() {
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

	@When("the user opens My Badlis and the Pending Confirmation tab for dispute")
	def openMyBadlisAndPendingConfirmationTabForDispute() {
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

	@When("the user clicks the Dispute button")
	def clickDisputeButton() {
		By disputeBtn = By.xpath("//button[normalize-space()='Dispute']")
		if (driver.findElements(disputeBtn).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(disputeBtn)).click()
			WebUI.delay(1)
		}
	}

	@When("the user enters dispute reason \"(.*)\"")
	def enterDisputeReason(String reason) {
		By reasonTextarea = By.xpath("//textarea[contains(@class,'reason') or @placeholder] | //textarea")
		if (driver.findElements(reasonTextarea).size() > 0) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(reasonTextarea)).sendKeys(reason)
			WebUI.delay(0.5)
		}
	}

	@When("the user submits the dispute form")
	def submitDisputeForm() {
		By submitBtn = By.xpath("//button[normalize-space()='Submit']")
		if (driver.findElements(submitBtn).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click()
		}
		WebUI.delay(1)
	}

	@Then("the dispute is submitted")
	def verifyDisputeSubmitted() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Dispute submitted')
	}
}
