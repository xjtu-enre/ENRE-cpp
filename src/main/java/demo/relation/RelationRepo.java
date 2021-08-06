package demo.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelationRepo {
	
	Map<Integer, Map<Integer, Map<String, Integer>>> relationMap = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
	
	public RelationRepo() {
		
	}
	
	public void addRelation(Relation re) {
		String type = re.getType();
		Integer from = re.getFromEntity().getId();
		Integer to = re.getToEntity().getId();
		Map<String, Integer> reType = new HashMap<String, Integer>();
		reType.put(re.getType(), 1);
		Map<Integer,Map<String, Integer>> relation = new HashMap<Integer, Map<String, Integer>>();
		relation.put(re.getToEntity().getId(),reType);
		
		if(!relationMap.containsKey(from)) {
			relationMap.put(from, relation);
		}else {
			if(!relationMap.get(from).containsKey(to)) {
				relationMap.get(from).put(to, reType);
			}
			else {
				relationMap.get(from).get(to).put(type, 1);
			}
		}
	}

	public Map<Integer, Map<Integer, Map<String, Integer>>> getrelationrepo(){
		return relationMap;
	}

}
