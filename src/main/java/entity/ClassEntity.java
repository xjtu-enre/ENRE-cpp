package entity;

import relation.ScopeRelation;
import symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class ClassEntity extends DataAggregateEntity{
	List<ScopeRelation> friendClass;
	List<ScopeRelation> friendFunction;

	boolean isAbstract = false;
	public ClassEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name,qualifiedName,  parent, id, scope, location);
		this.friendClass = new ArrayList<ScopeRelation>();
		this.friendFunction = new ArrayList<ScopeRelation>();
	}
	public void addFriendClass(ScopeRelation classentity) {
		friendClass.add(classentity);
	}
	public List<ScopeRelation> getFriendClass(){
		return friendClass;
	}
	public void addFriendFunction(ScopeRelation functionentity) {
		friendFunction.add(functionentity);
	}
	public List<ScopeRelation> getFriendFunction(){
		return friendFunction;
	}


	public void setAbstract() { this.isAbstract = true; }
	public boolean isAbstract() { return this.isAbstract; }
}
