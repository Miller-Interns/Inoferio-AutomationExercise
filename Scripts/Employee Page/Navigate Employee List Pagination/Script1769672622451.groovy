import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.model.FailureHandling

CucumberKW.runFeatureFile('Include/features/Navigate_Employee_List_Pagination.feature', FailureHandling.STOP_ON_FAILURE)
