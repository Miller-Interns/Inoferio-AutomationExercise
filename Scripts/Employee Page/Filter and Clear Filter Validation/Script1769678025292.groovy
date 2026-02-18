import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/Filter_and_Clear_Filter_Validation.feature', FailureHandling.STOP_ON_FAILURE)
