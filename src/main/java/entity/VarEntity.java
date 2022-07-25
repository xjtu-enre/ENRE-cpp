package entity;

import entityType.Type;

public class VarEntity extends Entity{
	protected Type type = null;
	protected String value = null;
	protected boolean isReference = false;
	protected boolean isPoint = false;
	
	public VarEntity(String name, String qualifiedName,  Entity parent, Integer id, Location location, String type) {
		super(name,qualifiedName, parent, id, location);
		this.type = new Type(type);
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {return this.type;}

	public void setPoint(boolean isPoint) {
		this.isPoint = isPoint;
	}
	public boolean equals(VarEntity var){
		if(var.type.equals(this.type)) return true;
		return false;
	}

}
