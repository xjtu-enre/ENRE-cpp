package entity;

import org.antlr.symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;

public class EnumEntity extends DataAggregateEntity {
	List<EnumeratorEntity> enumeratorList;
	public EnumEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
	}
	
	public void addEnumerator(EnumeratorEntity enumerator) {
		if(enumeratorList==null) enumeratorList = new ArrayList<EnumeratorEntity>();
		enumeratorList.add(enumerator);
	}
}