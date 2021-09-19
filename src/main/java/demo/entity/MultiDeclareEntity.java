package demo.entity;

import org.antlr.symtab.BaseScope;

public class MultiDeclareEntity extends DataAggregateEntity{

	public MultiDeclareEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
	}

}
