package relation;

import util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelationRepo {
	
	Map<String, List<Tuple<Integer, Integer>>> dep = new HashMap<String, List<Tuple<Integer, Integer>>>();
	public RelationRepo() {
		
	}
	
	
	/**
	* @methodsName: addRelation
	* @description: add a relation into repo
	* @param: Relation re
	* @return: void
	* @throws: 
	*/
	public void addRelation(Relation re) {
		String type = re.getType();
		Integer from = re.getFromEntity().getId();

		Integer to = re.getToEntity().getId();
		if(!dep.containsKey(re.getType())) {
			List<Tuple<Integer,Integer>> listNew = new ArrayList<Tuple<Integer,Integer>>();
			listNew.add(new Tuple(from,to));
			this.dep.put(re.getType(), listNew);
		}
		else {
			this.dep.get(type).add(new Tuple(from,to));
		}
	}

	public Map<String, List<Tuple<Integer, Integer>>> getrelationrepo(){
		return dep;
	}

}
