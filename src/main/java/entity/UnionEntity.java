package entity;

import symtab.BaseScope;


public class UnionEntity extends DataAggregateEntity{
	public UnionEntity(String name, String qualifiedName , Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		
	}
}
