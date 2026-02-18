import cucumber.api.java.en.Given
import cucumber.api.java.en.When

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

/**
 * Shared step definitions for common Announcement-related steps
 * Used by Delete, Edit, and other Announcement features
 */
public class CommonAnnouncementSteps {

	private WebDriver getDriver() {
		return DriverFactory.getWebDriver()
	}

	private WebDriverWait getWait() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(40))
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

	// Shared locators
	private static final By MICROSOFT_LOGIN_BTN = By.xpath('/html/body/div[1]/div/div/div/div[2]/button')
	private static final By EMAIL_INPUT = By.name('loginfmt')
	private static final By PASSWORD_INPUT = By.name('passwd')
	private static final By NEXT_BTN = By.id('idSIButton9')
	private static final By VIEW_ALL_ANNOUNCEMENTS_BTN = By.xpath('//*[@id="view-all-announcements-button"]')
	// Use stable selector by visible text (pv_id_* IDs are dynamic and change between runs)
	private static final By MY_POSTS_TAB = By.xpath("//*[contains(normalize-space(.), 'My Posts') and (self::button or self::a or self::span or self::li)]")

	private String baseUrl
	private String email
	private String password

	@Given("the user is logged in as HR and on the Announcements page")
	def loginAndOpenAnnouncements() {
		loadEnvVariables()
		WebDriver driver = getDriver()
		WebDriverWait wait = getWait()

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

	@Given('the user is on the "My Posts" tab')
	def openMyPostsTab() {
		WebDriverWait wait = getWait()
		wait.until(ExpectedConditions.elementToBeClickable(MY_POSTS_TAB)).click()
		wait.until(ExpectedConditions.visibilityOfElementLocated(MY_POSTS_TAB))
	}

	@Given('the user is on the {string} tab')
	def openTab(String tabName) {
		WebDriverWait wait = getWait()
		if (tabName == 'My Posts') {
			wait.until(ExpectedConditions.elementToBeClickable(MY_POSTS_TAB)).click()
			wait.until(ExpectedConditions.visibilityOfElementLocated(MY_POSTS_TAB))
		} else {
			throw new IllegalArgumentException("Unknown tab: " + tabName)
		}
	}

	// Generic "clicks the {string} button" step removed to avoid conflicts with Login steps
	// Each feature now has specific button click steps:
	// - Create Announcement: "clicks the Create Announcement button" and "clicks the Publish button"
	// - Edit Announcement: "clicks the Update button to save changes"
	// - Login: specific steps with quotes like "clicks the 'Login with Microsoft' button"
}
