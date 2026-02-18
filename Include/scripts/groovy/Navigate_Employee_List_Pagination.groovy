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

class NavigateEmployeeListPaginationSteps {

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
	// Pagination: use aria-label and p-paginator (PrimeVue); absolute paths break when DOM changes
	private static final By SECOND_PAGE_BTN = By.xpath("//*[contains(@class,'p-paginator')]//button[normalize-space(.)='2'] | //nav//button[normalize-space(.)='2']")
	private static final By NEXT_PAGE_BTN = By.xpath("//button[@aria-label='Next Page'] | //button[@aria-label='Next'] | //*[contains(@class,'p-paginator')]//span[contains(@class,'pi-chevron-right')]/parent::*")
	private static final By LAST_PAGE_BTN = By.xpath("//button[@aria-label='Last Page'] | //button[@aria-label='Last'] | //*[contains(@class,'p-paginator')]//span[contains(@class,'pi-angle-double-right')]/parent::*")
	private static final By PREV_PAGE_BTN = By.xpath("//button[@aria-label='Previous Page'] | //button[@aria-label='Previous'] | //*[contains(@class,'p-paginator')]//span[contains(@class,'pi-chevron-left')]/parent::*")
	private static final By FIRST_PAGE_BTN = By.xpath("//button[@aria-label='First Page'] | //button[@aria-label='First'] | //*[contains(@class,'p-paginator')]//span[contains(@class,'pi-angle-double-left')]/parent::*")
	private static final By CURRENT_PAGE_NUMBER = By.xpath("//*[contains(@class,'p-paginator')]//*[contains(@class,'p-highlight')] | //nav//button[contains(@class,'p-highlight')] | //*[contains(@class,'p-paginator')]//button[normalize-space(.)='1']")

	@Before("@NavigateEmployeeListPagination")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@NavigateEmployeeListPagination")
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

	@Given("the user is logged in and on the dashboard to test employee pagination")
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
		KeywordUtil.logInfo('Login successful, dashboard loaded')
		wait.until { js.executeScript('return document.readyState') == 'complete' }
		WebUI.delay(2)
	}

	@When("the user goes to the Employees page for pagination")
	def goToEmployeesPageForPagination() {
		wait.until(ExpectedConditions.presenceOfElementLocated(EMPLOYEES_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(EMPLOYEES_MENU))
		try {
			driver.findElement(EMPLOYEES_MENU).click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(EMPLOYEES_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/employee'))
		KeywordUtil.logInfo('Successfully navigated to Employees page')
	}

	private void clickAndWait(By locator, String name) {
		wait.until(ExpectedConditions.elementToBeClickable(locator))
		driver.findElement(locator).click()
		KeywordUtil.logInfo("Clicked: ${name}")
		WebUI.delay(2)
	}

	@When("the user goes to the second page")
	def goToSecondPage() { clickAndWait(SECOND_PAGE_BTN, 'Second Page') }

	@When("the user clicks the Next page button")
	def clickNextPage() { clickAndWait(NEXT_PAGE_BTN, 'Next Page') }

	@When("the user goes to the last page")
	def goToLastPage() { clickAndWait(LAST_PAGE_BTN, 'Last Page') }

	@When("the user clicks the Previous page button")
	def clickPrevPage() { clickAndWait(PREV_PAGE_BTN, 'Previous Page') }

	@When("the user goes to the first page")
	def goToFirstPage() { clickAndWait(FIRST_PAGE_BTN, 'First Page') }

	@Then("the user should be on page one of the employee list")
	def verifyOnPageOne() {
		String pageNumText = wait.until(ExpectedConditions.visibilityOfElementLocated(CURRENT_PAGE_NUMBER)).getText()
		assert pageNumText.trim() == '1' : "Expected page 1, found: ${pageNumText}"
		KeywordUtil.logInfo('Verified current page is 1')
		WebUI.delay(2)
	}
}
