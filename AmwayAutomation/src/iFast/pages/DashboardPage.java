package iFast.pages;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import core.BasePage;
import utils.Locator;
import utils.Locator.Type;
import utils.TestNGUtils;

public class DashboardPage extends BasePage {
	public static Locator MY_PROJECTS=new Locator("my projects", "//img[contains(@src,'MyProject.jpg') and @class='img-responsive']", Type.XPATH)	;
	public static Locator OPEN_PROJECT=new Locator("open project", "(//div[contains(@class,'myproject_tiles')]//span[text()=' OPEN'])[1]", Type.XPATH)	;
	public static Locator CATEGORY_TESTS=new Locator("Category Test", "//h4[@class='panel-title']//span[text()='{0}']", Type.XPATH)	; //AMWAYTESTAPI
	public static Locator SUBCATEGORY_TESTS=new Locator("Subcategory Test", "//div[contains(@class,'panel panel-default custom_panel_accordion_border')]//span[@class='title_accordion_submenu ng-binding' and text()='{0}']", Type.XPATH)	; //API TEST
	public static Locator RUN_BUTTON=new Locator("RUN BUTTON", "(//a[@class='btn btn-info btn_run_test'])[1]", Type.XPATH)	; //API TEST


	public DashboardPage goToTestCasePage(String cat, String Subcat) {

		TestNGUtils.reportLog("Click on Open to open the project list");
		Actions action=new Actions(getWebAction().getDriver());
		WebElement project=getWebAction().findElement(MY_PROJECTS);
		action.moveToElement(project).build().perform();
		getWebAction().findElement(OPEN_PROJECT);
		getWebAction().click(OPEN_PROJECT);
		TestNGUtils.reportLog("Click on Category test:"+cat);
		getWebAction().waitFor(2000);
		WebElement category=getWebAction().findElement(CATEGORY_TESTS.format(cat));
		action.moveToElement(category).build().perform();
		getWebAction().click(CATEGORY_TESTS.format(cat));
		TestNGUtils.reportLog("Click on Subcategory test:"+Subcat);
		getWebAction().findElement(SUBCATEGORY_TESTS.format(Subcat));
		getWebAction().click(SUBCATEGORY_TESTS.format(Subcat));
		getWebAction().waitTillElementVisible(2000,RUN_BUTTON);
		if(!getWebAction().isElementVisible(RUN_BUTTON)) {
			Assert.assertFalse(false, "Test case listing page is not loaded");
		}

		return this;	
	}
	

}
