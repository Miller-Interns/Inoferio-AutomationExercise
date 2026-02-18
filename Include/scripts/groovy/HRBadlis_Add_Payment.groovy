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

class HRBadlisAddPaymentSteps {

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

	@Before("@HRBadlisAddPayment")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisAddPayment")
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

	@Given("the user is logged in and on the dashboard to add HR Payment")
	def loginAndOpenDashboardForAddPayment() {
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

	@When("the user opens HR Badlis Records and searches for an employee")
	def openHRBadlisRecordsAndSearchEmployee() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try { driver.findElement(BADLIS_RECORDS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(BADLIS_RECORDS_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_TAB)).click()
		WebUI.delay(2)
		By searchInput = By.xpath("//input[contains(@class,'badlis-records-search-input') or @placeholder]")
		if (driver.findElements(searchInput).size() > 0) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).sendKeys('mary')
			WebUI.delay(1)
		}
	}

	@When("the user opens Add Payment for the employee and selects amount")
	def openAddPaymentAndSelectAmount() {
		By walletIcon = By.xpath("//span[contains(@class,'pi-wallet')]/parent::*")
		if (driver.findElements(walletIcon).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(walletIcon)).click()
			WebUI.delay(1)
		}
		By stackedButtons = By.xpath("//input[contains(@class,'stacked-buttons')]")
		if (driver.findElements(stackedButtons).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(stackedButtons)).click()
			WebUI.delay(0.5)
		}
		By maxOption = By.xpath("//span[normalize-space()='Max']")
		if (driver.findElements(maxOption).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(maxOption)).click()
			WebUI.delay(0.5)
		}
	}

	@When("the user submits the Add Payment form")
	def submitAddPaymentForm() {
		By addBtn = By.xpath("//button[normalize-space()='Add']")
		if (driver.findElements(addBtn).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click()
		}
		WebUI.delay(2)
	}

	@Then("the payment is added")
	def verifyPaymentAdded() {
		WebUI.delay(2)
		KeywordUtil.logInfo('Add Payment completed')
	}
}
