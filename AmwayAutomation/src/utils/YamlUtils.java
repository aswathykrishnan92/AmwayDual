package utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlUtils {

	@SuppressWarnings("rawtypes")
	public static Object loadYaml(String key, String filepath) {
		Map obj=new HashMap();
		Yaml yaml=new Yaml();
		try {
			obj=(Map)yaml.load(new FileInputStream(new File(filepath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.get(key);
	}
}
