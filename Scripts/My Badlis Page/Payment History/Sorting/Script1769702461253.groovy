import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/MyBadlis_Payment_History_Sorting.feature', FailureHandling.STOP_ON_FAILURE)
