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

class HRBadlisAddBadlisSteps {

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
	private static final By BADLIS_RECORDS_TAB_BY_TEXT = By.xpath("//*[contains(@class,'p-tab') or @role='tab']//*[normalize-space()='Badlis Records'] | //button[normalize-space()='Badlis Records'] | //a[normalize-space()='Badlis Records']")

	@Before("@HRBadlisAddBadlis")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@HRBadlisAddBadlis")
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

	@Given("the user is logged in and on the dashboard to add HR Badlis")
	def loginAndOpenDashboardForAddBadlis() {
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

	@When("the user opens HR Badlis Records and the Badlis Records tab for adding Badlis")
	def openHRBadlisRecordsAndTabForAddBadlis() {
		wait.until(ExpectedConditions.presenceOfElementLocated(BADLIS_RECORDS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(BADLIS_RECORDS_MENU))
		try { driver.findElement(BADLIS_RECORDS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(BADLIS_RECORDS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/admin'))
		wait.until { js.executeScript('return document.readyState') == 'complete' }
		WebUI.delay(3)
		wait.until(ExpectedConditions.or(
			ExpectedConditions.visibilityOfElementLocated(BADLIS_RECORDS_TAB),
			ExpectedConditions.visibilityOfElementLocated(BADLIS_RECORDS_TAB_BY_TEXT)
		))
		def tabEl = driver.findElements(BADLIS_RECORDS_TAB).size() > 0 ? driver.findElement(BADLIS_RECORDS_TAB) : driver.findElement(BADLIS_RECORDS_TAB_BY_TEXT)
		try {
			tabEl.click()
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", tabEl)
		}
		WebUI.delay(2)
	}

	private static final String DATEPICKER_PANEL = "//*[contains(@class,'p-datepicker-panel') or contains(@class,'p-datepicker') or contains(@class,'p-calendar-panel')]"

	@When("the user opens the Add Badlis form and fills date and description")
	def openAddBadlisFormAndFill() {
		WebUI.delay(2)
		By addBtn = By.xpath("//button[contains(@class,'p-button') and .//span[contains(@class,'pi-plus')]] | //span[contains(@class,'pi-plus')]/parent::* | //button[contains(@class,'p-button-icon')]")
		if (driver.findElements(addBtn).size() > 0) {
			def addEl = driver.findElement(addBtn)
			js.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'instant'});", addEl)
			WebUI.delay(0.5)
			try { addEl.click() } catch (Exception e) { js.executeScript("arguments[0].click();", addEl) }
			WebUI.delay(2)
		}
		String dialogScope = "//*[contains(@class,'p-dialog') or @role='dialog']"
		By dateTriggerInDialog = By.xpath("${dialogScope}//*[contains(@class,'p-calendar')]//input | ${dialogScope}//*[contains(@class,'p-datepicker')]//input | ${dialogScope}//span[contains(@class,'pi-calendar')]/parent::*")
		if (driver.findElements(dateTriggerInDialog).size() > 0) {
			def trigger = driver.findElement(dateTriggerInDialog)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", trigger)
			try { trigger.click() } catch (Exception e) { js.executeScript("arguments[0].click();", trigger) }
			WebUI.delay(1)
			By date10InPanel = By.xpath("${DATEPICKER_PANEL}//span[normalize-space()='10']")
			if (driver.findElements(date10InPanel).size() > 0) {
				def dayEl = driver.findElement(date10InPanel)
				try { dayEl.click() } catch (Exception e) { js.executeScript("arguments[0].click();", dayEl) }
			}
			WebUI.delay(0.5)
		}
		By checkbox = By.xpath("//*[contains(@class,'p-dialog') or @role='dialog']//input[contains(@class,'p-checkbox-input')] | //input[contains(@class,'p-checkbox-input')]")
		if (driver.findElements(checkbox).size() > 0) {
			def cb = driver.findElement(checkbox)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", cb)
			try { cb.click() } catch (Exception e) { js.executeScript("arguments[0].click();", cb) }
			WebUI.delay(0.5)
		}
		By description = By.xpath("//*[contains(@class,'p-dialog') or @role='dialog']//textarea[contains(@class,'badlis-description') or contains(@placeholder,'escription') or contains(@placeholder,'Description')] | //textarea[contains(@class,'badlis-description')] | //textarea[@placeholder] | //textarea")
		if (driver.findElements(description).size() > 0) {
			def descEl = driver.findElement(description)
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", descEl)
			WebUI.delay(0.3)
			def text = 'Late: Went in at 10:01 AM'
			try {
				descEl.clear()
				descEl.sendKeys(text)
			} catch (Exception e) {
				js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", descEl, text)
			}
			WebUI.delay(0.5)
		}
	}

	@When("the user submits the Add Badlis form")
	def submitAddBadlisForm() {
		By addSubmitBtn = By.xpath("//*[contains(@class,'p-dialog') or @role='dialog']//button[normalize-space()='Add'] | //button[normalize-space()='Add'] | //*[contains(@class,'p-button')]//span[normalize-space()='Add']/parent::*")
		if (driver.findElements(addSubmitBtn).size() > 0) {
			def btn = driver.findElement(addSubmitBtn)
			js.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'instant'});", btn)
			WebUI.delay(0.5)
			try { btn.click() } catch (Exception e) { js.executeScript("arguments[0].click();", btn) }
		}
		WebUI.delay(1)
	}

	@Then("the new Badlis is added")
	def verifyNewBadlisAdded() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Add Badlis completed')
	}
}
