package iFast.pages;


import org.testng.Assert;

import core.BasePage;
import utils.Locator;
import utils.Locator.Type;
import utils.TestNGUtils;

public class LoginPage extends BasePage {

public Locator USERNAME=new Locator("USERNAME", "//input[@name='userName']", Type.XPATH)	;
public Locator PASSWORD=new Locator("USERNAME", "//input[@name='password']", Type.XPATH)	;
public Locator lOGIN_BUTTON=new Locator("USERNAME", "//button[text()='Login']", Type.XPATH)	;

public LoginPage loginToiFast(String uname,String password) {
	TestNGUtils.reportLog("Logging in to iFast with credetionals: username:"+uname+" Password:"+password);
	getWebAction().findElement(USERNAME);
	getWebAction().sendText(USERNAME, uname);
	getWebAction().findElement(PASSWORD);
	getWebAction().sendText(PASSWORD, password);
	getWebAction().findElement(lOGIN_BUTTON);
	getWebAction().click(lOGIN_BUTTON);
	getWebAction().waitFor(2000);
	if(!getWebAction().isElementVisible(DashboardPage.MY_PROJECTS)) {
		Assert.assertFalse(false,"Login is unsuccessful");
	}
	return this;
}
	
}
