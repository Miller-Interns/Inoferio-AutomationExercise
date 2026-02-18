import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Edit Announcement (browser lifecycle via @EditAnnouncement hooks)
CucumberKW.runFeatureFile('Include/features/Edit_Announcement.feature', FailureHandling.STOP_ON_FAILURE)
