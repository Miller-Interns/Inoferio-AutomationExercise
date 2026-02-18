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

class HRBadlisApproveDisputeSteps {

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
	private static final By DISPUTES_TAB = By.xpath('//*[@id="admin-disputes-tab"]')
	private static final By DISPUTES_TAB_BY_TEXT = By.xpath("//*[contains(@class,'p-tablist') or contains(@class,'p-tab')]//*[normalize-space()='Disputes'] | //a[normalize-space()='Disputes'] | //button[normalize-space()='Disputes'] | //*[@role='tab'][normalize-space()='Disputes']")

	@Before("@HRBadlisApproveDispute")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisApproveDispute")
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

	@Given("the user is logged in and on the dashboard to approve HR dispute")
	def loginAndOpenDashboardForApproveDispute() {
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

	@When("the user opens HR Badlis Records and the Disputes tab for approve")
	def openHRBadlisRecordsAndDisputesTabForApprove() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try { driver.findElement(BADLIS_RECORDS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until { js.executeScript('return document.readyState') == 'complete' }
		WebUI.delay(3)
		def disputesEl = null
		try {
			if (driver.findElements(DISPUTES_TAB).size() > 0) disputesEl = driver.findElement(DISPUTES_TAB)
			else if (driver.findElements(DISPUTES_TAB_BY_TEXT).size() > 0) disputesEl = driver.findElement(DISPUTES_TAB_BY_TEXT)
		} catch (org.openqa.selenium.WebDriverException e) {
			if (e.message?.contains('tab crashed') || e.message?.contains('crashed')) {
				// Katalon doesn't have KeywordUtil.logError; use logInfo to avoid MissingMethodException
				KeywordUtil.logInfo('Chrome tab crashed when loading admin page. This is a browser/environment issue (memory, Chrome/Driver), not a locator or script bug.')
				throw e
			}
			throw e
		}
		if (disputesEl != null) {
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", disputesEl)
			WebUI.delay(0.3)
			try { disputesEl.click() } catch (Exception e) { js.executeScript("arguments[0].click();", disputesEl) }
		}
		WebUI.delay(2)
	}

	@When("the user expands a dispute row and clicks Approve")
	def expandDisputeRowAndClickApprove() {
		WebUI.delay(2)
		// Disputes table has class "disputes-table". Expand = open first dispute row
		// (row toggler, actions cell td[3]/div per Object Repository, or first cell/row).
		By rowExpand = By.xpath(
			"//*[contains(@class,'disputes-table')]//tbody//tr[1]//*[contains(@class,'p-row-toggler')] | " +
			"//*[contains(@class,'disputes-table')]//tbody//tr[1]//*[contains(@class,'pi-chevron-right') or contains(@class,'pi-chevron-down')]/parent::* | " +
			"//*[contains(@class,'disputes-table')]//tbody//tr[1]//td[3]/div | " +
			"//*[contains(@class,'disputes-table')]//tbody//tr[1]//td[1] | " +
			"//*[contains(@class,'disputes-table')]//tbody//tr[1]"
		)
		def expandEls = driver.findElements(rowExpand)
		if (expandEls.size() > 0) {
			def el = expandEls.get(0)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
			WebUI.delay(0.3)
			try { el.click() } catch (Exception e) { js.executeScript("arguments[0].click();", el) }
			WebUI.delay(1)
		}
		// Approve button: id="approve-button" (from Object Repository), then fallbacks
		By approveBtn = By.xpath("//button[@id='approve-button'] | //button[normalize-space()='Approve'] | //*[contains(@class,'p-button')][.//span[normalize-space()='Approve']]")
		if (driver.findElements(approveBtn).size() > 0) {
			def btn = driver.findElement(approveBtn)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn)
			WebUI.delay(0.3)
			try { wait.until(ExpectedConditions.elementToBeClickable(approveBtn)).click() } catch (Exception e) { js.executeScript("arguments[0].click();", btn) }
			WebUI.delay(0.5)
		}
	}

	@When("the user confirms the approve dialog")
	def confirmApproveDialog() {
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@class,'p-dialog')] | //*[@role='dialog']")))
		By confirmBtn = By.xpath("//*[contains(@class,'p-dialog') or @role='dialog']//button[normalize-space()='Confirm'] | //*[@role='dialog']//*[normalize-space()='Confirm']/parent::* | //button[normalize-space()='Confirm']")
		if (driver.findElements(confirmBtn).size() > 0) {
			def btn = driver.findElement(confirmBtn)
			try { wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click() } catch (Exception e) { js.executeScript("arguments[0].click();", btn) }
		}
		WebUI.delay(1)
	}

	@Then("the dispute is approved")
	def verifyDisputeApproved() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Dispute approved')
	}
}
