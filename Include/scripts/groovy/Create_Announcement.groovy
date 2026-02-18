import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import cucumber.api.java.Before
import cucumber.api.java.After

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import java.nio.file.Files
import java.nio.file.Paths

class AnnouncementSteps {

	WebDriver driver
	WebDriverWait wait
	private String baseUrl
	private String email
	private String password

	// =====================
	// Hooks
	// =====================

	@Before("@CreateAnnouncement")
	def beforeScenario() {
		loadEnvVariables()
		WebUI.openBrowser('')
		WebUI.maximizeWindow()
		driver = DriverFactory.getWebDriver()
		wait = new WebDriverWait(driver, Duration.ofSeconds(40))
	}

	@After("@CreateAnnouncement")
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

	@Given("the user is logged in as HR and on the dashboard")
	def loginAsHR() {
		WebUI.navigateToUrl(baseUrl + '/login')

		By microsoftLoginBtn = By.xpath('/html/body/div[1]/div/div/div/div[2]/button')
		wait.until(ExpectedConditions.elementToBeClickable(microsoftLoginBtn)).click()

		wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('loginfmt'))).sendKeys(email)
		driver.findElement(By.id('idSIButton9')).click()

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('passwd'))).sendKeys(password)
		driver.findElement(By.id('idSIButton9')).click()

		wait.until(ExpectedConditions.elementToBeClickable(By.id('idSIButton9'))).click()

		wait.until(ExpectedConditions.urlContains('/dashboard'))
		KeywordUtil.logInfo('Redirected to dashboard successfully')
		assert driver.getCurrentUrl().contains('/dashboard') : 'Failed to redirect to dashboard after login'
	}

	@Given("the Announcements section is displayed")
	def verifyAnnouncementsSection() {
		By viewAllButton = By.xpath('//*[@id="view-all-announcements-button"]')
		wait.until(ExpectedConditions.visibilityOfElementLocated(viewAllButton))
		assert driver.findElement(viewAllButton).isDisplayed() : 'Announcements section not displayed'
		KeywordUtil.logInfo('Announcements section is displayed')
	}

	@When("the user clicks the Create Announcement button")
	def clickCreateAnnouncementButton() {
		By button = By.xpath('//*[@id="create-post-button"]')
		wait.until(ExpectedConditions.elementToBeClickable(button)).click()
	}

	@When("the user clicks the Publish button")
	def clickPublishButton() {
		By button = By.xpath('//*[@id="publish-button"]')
		wait.until(ExpectedConditions.elementToBeClickable(button)).click()
	}

	@Then("a create announcement modal should be displayed")
	def verifyCreateModal() {
		By modalById = By.xpath('//*[@id="announcement-modal"]')
		By titleInput = By.xpath('//*[@id="title-input"]')
		By dialogByRole = By.xpath('//*[@role="dialog"]')
		wait.until(ExpectedConditions.or(
			ExpectedConditions.visibilityOfElementLocated(modalById),
			ExpectedConditions.visibilityOfElementLocated(titleInput),
			ExpectedConditions.visibilityOfElementLocated(dialogByRole)
		))
		KeywordUtil.logInfo('Create announcement modal is displayed')
	}

	@When("the user enters a valid title {string}")
	def enterTitle(String title) {
		By postTitleInput = By.xpath('//*[@id="title-input"]')
		wait.until(ExpectedConditions.visibilityOfElementLocated(postTitleInput)).sendKeys(title)
	}

	@When("the user enters a valid message {string}")
	def enterMessage(String message) {
		By messageInput = By.xpath('//*[@id="message-input"]')
		wait.until(ExpectedConditions.visibilityOfElementLocated(messageInput)).sendKeys(message)
	}

	@Then("the announcement should be successfully published")
	def verifyPublished() {
		By successToast = By.xpath('/html/body/div[5]/div/div/div')
		wait.until(ExpectedConditions.visibilityOfElementLocated(successToast))
		assert driver.findElement(successToast).isDisplayed() : 'Success message not displayed after publishing'
		KeywordUtil.logInfo('Announcement published successfully')
	}

	@Then("the new announcement should appear in the Announcements list")
	def verifyAnnouncementInList() {
		By newAnnouncement = By.xpath("//*[contains(., 'Automation Test Announcement') and (self::div or self::span or self::p or self::h1 or self::h2 or self::h3 or self::li or self::a)]")
		wait.until(ExpectedConditions.visibilityOfElementLocated(newAnnouncement))
		assert driver.findElement(newAnnouncement).isDisplayed() : 'New announcement not found in list'
		KeywordUtil.logInfo('New announcement appears in the list')
		WebUI.delay(2)
	}
}
