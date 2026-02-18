import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Delete Announcement (browser lifecycle via @DeleteAnnouncement hooks)
CucumberKW.runFeatureFile('Include/features/Delete_Announcement.feature', FailureHandling.STOP_ON_FAILURE)
