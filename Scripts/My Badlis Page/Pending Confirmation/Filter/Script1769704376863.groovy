import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/MyBadlis_Pending_Confirmation_Filter.feature', FailureHandling.STOP_ON_FAILURE)
