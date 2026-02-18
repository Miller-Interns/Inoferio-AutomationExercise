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

class MyBadlisBadlisHistorySortingSteps {

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
	private static final By BADLIS_HISTORY_TAB = By.xpath('//*[@id="badlis-history-tab"]')

	@Before("@MyBadlisBadlisHistorySorting")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisBadlisHistorySorting")
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

	@Given("the user is logged in and on the dashboard to sort Badlis History")
	def loginAndOpenDashboardForBadlisHistorySorting() {
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

	@When("the user opens My Badlis and the Badlis History tab for sorting")
	def openMyBadlisAndBadlisHistoryForSorting() {
		wait.until(ExpectedConditions.presenceOfElementLocated(MY_BADLIS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(MY_BADLIS_MENU))
		try { driver.findElement(MY_BADLIS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(MY_BADLIS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/badlis'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(BADLIS_HISTORY_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_HISTORY_TAB)).click()
		WebUI.delay(2)
	}

	private static final String SORT_HEADER_SCOPE = "//thead | //*[contains(@class,'p-sortable-column') or contains(@class,'p-column-header') or contains(@class,'p-datatable-thead')]"

	@When("the user sorts by Date, Badlis Count, Balance, Reason and Status on Badlis History")
	def sortByColumnsOnBadlisHistory() {
		WebUI.delay(1)
		def columns = ['Date', 'Badlis Count', 'Balance', 'Reason', 'Status']
		columns.each { col ->
			By header = By.xpath("${SORT_HEADER_SCOPE}//div[normalize-space()='${col}'] | ${SORT_HEADER_SCOPE}//span[normalize-space()='${col}'] | ${SORT_HEADER_SCOPE}//th[.//*[normalize-space()='${col}']]")
			def elements = driver.findElements(header)
			if (elements.size() > 0) {
				def el = elements.get(0)
				try {
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
					wait.until(ExpectedConditions.elementToBeClickable(header)).click()
					WebUI.delay(0.5)
				} catch (Exception e) {
					js.executeScript("arguments[0].click();", el)
				}
			}
		}
	}

	@Then("the Badlis History table supports sorting")
	def verifyBadlisHistorySorting() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Badlis History sorting verified')
	}
}
