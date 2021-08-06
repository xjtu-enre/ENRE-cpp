package demo.entity;

import org.antlr.symtab.BaseScope;


public class UnionEntity extends DataAggregateEntity{
	public UnionEntity(String name, String qualifiedName , Entity parent, Integer id, BaseScope scope) {
		super(name, qualifiedName, parent, id, scope);
		
	}
}
