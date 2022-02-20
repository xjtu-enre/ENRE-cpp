package entity;

import org.antlr.symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class StructEntity extends DataAggregateEntity{
	List<String> baseStruct;
	public StructEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		this.baseStruct = new ArrayList<String>();	
	}
	
	public void addBaseStruct(List<String> Structentity) {
		baseStruct.addAll(Structentity);
	}
	public List<String> getBaseStruct(){
		return baseStruct;
	}
	
}
