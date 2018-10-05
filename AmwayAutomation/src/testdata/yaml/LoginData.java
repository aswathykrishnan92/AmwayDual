package testdata.yaml;

import utils.YamlUtils;

public class LoginData {
	public String emailid;
	public String password;
	
	public static LoginData fetch(String key) {
		LoginData data=(LoginData)YamlUtils.loadYaml(key, System.getProperty("user.dir")+"/resources/Login.yaml");
		if(data!=null) {
			return data;
		}else {
			return null;
		}
}
}
