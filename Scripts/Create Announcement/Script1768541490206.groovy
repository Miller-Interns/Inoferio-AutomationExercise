import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import org.openqa.selenium.By as By
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.support.ui.WebDriverWait as WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions as ExpectedConditions
import java.time.Duration as Duration
import java.nio.file.Files as Files
import java.nio.file.Paths as Paths
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

Map<String, String> env = [:]

Files.readAllLines(Paths.get('.env')).each { line ->
    if (!line.startsWith('#') && line.contains('=')) {
        def (key, value) = line.split('=', 2)
        env[key.trim()] = value.trim()
    }
}


String baseUrl = env['BASE_URL']

String email = env['MS_EMAIL']

String password = env['MS_PASSWORD']

assert ((baseUrl != null) && (email != null)) && (password != null) : 'Missing required environment variables'

WebUI.openBrowser('')

WebUI.maximizeWindow()

WebUI.navigateToUrl(baseUrl + '/login')

WebDriver driver = DriverFactory.getWebDriver()

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30))

// Microsoft login button
By microsoftLoginBtn = By.xpath('/html/body/div[1]/div/div/div/button')

wait.until(ExpectedConditions.elementToBeClickable(microsoftLoginBtn)).click()

// Microsoft login page
wait.until(ExpectedConditions.urlContains('login.microsoftonline.com'))

// Email
wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('loginfmt'))).sendKeys(email)

driver.findElement(By.id('idSIButton9')).click()

// Password
wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('passwd'))).sendKeys(password)

driver.findElement(By.id('idSIButton9')).click()

// Stay signed in?
wait.until(ExpectedConditions.elementToBeClickable(By.id('idSIButton9'))).click()

wait.until(ExpectedConditions.urlContains('/dashboard'))

KeywordUtil.logInfo('Redirected to dashboard successfully')

String currentUrl = driver.getCurrentUrl()

assert currentUrl.contains('/dashboard') : 'Failed to redirect to dashboard after login'

// Click "Create Post" button
By createPostBtn = By.xpath('//*[@id="create-post-button"]')

wait.until(ExpectedConditions.visibilityOfElementLocated(createPostBtn))

wait.until(ExpectedConditions.elementToBeClickable(createPostBtn)).click()

// Wait for modal title input
By postTitleInput = By.xpath('//*[@id="title-input"]')

wait.until(ExpectedConditions.visibilityOfElementLocated(postTitleInput)).sendKeys('Automation Test Announcement')

// Message textarea
By messageInput = By.xpath('//*[@id="message-input"]')

wait.until(ExpectedConditions.visibilityOfElementLocated(messageInput)).sendKeys('This announcement was created using Katalon automation.')

// Publish button
By publishBtn = By.xpath('//*[@id="publish-button"]')

wait.until(ExpectedConditions.elementToBeClickable(publishBtn)).click()

KeywordUtil.logInfo('Announcement published successfully')

By successToast = By.xpath('/html/body/div[5]/div/div/div')

wait.until(ExpectedConditions.visibilityOfElementLocated(successToast))

assert driver.findElement(successToast).isDisplayed() : 'Success message not displayed after publishing announcement'

WebUI.delay(8)

WebUI.closeBrowser()

