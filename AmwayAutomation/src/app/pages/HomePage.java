package app.pages;


import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import core.BasePage;
import utils.Locator;
import utils.TestNGUtils;

public class HomePage extends BasePage {

	WebDriver driver;


	public Locator SIGN_IN_BUTTON=new Locator("Sign In Button", "//android.widget.Button[@text='Log In or Create Account']",Locator.Type.XPATH );
	public Locator USERNAME=new Locator("username", "home__formMemberEmail_username_username",Locator.Type.ID );
	public Locator PASSWORD=new Locator("password", "home__formMemberEmail_pass1_password",Locator.Type.ID );
	public Locator SIGN_IN_BUTTON_ACCEPT=new Locator("Sign In Button Accept", "//android.widget.Button[@content-desc='SIGN IN']",Locator.Type.XPATH );
	public Locator HAMBURGER_BUTTON=new Locator("Hamburger Button", "com.amwayglobal.atmosphere:id/hamburgerButton",Locator.Type.ID );
	public Locator SETTINGS_BUTTON=new Locator("Settings Button", "//android.widget.TextView[@text='Settings']",Locator.Type.XPATH );
	public Locator LOG_OUT_BUTTON=new Locator("Logout Button", "//android.widget.Button[@text='Log Out']",Locator.Type.XPATH );
	public Locator YES_LOG_OUT_BUTTON=new Locator(" Yes Logout Button", "//android.widget.Button[@text='Yes, Log Out']",Locator.Type.XPATH );



	public HomePage() {

	}
	/**
	 * @author Aswathy_Krishnan
	 * Description: Sign in to the site
	 * @return
	 * @throws Exception
	 */
	public HomePage signIn(String username,String password) throws Exception {

		getAction().storeKeyValue("signedIn", false);
		getAction().waitFor(11000);
		TestNGUtils.reportLog("Click on Sign in Button");
		getAction().findElement(SIGN_IN_BUTTON);
		getAction().click(SIGN_IN_BUTTON);
		getAction().waitFor(25000);
		TestNGUtils.reportLog("Entering the login credentials: Username-"+username+" Password-"+password);
		getAction().findElement(USERNAME);
		getAction().sendText(USERNAME, username);
		getAction().findElement(PASSWORD);
		getAction().click(PASSWORD);
		getAction().sendText(PASSWORD, password);
		getAction().pressEnter();
		//getAction().click(SIGN_IN_BUTTON_ACCEPT);
		getAction().waitFor(11000);
		//Assert.assertTrue(!getAction().isElementVisible(SIGN_IN_BUTTON_ACCEPT), "Sign in button still visible. Sign In unsuccessful");
		getAction().storeKeyValue("signedIn", true);
		return this;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Sign out from site
	 * @return
	 * @throws Exception
	 */
	public HomePage signOut() {
		if((boolean) getAction().retrieveKeyValue("signedIn")) {
			TestNGUtils.reportLog("User Sign out");
			getAction().findElement(HAMBURGER_BUTTON);
			getAction().click(HAMBURGER_BUTTON);
			getAction().findElement(SETTINGS_BUTTON);
			getAction().click(SETTINGS_BUTTON);
			getAction().findElement(LOG_OUT_BUTTON);
			getAction().click(LOG_OUT_BUTTON);
			getAction().findElement(YES_LOG_OUT_BUTTON);
			getAction().click(YES_LOG_OUT_BUTTON);
			getAction().waitFor(5000);

		}
		return this;
	}
	public HomePage goBack(int number) {
	
			TestNGUtils.reportLog("User goes back "+number+" times");
			while(number>0) {
				getAction().pressBackButton();
				getAction().waitFor(2000);
				number--;
			}
			
	
		return this;
	}


}
