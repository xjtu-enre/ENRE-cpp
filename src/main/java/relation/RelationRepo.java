package relation;

import util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelationRepo {
	
	List<Relation> dep = new ArrayList<Relation>();
	private List<BindingRelation> RelationListByBinding = new ArrayList<BindingRelation>();
	private List<ScopeBindingRelation> RelationListByScopeBinding = new ArrayList<ScopeBindingRelation>();
	public RelationRepo() {

	}

	public void addScopeBindingRelation(ScopeBindingRelation relation){
		RelationListByScopeBinding.add(relation);
	}

	public List<ScopeBindingRelation> getScopeBindingList(){
		return RelationListByScopeBinding;
	}

	public void addBindingRelation(BindingRelation relation){
		RelationListByBinding.add(relation);
	}

	public List<BindingRelation> getBindingList(){
		return RelationListByBinding;
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
