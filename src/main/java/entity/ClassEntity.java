package entity;

import org.antlr.symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class ClassEntity extends DataAggregateEntity{
	List<String> baseClass;
	List<String> friendClass;
	List<String> friendFunction;
	List<Integer> containEntity;
	public ClassEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name,qualifiedName,  parent, id, scope, location);
		this.baseClass = new ArrayList<String>(); 
		this.friendClass = new ArrayList<String>(); 
		this.friendFunction = new ArrayList<String>();
		this.containEntity = new ArrayList<Integer>();
	}
	public void addBaseClass(List<String> classentity) {
		baseClass.addAll(classentity);
	}
	public List<String> getBaseClass(){
		return baseClass;
	}
	public void addFriendClass(String classentity) {
		friendClass.add(classentity);
	}
	public List<String> getFriendClass(){
		return friendClass;
	}
	public void addFriendFunction(String functionentity) {
		friendFunction.add(functionentity);
	}
	public List<String> getFriendFunction(){
		return friendFunction;
	}
	public void addContainEntity(Integer entityID){this.containEntity.add(entityID);}
	public List<Integer> getContainEntity() {return this.containEntity;}

}
