package entity;

import org.antlr.symtab.BaseScope;

public class FunctionTemplateEntity extends FunctionEntity{

	public FunctionTemplateEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope,
			Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		// TODO Auto-generated constructor stub
	}

}
