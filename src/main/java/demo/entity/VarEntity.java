package demo.entity;

import demo.entityType.Type;

public class VarEntity extends Entity{
	protected Type type = null;
	protected String value = null;
	protected Entity typeEntity;
	protected boolean isReference = false;
	
	public VarEntity(String name, String qualifiedName,  Entity parent, Integer id) {
		super(name,qualifiedName, parent, id);
	}
	public void setTypeEntity(Entity type) {
		this.typeEntity = type;
	}


}
