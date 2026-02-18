import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/MyBadlis_View_Badlis_Details.feature', FailureHandling.STOP_ON_FAILURE)
