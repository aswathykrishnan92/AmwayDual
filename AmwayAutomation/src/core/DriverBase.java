package core;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;


public class DriverBase {
	WebDriver driver;
	Action action;
	WebAction webAction;
	AndroidDriver<MobileElement>appDriver;


	public DriverBase() {
		if(TestProperties.APPLICATION_TYPE.toString().equalsIgnoreCase("desktop")||BasePage.retrieveKeyValue("flow").toString().equalsIgnoreCase("second")) {
			assignWebDriver();
			webAction=new WebAction(driver);
		}else {
			assignAndroidDriver();
			action=new Action(appDriver);
		}

	}
	public WebDriver getDriver() {
		return driver;
	}
	public AndroidDriver<MobileElement> getAndroidDriver() {
		if(getThread()!=null) {
			driverThread.remove();
		}	
		return appDriver;
	}
	public Action getAction() {
		return action;
	}
	public WebAction getWebAction() {
		return webAction;
	}

	public static AndroidDriver<MobileElement> getDriverInstanceForAndroid() {
		return getThread().getAndroidDriver();
	}
	public static WebDriver getDriverInstance() {
		if(getThread()!=null) {
			driverThread.remove();
		}		
		return getThread().getDriver();
	}
	public static ThreadLocal<DriverBase> driverThread=new ThreadLocal<DriverBase>() {
		@Override
		protected DriverBase initialValue() {
			return new DriverBase();
		}
	};
	public static DriverBase getThread() {
		return driverThread.get();
	}

	public WebDriver assignWebDriver() {
		DesiredCapabilities caps=null;
		caps=getWebCapabilities();

		try {
			String seleniumHost=TestProperties.SELENIUM_HOST.toString();
			String seleniumPort=":"+TestProperties.SELENIUM_PORT.toString();
			String hostURL = "http://" + seleniumHost + seleniumPort + "/wd/hub";
			driver=new RemoteWebDriver(new URL(hostURL), caps);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return driver;
	}
	public AndroidDriver<MobileElement> assignAndroidDriver() {
		DesiredCapabilities caps=null;
		caps=getCapabilities();

		try {
			if(TestProperties.APPLICATION_TYPE.toString().equals("desktop")) {
				String seleniumHost=TestProperties.SELENIUM_HOST.toString();
				String seleniumPort=":"+TestProperties.SELENIUM_PORT.toString();
				String hostURL = "http://" + seleniumHost + seleniumPort + "/wd/hub";
				driver=new RemoteWebDriver(new URL(hostURL), caps);
			}else {
				appDriver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appDriver;
	}


	public DesiredCapabilities getCapabilities() {
		DesiredCapabilities cap=null;;
		if(TestProperties.APPLICATION_TYPE.toString().equals("desktop")||TestProperties.APPLICATION_TYPE.toString().equals("desktop/mobile")) {
			ChromeOptions options=new ChromeOptions();
			String browser=TestProperties.BROWSER_NAME.toString();

			switch(browser) {
			case("chrome"):
				options.addArguments("--start-maximised");
			cap=new DesiredCapabilities();
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			cap.setCapability("browserName", browser);
			default:
				cap.setCapability("browserName", browser);
				break;
			}
			cap.setJavascriptEnabled(true);
			cap.setCapability("browserVersion", TestProperties.BROWSER_VERSION.toString().trim());

		}else {
			cap=new DesiredCapabilities();
			cap.setCapability(CapabilityType.BROWSER_NAME, "");
			cap.setCapability("deviceName", TestProperties.DEVICE_NAME.toString());
			cap.setCapability("platformVersion",TestProperties.PLATFORM_VERSION.toString());
			cap.setCapability("platformName", TestProperties.PLATFORM_NAME.toString());
			cap.setCapability("noReset", "true");
			cap.setCapability("fullReset", "false");
			cap.setCapability("app", TestProperties.APP_PATH.toString());
			cap.setCapability("appPackage", TestProperties.APP_PACKAGE.toString());
			cap.setCapability("launchActivity", TestProperties.APP_PACKAGE.toString()+".MainActivity");				

		}
		return cap;
	}
	public DesiredCapabilities getWebCapabilities() {
		DesiredCapabilities cap=null;;
		ChromeOptions options=new ChromeOptions();
		String browser=TestProperties.BROWSER_NAME.toString();

		switch(browser) {
		case("chrome"):
			options.addArguments("--start-maximised");
		cap=new DesiredCapabilities();
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		cap.setCapability("browserName", browser);
		default:
			cap.setCapability("browserName", browser);
			break;
		}
		cap.setJavascriptEnabled(true);
		cap.setCapability("browserVersion", TestProperties.BROWSER_VERSION.toString().trim());

		return cap;
	}

}
