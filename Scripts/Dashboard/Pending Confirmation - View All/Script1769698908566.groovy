import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

// Run BDD feature for Pending Confirmation - View All (browser lifecycle via @PendingConfirmationViewAll hooks)
CucumberKW.runFeatureFile('Include/features/Pending_Confirmation-ViewAll.feature', FailureHandling.STOP_ON_FAILURE)
