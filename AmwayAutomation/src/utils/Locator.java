package utils;

import org.openqa.selenium.By;

public class Locator {
	public String key;
	public String value;
	public Type type;

	public enum Type{
		XPATH,ID,CSS,CLASSNAME

	}
	public Locator(String key,String value,Type type) {
		this.key=key;
		this.value=value;
		this.type=type;

	}
	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public Type getType() {
		return this.type;
	}

			public Locator format(String... formatValue) {
				String tmpLoc = this.getValue();
				String tmpName=this.getKey();
				for (int i = 0; i < formatValue.length; i++) {
					tmpLoc = tmpLoc.replace(String.format("{%s}", i), formatValue[i]);
					tmpName = tmpName.replace(String.format("{%s}", i), formatValue[i]);
				}
				return new Locator(tmpName,tmpLoc, this.getType());
			} 

				
	public static By getByObject(Locator loc) {
		By by;
		String locValue=loc.getValue();
		switch(loc.getType().toString()) {
		case("XPATH"):
			by=By.xpath(locValue);
		break;

		case("ID"):
			by=By.id(locValue);
		break;

		case("CLASSNAME"):
			by=By.className(locValue);
		break;

		case("CSS"):
			by=By.cssSelector(locValue);
		break;
		default:
			by=null;


			

		}
		return by;
	}
}
