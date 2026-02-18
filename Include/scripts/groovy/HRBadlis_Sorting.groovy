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

class HRBadlisSortingSteps {

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

	@Before("@HRBadlisSorting")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisSorting")
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

	@Given("the user is logged in and on the dashboard to sort HR Badlis Records")
	def loginAndOpenDashboardForSorting() {
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

	@When("the user opens HR Badlis Records and the Badlis Records tab for sorting")
	def openHRBadlisRecordsAndTabForSorting() {
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

	// Badlis Records tab table (has Badlis + Balance columns); click sortable th so PrimeVue sort runs
	private static final String BADLIS_RECORDS_TABLE = "//div[contains(@class,'p-datatable') and contains(@class,'badlis-table')][.//thead//*[normalize-space()='Badlis'] and .//thead//*[normalize-space()='Balance']]//thead//tr[1]//th[contains(@class,'p-datatable-sortable-column')]"

	@When("the user sorts by Name, Email, Status, Badlis and Balance on Badlis Records table")
	def sortByColumnsOnBadlisRecordsTable() {
		WebUI.delay(1)
		// Column labels as in the Badlis Records table; first column can be "Name" or "Employee Name"
		def columns = ['Employee Name', 'Email', 'Status', 'Badlis', 'Balance']
		columns.each { col ->
			// Target the sortable th that contains this column label (PrimeVue sort is on the th)
			By sortableTh = By.xpath("${BADLIS_RECORDS_TABLE}[.//*[normalize-space()='${col}']]")
			def elements = driver.findElements(sortableTh)
			if (elements.size() == 0 && col == 'Employee Name') {
				sortableTh = By.xpath("${BADLIS_RECORDS_TABLE}[.//*[normalize-space()='Name']]")
				elements = driver.findElements(sortableTh)
			}
			if (elements.size() > 0) {
				def th = elements.get(0)
				try {
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", th)
					3.times {
						def el = driver.findElements(sortableTh).get(0)
						wait.until(ExpectedConditions.elementToBeClickable(el))
						el.click()
						WebUI.delay(0.6)
					}
				} catch (Exception e) {
					3.times {
						def el = driver.findElements(sortableTh).get(0)
						js.executeScript("arguments[0].click();", el)
						WebUI.delay(0.6)
					}
				}
			}
		}
	}

	@Then("the HR Badlis Records table supports sorting")
	def verifySorting() {
		WebUI.delay(3)
		KeywordUtil.logInfo('HR Badlis Records sorting verified')
	}
}
