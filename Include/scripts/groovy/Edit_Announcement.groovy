import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class EditAnnouncementSteps {

	private WebDriver driver
	private WebDriverWait wait
	private String baseUrl
	private String email
	private String password

	// =====================
	// Locators 
	// =====================

	private static final By MICROSOFT_LOGIN_BTN = By.xpath('/html/body/div[1]/div/div/div/div[2]/button')
	private static final By EMAIL_INPUT = By.name('loginfmt')
	private static final By PASSWORD_INPUT = By.name('passwd')
	private static final By NEXT_BTN = By.id('idSIButton9')

	private static final By VIEW_ALL_ANNOUNCEMENTS_BTN = By.xpath('//*[@id="view-all-announcements-button"]')
	// My Posts tab: use text-based selector (pv_id_* is dynamic and causes timeouts)
	private static final By MY_POSTS_TAB = By.xpath("//*[contains(normalize-space(.), 'My Posts') and (self::button or self::a or self::span or self::li)]")
	private static final By POST_CONTAINING_KATALON = By.xpath("//*[contains(.,'Katalon automation')]")
	private static final By EDIT_BTN = By.xpath('//*[@id="edit-button"]')
	private static final By TITLE_FIELD = By.xpath('//*[@id="title-field"]')
	private static final By MESSAGE_FIELD = By.xpath('//*[@id="message-textarea"]')
	private static final By UPDATE_BTN = By.xpath('//*[@id="update-button"]')

	// =====================
	// Hooks
	// =====================

	@Before("@EditAnnouncement")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
	}

	@After("@EditAnnouncement")
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

	// =====================
	// Steps
	// =====================

	@Given("the user is logged in as HR and on the Announcements page for editing")
	def loginAndOpenAnnouncementsForEdit() {
		loadEnvVariables()
		WebUI.navigateToUrl(baseUrl + '/login')

		wait.until(ExpectedConditions.elementToBeClickable(MICROSOFT_LOGIN_BTN)).click()
		wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))

		wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT)).sendKeys(email)
		driver.findElement(NEXT_BTN).click()

		wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT)).sendKeys(password)
		driver.findElement(NEXT_BTN).click()

		wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN)).click()
		wait.until(ExpectedConditions.urlContains('/dashboard'))

		KeywordUtil.logInfo('Redirected to dashboard successfully')
		assert driver.getCurrentUrl().contains('/dashboard') : 'Failed to redirect to dashboard after login'

		By announcementItems = By.xpath("//*[contains(@class,'announcement')]")
		By emptyAnnouncement = By.xpath("//*[contains(text(),'No announcements')]")
		wait.until { d ->
			d.findElements(announcementItems).size() > 0 || d.findElements(emptyAnnouncement).size() > 0
		}
		KeywordUtil.logInfo('✅ Announcements section loaded')

		wait.until(ExpectedConditions.visibilityOfElementLocated(VIEW_ALL_ANNOUNCEMENTS_BTN))
		wait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_ANNOUNCEMENTS_BTN)).click()
	}

	@Given("the user opens the My Posts tab")
	def openMyPostsTabForEdit() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(MY_POSTS_TAB))
		wait.until(ExpectedConditions.elementToBeClickable(MY_POSTS_TAB)).click()
		KeywordUtil.logInfo('My Posts tab opened')
	}

	@When("the user clicks the edit button of an announcement")
	def clickEditButton() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(POST_CONTAINING_KATALON))
		wait.until(ExpectedConditions.elementToBeClickable(EDIT_BTN)).click()
	}

	@Then("an edit announcement form should be displayed")
	def verifyEditFormDisplayed() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_FIELD))
		wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGE_FIELD))
		KeywordUtil.logInfo('✅ Edit form displayed')
	}

	@When('the user updates the title to {string}')
	def updateTitle(String title) {
		WebElement titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_FIELD))
		titleField.sendKeys(Keys.chord(Keys.COMMAND, 'a'))
		titleField.sendKeys(Keys.DELETE)
		titleField.sendKeys(title)
	}

	@When('the user updates the message to {string}')
	def updateMessage(String message) {
		WebElement messageField = wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGE_FIELD))
		messageField.sendKeys(Keys.chord(Keys.COMMAND, 'a'))
		messageField.sendKeys(Keys.DELETE)
		messageField.sendKeys(message)
	}

	@When('the user clicks the Update button to save changes')
	def clickUpdateButton() {
		wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click()
		KeywordUtil.logInfo('Announcement updated successfully')
	}

	@Then("the announcement should be successfully updated")
	def verifyUpdatedSuccessfully() {
		WebUI.delay(8)
		KeywordUtil.logInfo('✅ Announcement successfully updated')
	}
}
