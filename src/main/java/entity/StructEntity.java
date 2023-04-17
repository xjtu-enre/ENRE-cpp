package entity;

import symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class StructEntity extends DataAggregateEntity{
	public StructEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
	}

	
}
