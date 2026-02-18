import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/HRBadlis_Sorting_PayHistory.feature', FailureHandling.STOP_ON_FAILURE)
