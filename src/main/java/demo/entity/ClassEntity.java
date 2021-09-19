package demo.entity;

import java.util.ArrayList;
import java.util.List;

import org.antlr.symtab.BaseScope;


public class ClassEntity extends DataAggregateEntity{
	List<String> baseClass;
	boolean isTemplate = false;
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
	
	public void setTemplate() {
		this.isTemplate = true;
	}
	public boolean isTemplate() {
		return this.isTemplate;
	}
}
