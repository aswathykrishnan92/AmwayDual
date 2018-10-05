package app.pages;

import java.util.Random;

import org.testng.Assert;

import core.BasePage;
import utils.Locator;
import utils.Locator.Type;
import utils.TestNGUtils;

public class SearchUnitPage extends BasePage {

	public static Locator ADD_UNIT=new Locator("Add unit", "//android.widget.TextView[@text='Add Unit']", Type.XPATH);
	public static Locator SEARCH_UNIT_BUTTON=new Locator("Search Unit Button", "android.widget.Button", Type.CLASSNAME);
	public static Locator TRY_AGAIN=new Locator("Try Again button", "//android.widget.Button[@text='Try Again']", Type.XPATH);
	public static Locator CLOSE_TRY_AGAIN=new Locator("Close Try Again ", "//android.widget.ImageView[@instance='0']", Type.XPATH);
	//public static Locator CLOSE_SEARCH_UNIT=new Locator("Close Search Unit ", "//android.widget.TextView[@instance='7']", Type.XPATH);
	public static Locator CLOSE_SEARCH_UNIT=new Locator("Close Search Unit ", "(//android.widget.TextView)[1]", Type.XPATH);
	public static Locator ADDED_UNIT=new Locator("Added Unit ", "//android.widget.TextView[@text='Bedroom']", Type.XPATH);
	//public static Locator UNIT_FOUND=new Locator("Unit Found", "//android.view.ViewGroup[@resource-id='com.amwayglobal.atmosphere:id/toolbar']//android.widget.TextView", Type.XPATH);
	public static Locator UNIT_FOUND=new Locator("Unit Found", "//android.widget.TextView[@text='Unit Found']", Type.XPATH);
	public static Locator CONNECTED_MESSAGE=new Locator("CONNECTED Message", "//android.widget.TextView[@text='Connected!']", Type.XPATH);
	public static Locator CONNECTED_WIFI=new Locator("Connect via wifi", "//android.widget.Button[@text='Connect to Wi-Fi']", Type.XPATH);
	public static Locator CONNECTED_BLUETOOTH=new Locator("Connect via bluetooth", "//android.widget.Button[@text='Bluetooth Only']", Type.XPATH);
	public static Locator FRAME=new Locator("Frame", "//android.widget.FrameLayout[@resource-id='android:id/content']", Type.XPATH);


	public SearchUnitPage() {

	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Click on a Random product in SRP after scrolling
	 * @return
	 * @throws Exception
	 */
	public SearchUnitPage addUnit() throws Exception {
		getAction().waitFor(15000);
		TestNGUtils.reportLog("Click on Add unit");
		getAction().findElement(ADD_UNIT);
		getAction().click(ADD_UNIT);

		return this;


	}
	public SearchUnitPage searchUnit() throws Exception {

		TestNGUtils.reportLog("Click on Search unit");
		getAction().findElement(SEARCH_UNIT_BUTTON);
		getAction().click(SEARCH_UNIT_BUTTON);
		getAction().waitFor(7000);

		return this;


	}
	public SearchUnitPage clickOnTryAgain() throws Exception {

		TestNGUtils.reportLog("Click on Try Again on failure to find devices");
		getAction().findElement(TRY_AGAIN);
		getAction().click(TRY_AGAIN);
		getAction().waitFor(7000);

		return this;


	}
	public SearchUnitPage unitFoundClickOnBlueTooth() throws Exception {
		TestNGUtils.reportLog("Validate unit found message");
		getAction().waitFor(5000);

		//getAction().getDriver().switchTo().frame("//android.widget.FrameLayout[@resource-id='android:id/content']");
		//getAction().getDriver().switchTo().frame("android:id/content");
		getAction().findElement(UNIT_FOUND);
		String txt=getAction().getText(UNIT_FOUND);
		getAction().waitFor(2000);
		Assert.assertEquals(txt, "Unit Found","The message Unit Found is not shown on PC");
		TestNGUtils.reportLog("Click on bluetooth of the device manually");
		//getAction().waitFor(5000);

		return this;


	}
	public SearchUnitPage connectToWifi() throws Exception {
		TestNGUtils.reportLog("Connect to wifi");

		//getAction().getDriver().switchTo().frame("//android.widget.FrameLayout[@resource-id='android:id/content']");
		//getAction().getDriver().switchTo().frame("android:id/content");
		if(!getAction().isElementVisible(CONNECTED_MESSAGE)){
			Assert.assertFalse(false, "Connected Message not shown on screen");
		}
		TestNGUtils.reportLog("Connected Message shown on screen");

		getAction().waitFor(2000);
		getAction().findElement(CONNECTED_WIFI);
		getAction().click(CONNECTED_WIFI);
		//getAction().waitFor(5000);

		return this;


	}
	public SearchUnitPage goBackToHomePage() throws Exception {


		TestNGUtils.reportLog("Navigating back to home page");

		getAction().findElement(CLOSE_TRY_AGAIN);
		getAction().click(CLOSE_TRY_AGAIN);
		getAction().waitFor(2000);
		getAction().waitTillElementVisible(5, CLOSE_SEARCH_UNIT);
		getAction().findElement(CLOSE_SEARCH_UNIT);
		getAction().click(CLOSE_SEARCH_UNIT);
		//getAction().waitFor(5000);

		return this;


	}




}
