package app.tests;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import app.pages.DeviceControlPage;
import app.pages.HomePage;
import app.pages.SearchUnitPage;
import core.BasePage;
import core.DriverBase;
import core.TestProperties;
import iFast.pages.DashboardPage;
import iFast.pages.LoginPage;
import iFast.pages.RunAndExecutionPage;
import iFast.pages.TestCasesPage;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import testdata.yaml.LoginData;
import testdata.yaml.ModeDetails;
import utils.TestInitializationUtils;
import utils.TestNGUtils;



public class EndToEndTestCases {
	@Test(groups= "EndToEndTest")
	public void EndToEndTest() throws Exception {
		String testCaseName=new Exception().getStackTrace()[0].getMethodName();

		TestInitializationUtils utils=new TestInitializationUtils();
		BasePage.storeKeyValue("flow", "first");

		AndroidDriver<MobileElement> driver=utils.getDriver();

		LoginData data=LoginData.fetch(testCaseName);
		HomePage homePage=new HomePage();
		homePage
		.signIn(data.emailid, data.password)
		;
		
		ModeDetails mode=ModeDetails.fetch(testCaseName);
		DeviceControlPage device=new DeviceControlPage();
		device
		.clickOnAddedDevice()
		.selectMode(mode.modeName)
		.increaseFanSpeed()
		.decreaseFanSpeed()
		.clickOnDetails()
		.clickOnValueInDetailsWidget(mode.LabelToDisplayInMode)
		.verifyFirmwareDetails()
		
		;
		homePage
		.goBack(3)
		.signOut()
		;
		
		TestNGUtils.reportLog("Reading bluetooth scan file. Output in console");
		File io=new File("resources\\BLEScan.txt");
		BufferedReader read=new BufferedReader(new FileReader(io));
		String st="";
		while((st=read.readLine())!=null) {
			System.out.println(st);
			
		}
		TestInitializationUtils utilsweb=new TestInitializationUtils();
		BasePage.storeKeyValue("flow", "second");
		WebDriver webdriver=utilsweb.getDriverForWeb();
		webdriver.manage().window().maximize();
		
		LoginPage ifastLogin=new LoginPage();
		ifastLogin
		.loginToiFast("Amway", "Amway");
		DashboardPage dashPage=new DashboardPage();
		dashPage
		.goToTestCasePage("AMWAYTESTAPI", "API TEST")
		;
		TestCasesPage testPage=new TestCasesPage();
		testPage
		.clickOnRunButtonInTestCasePage();
		RunAndExecutionPage runPage=new RunAndExecutionPage();
		runPage
		.clickOnRun()
		.clickOnNoInSaveReports()
		.getiFastReportDetails()
		;
		}
		
	
}

