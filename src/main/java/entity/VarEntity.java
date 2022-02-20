package entity;

import entityType.Type;

public class VarEntity extends Entity{
	protected Type type = null;
	protected String value = null;
	protected Entity typeEntity;
	protected boolean isReference = false;
	
	public VarEntity(String name, String qualifiedName,  Entity parent, Integer id, Location location) {
		super(name,qualifiedName, parent, id, location);
	}
	public void setTypeEntity(Entity type) {
		this.typeEntity = type;
	}


}
