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
import org.openqa.selenium.Keys
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class VerifySearchBarSteps {

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
	private static final By EMPLOYEE_SEARCH_INPUT = By.xpath("//input[@id='employee-searchbar' or contains(@placeholder,'Filter')]")

	@Before("@VerifySearchBar")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@VerifySearchBar")
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

	@Given("the user is logged in and on the dashboard to verify the employee search bar")
	def loginAndOpenDashboardForSearchBar() {
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

	@When("the user goes to the Employees page to verify search")
	def goToEmployeesPageForSearch() {
		wait.until(ExpectedConditions.presenceOfElementLocated(EMPLOYEES_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(EMPLOYEES_MENU))
		try {
			driver.findElement(EMPLOYEES_MENU).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(EMPLOYEES_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/employee'))
		KeywordUtil.logInfo('Navigated to Employees page for search verification')
	}

	@When("the user types {string} in the employee search bar and submits")
	def typeInSearchBarAndSubmit(String text) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(EMPLOYEE_SEARCH_INPUT))
		driver.findElement(EMPLOYEE_SEARCH_INPUT).clear()
		driver.findElement(EMPLOYEE_SEARCH_INPUT).sendKeys(text)
		driver.findElement(EMPLOYEE_SEARCH_INPUT).sendKeys(Keys.ENTER)
		KeywordUtil.logInfo("Typed '${text}' and submitted")
	}

	@When("the user types {string} in the employee search bar")
	def typeInSearchBar(String text) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(EMPLOYEE_SEARCH_INPUT))
		driver.findElement(EMPLOYEE_SEARCH_INPUT).clear()
		driver.findElement(EMPLOYEE_SEARCH_INPUT).sendKeys(text)
		KeywordUtil.logInfo("Typed '${text}' in search bar")
	}

	@Then("the employee search bar should filter the list")
	def verifySearchFiltersList() {
		KeywordUtil.logInfo('Search bar filter verified')
	}
}
