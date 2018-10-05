package iFast.pages;

import core.BasePage;
import utils.Locator;
import utils.TestNGUtils;
import utils.Locator.Type;

public class RunAndExecutionPage extends BasePage {
	public static Locator RUN_BUTTON=new Locator("RUN BUTTON", "(//a[@class='btn btn-info btn_run_test'])[1]", Type.XPATH)	; 
	public static Locator YES_RUN=new Locator("Yes in run popup", "(//a[@class='popup_button_confirm' and text()='YES'])[3]", Type.XPATH)	; 
	public static Locator NO_SAVEREPORT=new Locator("No in save report popup", "(//div[@class='modal-content']//a[text()='NO'])[3]", Type.XPATH)	; 
	public static Locator TOTAL_TESTCASE_COUNT=new Locator("TOTAL TESTCASE COUNT", "(//label[text()='Total Test Cases:']//ancestor::div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12 zero_padding']/div/label)[2]", Type.XPATH)	; 
	public static Locator PASS_TESTCASE=new Locator("PASS TESTCASE COUNT", "(//label[text()='Total Cases Passed:']//ancestor::div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12 zero_padding']/div/label)[2]", Type.XPATH)	; 
	public static Locator PASS_PERCENTAGE_TESTCASE=new Locator("PASS PERCENTAGE TESTCASE ", "(//label[text()='Total Percentage Passed:']//ancestor::div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12 zero_padding']/div/label)[2]", Type.XPATH)	; 
	public static Locator FAIL_TESTCASE=new Locator("FAIL TESTCASE COUNT", "(//label[text()='Total Cases Failed:']//ancestor::div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12 zero_padding']/div/label)[2]", Type.XPATH)	; 
	public static Locator FAIL_PERCENTAGE_TESTCASE=new Locator("FAIL PERCENTAGE TESTCASE ", "(//label[text()='Total Percentage Failed:']//ancestor::div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12 zero_padding']/div/label)[2]", Type.XPATH)	; 

public RunAndExecutionPage clickOnRun() {
		
		TestNGUtils.reportLog("Click on Run Button in Run And Execution Page");	
		getWebAction().findElement(RUN_BUTTON);
		getWebAction().click(RUN_BUTTON);
		getWebAction().findElement(YES_RUN);
		getWebAction().click(YES_RUN);
		
				
		return this;	
	}	
public RunAndExecutionPage clickOnNoInSaveReports() {
	
	TestNGUtils.reportLog("Click on No in save report");	
	getWebAction().findElement(NO_SAVEREPORT);
	getWebAction().click(NO_SAVEREPORT);
		
	return this;	
}	
public RunAndExecutionPage getiFastReportDetails() {
	
	TestNGUtils.reportLog("Get the details from ifast Report");	
	
	getWebAction().findElement(TOTAL_TESTCASE_COUNT);
	String tot_count=getWebAction().getText(TOTAL_TESTCASE_COUNT);
	TestNGUtils.reportLog("Total test case count is:"+tot_count);	
	getWebAction().findElement(PASS_TESTCASE);
	String pass_count=getWebAction().getText(PASS_TESTCASE);
	TestNGUtils.reportLog("Total test case passed is:"+pass_count);	
	getWebAction().findElement(FAIL_TESTCASE);
	String fail_count=getWebAction().getText(FAIL_TESTCASE);
	TestNGUtils.reportLog("Total test case failed is:"+fail_count);	
	getWebAction().findElement(PASS_PERCENTAGE_TESTCASE);
	String passpercent_count=getWebAction().getText(PASS_PERCENTAGE_TESTCASE);
	TestNGUtils.reportLog("Pass Percentage:"+passpercent_count);	
	getWebAction().findElement(FAIL_PERCENTAGE_TESTCASE);
	String failpercent_count=getWebAction().getText(FAIL_PERCENTAGE_TESTCASE);
	TestNGUtils.reportLog("Fail Percentage:"+failpercent_count);	
	
	return this;	
}	

}
