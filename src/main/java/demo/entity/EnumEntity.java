package demo.entity;

import java.util.ArrayList;
import java.util.List;
import org.antlr.symtab.BaseScope;

public class EnumEntity extends DataAggregateEntity {
	List<EnumeratorEntity> enumeratorList;
	public EnumEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope) {
		super(name, qualifiedName, parent, id, scope);
	}
	
	public void addEnumerator(EnumeratorEntity enumerator) {
		if(enumeratorList==null) enumeratorList = new ArrayList<EnumeratorEntity>();
		enumeratorList.add(enumerator);
	}
}
