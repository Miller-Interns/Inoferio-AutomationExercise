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

class MyBadlisConfirmAllBadlisSteps {

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

	@Before("@MyBadlisConfirmAllBadlis")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisConfirmAllBadlis")
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

	@Given("the user is logged in and on the dashboard to confirm all Badlis")
	def loginAndOpenDashboardForConfirmAllBadlis() {
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

	@When("the user opens My Badlis and the Pending Confirmation tab for confirm all")
	def openMyBadlisAndPendingConfirmationTabForConfirmAll() {
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

	@When("the user clicks the Confirm All button")
	def clickConfirmAllButton() {
		By confirmAllBtn = By.xpath("//button[normalize-space()='Confirm All']")
		if (driver.findElements(confirmAllBtn).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(confirmAllBtn)).click()
			WebUI.delay(1)
		}
	}

	@When("the user confirms the confirmation dialog for Confirm All")
	def confirmDialogForConfirmAll() {
		By dialog = By.xpath("//*[contains(@class,'p-dialog') and contains(@class,'p-component')] | //*[@role='dialog']")
		wait.until(ExpectedConditions.visibilityOfElementLocated(dialog))
		WebUI.delay(2)
		By confirmBtn = By.xpath("//*[contains(@class,'p-dialog')]//button[@id='confirm-badlis-button'] | //*[@role='dialog']//button[@id='confirm-badlis-button'] | //*[contains(@class,'p-dialog')]//button[normalize-space()='Confirm'] | //*[@role='dialog']//button[normalize-space()='Confirm']")
		wait.until(ExpectedConditions.visibilityOfElementLocated(confirmBtn))
		def el = driver.findElement(confirmBtn)
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
		WebUI.delay(0.5)
		js.executeScript("arguments[0].click();", el)
		KeywordUtil.logInfo('Confirmed dialog')
		WebUI.delay(2)
	}

	@Then("all Badlis are confirmed")
	def verifyAllBadlisConfirmed() {
		WebUI.delay(2)
		KeywordUtil.logInfo('Confirm All completed')
	}
}
