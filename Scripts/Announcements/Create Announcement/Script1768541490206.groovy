import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Create Announcement (browser lifecycle via @CreateAnnouncement hooks)
CucumberKW.runFeatureFile('Include/features/Create_Announcement.feature', FailureHandling.STOP_ON_FAILURE)
