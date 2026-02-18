import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Announcements - View My Posts (browser lifecycle via @AnnouncementsViewMyPosts hooks)
CucumberKW.runFeatureFile('Include/features/Announcements-View_My_Posts.feature', FailureHandling.STOP_ON_FAILURE)
