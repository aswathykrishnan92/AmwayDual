package iFast.pages;

import org.testng.Assert;

import core.BasePage;
import utils.Locator;
import utils.TestNGUtils;
import utils.Locator.Type;

public class TestCasesPage extends BasePage{
	public static Locator RUN_BUTTON=new Locator("RUN BUTTON", "(//a[@class='btn btn-info btn_run_test'])[1]", Type.XPATH)	; 
	public static Locator YES_RUN=new Locator("Yes in run popup", "(//a[@class='popup_button_confirm' and text()='YES'])[3]", Type.XPATH)	; 
	public static Locator RUN_BREADCRUMB=new Locator("RUN AND EXECUTION BREADCRUMB", "//h6[@class='breadcrumb_menu ng-binding' and contains(text(),'Run and Execution')]", Type.XPATH)	; 

public TestCasesPage clickOnRunButtonInTestCasePage() {
		
		TestNGUtils.reportLog("Click on Run Button in TestCase page");
		getWebAction().waitTillElementVisible(2000,RUN_BUTTON);
		getWebAction().click(RUN_BUTTON);
		getWebAction().waitTillElementVisible(2000, RUN_BREADCRUMB);
		if(getWebAction().isElementVisible(RUN_BREADCRUMB)) {
			Assert.assertFalse(false, "Run and execution page is not loaded correctly");
		}
				
		return this;	
	}	
}
