import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Announcements - View All Posts (browser lifecycle via @AnnouncementsViewAllPosts hooks)
CucumberKW.runFeatureFile('Include/features/Announcements-View_All_Posts.feature', FailureHandling.STOP_ON_FAILURE)
