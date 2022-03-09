package entity;

import org.antlr.symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class ClassEntity extends DataAggregateEntity{
	List<String> baseClass;
	public ClassEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name,qualifiedName,  parent, id, scope, location);
		this.baseClass = new ArrayList<String>(); 
	}
	public void addBaseClass(List<String> classentity) {
		baseClass.addAll(classentity);
	}
	public List<String> getBaseClass(){
		return baseClass;
	}

}
