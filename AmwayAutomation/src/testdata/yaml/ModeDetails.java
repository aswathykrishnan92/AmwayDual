package testdata.yaml;

import utils.YamlUtils;

public class ModeDetails {
	public String modeName;
	public String LabelToDisplayInMode;
	
	public static ModeDetails fetch(String key) {
		ModeDetails data=(ModeDetails)YamlUtils.loadYaml(key, System.getProperty("user.dir")+"/resources/Mode.yaml");
		if(data!=null) {
			return data;
		}else {
			return null;
		}
}
}
