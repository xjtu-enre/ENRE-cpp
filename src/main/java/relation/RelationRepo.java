package relation;

import util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelationRepo {
	
	List<Relation> dep = new ArrayList<Relation>();
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
		this.dep.add(re);
	}

	public List<Relation> getrelationrepo(){
		return dep;
	}

}
