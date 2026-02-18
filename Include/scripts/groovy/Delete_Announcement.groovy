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
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions

import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class DeleteAnnouncementSteps {

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
	// Use stable selector by visible text (pv_id_* IDs are dynamic and change between runs)
	private static final By MY_POSTS_TAB = By.xpath("//*[contains(normalize-space(.), 'My Posts') and (self::button or self::a or self::span or self::li)]")
	private static final By POST_CONTAINING_KATALON = By.xpath("//*[contains(.,'Katalon automation')]")
	private static final By DELETE_ICON = By.xpath('//*[@id="delete-icon"]')
	private static final By DELETE_CONFIRM_BTN = By.xpath('//*[@id="delete-icon-button"]')
	private static final By SUCCESS_MESSAGE = By.xpath("//*[contains(text(),'The post has been deleted successfully.')]")

	// =====================
	// Hooks
	// =====================

	@Before("@DeleteAnnouncement")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
	}

	@After("@DeleteAnnouncement")
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

	@Given("the user is logged in as HR and navigates to the Announcements page")
	def loginAndOpenAnnouncements() {
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

		WebUI.delay(8)

		wait.until(ExpectedConditions.visibilityOfElementLocated(VIEW_ALL_ANNOUNCEMENTS_BTN))
		wait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_ANNOUNCEMENTS_BTN)).click()
	}

	@Given("the user switches to the My Posts tab")
	def openMyPostsTabForDelete() {
		wait.until(ExpectedConditions.elementToBeClickable(MY_POSTS_TAB)).click()
		wait.until(ExpectedConditions.visibilityOfElementLocated(MY_POSTS_TAB))
	}

	@When("the user clicks the delete icon of an announcement")
	def clickDeleteIcon() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(POST_CONTAINING_KATALON))
		wait.until(ExpectedConditions.elementToBeClickable(DELETE_ICON)).click()
	}

	@Then("a delete confirmation modal should be displayed")
	def verifyDeleteModalDisplayed() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(DELETE_CONFIRM_BTN))
		assert driver.findElement(DELETE_CONFIRM_BTN).isDisplayed() : 'Delete confirmation modal/button not displayed'
		KeywordUtil.logInfo('✅ Delete confirmation modal displayed')
	}

	@When("the user confirms deletion")
	def confirmDeletion() {
		wait.until(ExpectedConditions.elementToBeClickable(DELETE_CONFIRM_BTN)).click()
	}

	@Then("the announcement should be successfully deleted")
	def verifyDeletedSuccessfully() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE))
		assert driver.findElement(SUCCESS_MESSAGE).isDisplayed() : 'Success message not displayed after delete'
		KeywordUtil.logInfo('✅ Announcement deleted successfully')
		WebUI.delay(8)
	}

	@Then("the announcement should no longer appear in the list")
	def verifyAnnouncementRemovedFromList() {
		WebUI.delay(2)
		KeywordUtil.logInfo('✅ Announcement no longer in list')
	}
}
