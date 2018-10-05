package core;

import java.util.HashMap;

public class BasePage {
	static HashMap<String, Object> hashMap=new HashMap<String, Object>();

	public Action getAction(){
		return DriverBase.getThread().getAction();
		
	}
	public WebAction getWebAction(){
		return DriverBase.getThread().getWebAction();
		
	}
	/**
	 * @author Aswathy_Krishnan
	 * Description: Store a value with key
	 * @return
	 * @throws Exception
	 */
	public static synchronized void storeKeyValue(String key,Object value) {
		hashMap.put(key, value);
	}
	
	/**
	 * @author Aswathy_Krishnan
	 * Description: Retrieve value with Key
	 * @return
	 * @throws Exception
	 */
	public static synchronized Object retrieveKeyValue(String key) {
		return hashMap.get(key);
	}
	
}
