package utils;

import org.openqa.selenium.WebDriver;

import core.DriverBase;
import core.TestProperties;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class TestInitializationUtils {


	public AndroidDriver<MobileElement> getDriver() {
		AndroidDriver<MobileElement> driver=DriverBase.getDriverInstanceForAndroid();
		return driver;
	}
	public WebDriver getDriverForWeb() {
		String url=TestProperties.TEST_URL.toString();
		//DriverBase.driverThread.remove();
		WebDriver driver=DriverBase.getDriverInstance();
		driver.get(url);
		return driver;
	}
}
