package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.testng.Reporter;

public class TestNGUtils {

	
	public static void reportLog(String message)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
		Calendar cal = Calendar.getInstance();
		String Timestamp = dateFormat.format(cal.getTime());
		message =Timestamp+" : "+message;
		Reporter.log(message,true);
	}
}
