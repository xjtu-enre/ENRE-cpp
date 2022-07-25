package entity;

import symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;

public class EnumEntity extends DataAggregateEntity {
	List<EnumeratorEntity> enumeratorList;
	// if this is an enum class or enum struct, isScope will be true
	boolean isScope = false;
	public EnumEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
	}
	
	public void addEnumerator(EnumeratorEntity enumerator) {
		if(enumeratorList==null) enumeratorList = new ArrayList<EnumeratorEntity>();
		enumeratorList.add(enumerator);
	}
	
	public void setScope(boolean isScope) {
		this.isScope = isScope;
	}
	
	public boolean getisScope() {
		return this.isScope;
	}
}
