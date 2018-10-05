package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;




public enum TestProperties {

	SELENIUM_HOST("selenium.host","",Type.String),
	SELENIUM_PORT("selenium.port","",Type.String),
	BROWSER_NAME("browser.name","",Type.String),
	BROWSER_VERSION("browser.version","",Type.String),
	THREAD_COUNT("thread.count","",Type.String),
	TESTNG_GROUP("testng.group","",Type.String),
	TEST_URL("test.url","",Type.String),
	TESTNG_PACKAGE("testng.package","",Type.String),
	APPLICATION_TYPE("application.type","",Type.String),
	APPLICATION_FLOW("application.flow","",Type.String),
	DEVICE_NAME("device.name","",Type.String),
	PLATFORM_VERSION("platform.version","",Type.String),
	PLATFORM_NAME("platform.name","",Type.String),
	APP_PATH("app.path","",Type.String),
	APP_PACKAGE("app.package","",Type.String),
	;
	
	String key;
	Object value;
	Type type;
	Properties prop;
	private enum Type{Integer,String,Boolean};

	private TestProperties(String key,Object value,Type type) {
		this.key=key;
		this.value=value;	
		this.type=type;
	}

	public void setProperty(Object val){		
		this.value=val;
	}
	
	@Override
	public String toString() {
		loadproperties();
		return this.value.toString();
	}

	public Object getProperty(){
		loadproperties();
		return this.value;
	}

public void loadproperties() {
	
	try {
		
		prop=new Properties();
		prop.load(new FileInputStream(new File(System.getProperty("user.dir")+"/resources/test.properties")));

		for(TestProperties tPrp:TestProperties.values()) {
			String valueFromFile=prop.getProperty(tPrp.key);
			if(valueFromFile!=null&&valueFromFile.contains(tPrp.key)) {
				valueFromFile=String.valueOf(tPrp.key);
			}
			if(valueFromFile!=null) {
				if(tPrp.type==Type.Integer){
					tPrp.setProperty(Integer.parseInt(valueFromFile));
				}else if(tPrp.type==Type.Boolean){
					tPrp.setProperty(Boolean.parseBoolean(valueFromFile));
				}else
					tPrp.setProperty(valueFromFile);
			}
			}
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}