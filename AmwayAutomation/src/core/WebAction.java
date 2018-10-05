package core;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import utils.Locator;
import utils.TestNGUtils;

public class WebAction {
	WebDriver driver;
	AndroidDriver<MobileElement> appdriver;
	HashMap<String, Object> hashMap=new HashMap<String, Object>();
	public WebAction( WebDriver driver) {
		this.driver = driver;
	}
	public WebDriver getDriver() {
		return this.driver;
	}
	/**
	 * @author Aswathy_Krishnan
	 * Description: Click on a Locator
	 * @return
	 * @throws Exception
	 */
	public WebAction click(Locator loc) {
		try {
			driver.findElement(Locator.getByObject(loc)).click();
		}catch(Exception e) {
			TestNGUtils.reportLog("The element:"+loc.getKey()+" could not be clicked");
			throw e;
		}

		return this;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Store a value with key
	 * @return
	 * @throws Exception
	 */
	public  synchronized void storeKeyValue(String key,Object value) {
		hashMap.put(key, value);
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Retrieve value with Key
	 * @return
	 * @throws Exception
	 */
	public  synchronized Object retrieveKeyValue(String key) {
		return hashMap.get(key);
	}

	
	public WebAction scrollToElement(Locator loc) {
		try {

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(Locator.getByObject(loc)));

		}
		catch(Exception e) {				
		}

		return this;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Find element in the page by locator
	 * @return
	 * @throws Exception
	 */
	public WebElement findElement(Locator loc)  {

		waitTillElementVisible(3000,loc);
		WebElement ele=driver.findElement(Locator.getByObject(loc));
		return ele;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Get text from locator
	 * @return
	 * @throws Exception
	 */
	public String getText(Locator loc) {

		String text=	driver.findElement(Locator.getByObject(loc)).getText();
		return text;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Returns true if element visible in page 
	 * @return
	 * @throws Exception
	 */
	public boolean isElementVisible(Locator loc){
		boolean isvisible=false;
		try {
			driver.findElement(Locator.getByObject(loc));
			isvisible=true;
		}catch(Exception e) {

		}		
		return isvisible;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Send text to locator
	 * @return
	 * @throws Exception
	 */
	public WebAction sendText(Locator loc,String input) {
		waitTillElementVisible(3000,loc);
		driver.findElement(Locator.getByObject(loc)).clear();;
		driver.findElement(Locator.getByObject(loc)).sendKeys(input);

		return this;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Press enter in android
	 * @return
	 * @throws Exception
	 */
	public WebAction pressEnter() {

		appdriver.pressKeyCode(AndroidKeyCode.ENTER);		

		return this;
	}

	/**
	 * @author Aswathy_Krishnan
	 * Description: Wait till element is visible
	 * @return
	 * @throws Exception
	 */
	public void waitTillElementVisible(long sec, Locator loc) {
		try {
			WebDriverWait wait=new WebDriverWait(driver, sec);
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(Locator.getByObject(loc))));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @author Aswathy_Krishnan
	 * Description: wait for milliseconds
	 * @return
	 * @throws Exception
	 */
	public void waitFor(long sec) {
		try {
			Thread.sleep(sec);
			//appdriver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);

		}catch(Exception e) {
			e.printStackTrace();
		}


	}
}





