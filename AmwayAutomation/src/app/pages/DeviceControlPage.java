package app.pages;

import core.BasePage;
import utils.Locator;
import utils.Locator.Type;
import utils.TestNGUtils;

public class DeviceControlPage extends BasePage {
	
public static Locator ADDED_UNIT=new Locator("Added Unit ", "//android.widget.TextView[@text='Bedroom']", Type.XPATH);
public static Locator EXISTING_MODE=new Locator("Existing mode ", "//android.widget.TextView[contains(@content-desc,'Status')]", Type.XPATH);
public static Locator MODE_SELECTION=new Locator("Mode selection ", "//android.widget.ImageView[@instance='{0}']", Type.XPATH) ;
public static Locator DECREASE_FAN_SPEED=new Locator("Decrease fan speed ", "//android.widget.ImageView[@instance='8']", Type.XPATH) ;
public static Locator INCREASE_FAN_SPEED=new Locator("Increase fan speed ", "//android.widget.ImageView[@instance='9']", Type.XPATH) ;
public static Locator DETAILS=new Locator("Details ", "//android.widget.Button[@text='Details']", Type.XPATH) ;
public static Locator DETAILS_WIDGET=new Locator("Widget Details ", "//android.widget.TextView[@text='{0}']", Type.XPATH) ;
public static Locator FIRMWARE_VERSION=new Locator("Firmware version ", "//android.widget.TextView[@text='Firmware Version']//following::android.widget.TextView[@instance='11']", Type.XPATH) ;
public static Locator RELEASE_DATE=new Locator("Release date ", "//android.widget.TextView[@text='Release Date']//following::android.widget.TextView[@instance='13']", Type.XPATH) ;
public static Locator INSTALLATION_DATE=new Locator("Installation date ", "//android.widget.TextView[@text='Installation Date']//following::android.widget.TextView[@instance='15']", Type.XPATH) ;


public DeviceControlPage clickOnAddedDevice() {
	
	getAction().waitFor(15000);	
	TestNGUtils.reportLog("Clicking on the added device in home page");
	getAction().findElement(EXISTING_MODE);
	getAction().waitFor(2000);
	String mode=getAction().getText(EXISTING_MODE);
	getAction().storeKeyValue("mode", mode);
	TestNGUtils.reportLog("Already selected mode:"+mode);
	getAction().findElement(ADDED_UNIT);
	getAction().click(ADDED_UNIT);
	getAction().waitFor(2000);	
	
	return this;
}
public DeviceControlPage selectMode(String mode) {
	TestNGUtils.reportLog("Selecting mode:"+mode);
	String modeFromHome=(String) getAction().retrieveKeyValue("mode");
	if(!modeFromHome.equalsIgnoreCase(mode)) {
	switch(mode) {
	case "Night":
	case "night":
		getAction().findElement(MODE_SELECTION.format("3"));
		getAction().click(MODE_SELECTION.format("3"));
		break;
	case "Auto":
	case "auto":
		getAction().findElement(MODE_SELECTION.format("5"));
		getAction().click(MODE_SELECTION.format("5"));
		break;
	case "Turbo":
	case "turbo":
		getAction().findElement(MODE_SELECTION.format("7"));
		getAction().click(MODE_SELECTION.format("7"));
		break;
		
	}
	}
	getAction().waitFor(2000);	
	return this;
}

public DeviceControlPage increaseFanSpeed() {
	TestNGUtils.reportLog("Increase fan speed");
	getAction().findElement(INCREASE_FAN_SPEED);
	for(int i=0;i<3;i++) {
	getAction().click(INCREASE_FAN_SPEED);
	getAction().waitFor(3000);	
	}
	return this;
}
public DeviceControlPage decreaseFanSpeed() {
	TestNGUtils.reportLog("Decrease fan speed");
	getAction().findElement(DECREASE_FAN_SPEED);
	for(int i=0;i<3;i++) {
		getAction().click(DECREASE_FAN_SPEED);
		getAction().waitFor(3000);	
	}
	return this;
}
public DeviceControlPage clickOnDetails() {
	TestNGUtils.reportLog("Click On Details Section");
	getAction().findElement(DETAILS);
	getAction().click(DETAILS);
	
	return this;
}
public DeviceControlPage clickOnValueInDetailsWidget(String value) {
	TestNGUtils.reportLog("Click On :"+value+" value in details widget");
	getAction().findElement(DETAILS_WIDGET.format(value));
	getAction().click(DETAILS_WIDGET.format(value));
	
	return this;
}
public DeviceControlPage verifyFirmwareDetails() {
	TestNGUtils.reportLog("Verify firmware details");
	getAction().waitFor(3000);
	getAction().findElement(FIRMWARE_VERSION);
	String version=getAction().getText(FIRMWARE_VERSION);
	getAction().findElement(RELEASE_DATE);
	String date=getAction().getText(RELEASE_DATE);
	getAction().findElement(INSTALLATION_DATE);
	String installdate=getAction().getText(INSTALLATION_DATE);
	
	TestNGUtils.reportLog("The firmware version is "+version);
	TestNGUtils.reportLog("The release date is "+date);
	TestNGUtils.reportLog("The installation date is "+installdate);

	
	return this;
}



}
