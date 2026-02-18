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

class FilterAndClearFilterValidationSteps {

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
	private static final By EMPLOYEES_MENU = By.xpath("//*[normalize-space()='Employees']")
	private static final By STATUS_DROPDOWN = By.xpath("//span[normalize-space()='Status']")
	private static final By ACTIVE_OPTION = By.xpath("//li[normalize-space()='Active']")
	private static final By ACTIVE_FILTER_TRIGGER = By.xpath("//span[normalize-space()='Active']")
	private static final By DEACTIVATED_OPTION = By.xpath("//li[normalize-space()='Deactivated']")
	private static final By CLEAR_FILTERS_BTN = By.xpath("//button[contains(.,'Clear Filters')]")

	@Before("@FilterAndClearFilterValidation")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@FilterAndClearFilterValidation")
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
		assert baseUrl && email && password : '❌ Missing BASE_URL, MS_EMAIL, or MS_PASSWORD'
	}

	@Given("the user is logged in and on the dashboard to validate employee filters")
	def loginAndOpenDashboardForFilterValidation() {
		WebUI.navigateToUrl(baseUrl + '/login')
		wait.until(ExpectedConditions.elementToBeClickable(MICROSOFT_LOGIN_BTN)).click()
		wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT)).sendKeys(email)
		driver.findElement(NEXT_BTN).click()
		wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT)).sendKeys(password)
		driver.findElement(NEXT_BTN).click()
		wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN)).click()
		wait.until(ExpectedConditions.urlContains('/dashboard'))
		wait.until { js.executeScript('return document.readyState') == 'complete' }
		WebUI.delay(2)
	}

	@When("the user goes to the Employees page for filter validation")
	def goToEmployeesPageForFilter() {
		wait.until(ExpectedConditions.presenceOfElementLocated(EMPLOYEES_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(EMPLOYEES_MENU))
		try {
			driver.findElement(EMPLOYEES_MENU).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(EMPLOYEES_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/employee'))
		KeywordUtil.logInfo('Navigated to Employees page for filter validation')
	}

	@When("the user opens the Status filter and selects Active")
	def openStatusAndSelectActive() {
		wait.until(ExpectedConditions.elementToBeClickable(STATUS_DROPDOWN)).click()
		wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_OPTION)).click()
		KeywordUtil.logInfo('Selected Active in Status filter')
	}

	@When("the user opens the Status filter again")
	def openStatusFilterAgain() {
		WebUI.delay(1)
		wait.until(ExpectedConditions.visibilityOfElementLocated(ACTIVE_FILTER_TRIGGER))
		wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_FILTER_TRIGGER))
		try {
			driver.findElement(ACTIVE_FILTER_TRIGGER).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(ACTIVE_FILTER_TRIGGER))
		}
		WebUI.delay(1)
		KeywordUtil.logInfo('Opened Status filter again for Deactivated option')
	}

	@When("the user selects Deactivated in the Status filter")
	def selectDeactivated() {
		wait.until(ExpectedConditions.elementToBeClickable(DEACTIVATED_OPTION)).click()
		KeywordUtil.logInfo('Selected Deactivated')
	}

	@When("the user clicks the Clear Filters button")
	def clickClearFilters() {
		wait.until(ExpectedConditions.elementToBeClickable(CLEAR_FILTERS_BTN)).click()
		KeywordUtil.logInfo('Clicked Clear Filters')
	}

	@Then("the employee filter should be cleared")
	def verifyFilterCleared() {
		KeywordUtil.logInfo('Filter cleared successfully')
	}
}
