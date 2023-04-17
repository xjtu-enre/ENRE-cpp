package macro;

import java.util.HashMap;
import java.util.Map;

public class MacroRepo {
	private Map<String, String> MacroMap = new HashMap<>();
	
	public void add(String key,String value) {
		this.MacroMap.put(key, value);
	}
	
	public Map<String, String> getDefaultMap() {
		return MacroMap;
	}

}
