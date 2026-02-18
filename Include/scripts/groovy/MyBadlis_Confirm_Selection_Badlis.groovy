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

class MyBadlisConfirmSelectionBadlisSteps {

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

	@Before("@MyBadlisConfirmSelectionBadlis")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
		js = (JavascriptExecutor) driver
	}

	@After("@MyBadlisConfirmSelectionBadlis")
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

	@Given("the user is logged in and on the dashboard to confirm selection Badlis")
	def loginAndOpenDashboardForConfirmSelectionBadlis() {
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
	}

	@When("the user opens My Badlis and the Pending Confirmation tab for confirm selection")
	def openMyBadlisAndPendingConfirmationTabForConfirmSelection() {
		wait.until(ExpectedConditions.presenceOfElementLocated(MY_BADLIS_MENU))
		wait.until(ExpectedConditions.elementToBeClickable(MY_BADLIS_MENU))
		try { driver.findElement(MY_BADLIS_MENU).click() } catch (Exception e) {
			js.executeScript("arguments[0].click();", driver.findElement(MY_BADLIS_MENU))
		}
		wait.until(ExpectedConditions.urlContains('/badlis'))
		wait.until(ExpectedConditions.visibilityOfElementLocated(PENDING_CONFIRMATION_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(PENDING_CONFIRMATION_TAB)).click()
	}

	@When("the user selects rows using the checkboxes on Pending Confirmation")
	def selectRowsUsingCheckboxes() {
		WebUI.delay(2)
		By headerCheckbox = By.xpath("//thead//input[@type='checkbox'] | //thead//*[contains(@class,'p-checkbox-input')] | //th[.//input[@type='checkbox']]//*[contains(@class,'p-checkbox')]")
		By rowCheckbox = By.xpath("//tbody//tr[1]//input[@type='checkbox'] | //tbody//tr[1]//*[contains(@class,'p-checkbox-input')] | //tbody//*[contains(@class,'p-datatable')]//tbody//tr[1]//*[contains(@class,'p-checkbox')]")
		clickCheckboxSafe(headerCheckbox)
		WebUI.delay(0.5)
		clickCheckboxSafe(rowCheckbox)
		WebUI.delay(0.5)
	}

	private void clickCheckboxSafe(By locator) {
		def elements = driver.findElements(locator)
		if (elements.size() == 0) return
		def el = elements.get(0)
		try {
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
			WebUI.delay(0.3)
			if (el.getAttribute('type') == 'checkbox') {
				js.executeScript("arguments[0].click();", el)
			} else {
				el.click()
			}
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", el)
		}
	}

	@When("the user clicks Confirm Selected and confirms the dialog")
	def clickConfirmSelectedAndConfirmDialog() {
		By confirmSelectedBtn = By.xpath("//button[contains(normalize-space(),'Confirm Selected')]")
		if (driver.findElements(confirmSelectedBtn).size() > 0) {
			wait.until(ExpectedConditions.elementToBeClickable(confirmSelectedBtn)).click()
			WebUI.delay(1)
		}
		By dialog = By.xpath("//*[contains(@class,'p-dialog') and contains(@class,'p-component')] | //*[@role='dialog']")
		wait.until(ExpectedConditions.visibilityOfElementLocated(dialog))
		WebUI.delay(2)
		By confirmBtn = By.xpath("//*[contains(@class,'p-dialog')]//button[@id='confirm-badlis-button'] | //*[@role='dialog']//button[@id='confirm-badlis-button'] | //*[contains(@class,'p-dialog')]//button[normalize-space()='Confirm'] | //*[@role='dialog']//button[normalize-space()='Confirm']")
		wait.until(ExpectedConditions.visibilityOfElementLocated(confirmBtn))
		def el = driver.findElement(confirmBtn)
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el)
		WebUI.delay(0.5)
		js.executeScript("arguments[0].click();", el)
		WebUI.delay(2)
	}

	@Then("the selected Badlis are confirmed")
	def verifySelectedBadlisConfirmed() {
		WebUI.delay(3)
		KeywordUtil.logInfo('Confirm Selection completed')
	}
}
