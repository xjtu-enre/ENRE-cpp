package entity;

import entity.TypeRecord;

public class VarEntity extends Entity{
	protected TypeRecord type = null;
	protected String value = null;
	protected boolean isReference = false;
	protected boolean isPoint = false;
	
	public VarEntity(String name, String qualifiedName,  Entity parent, Integer id, Location location, String type) {
		super(name,qualifiedName, parent, id, location);
		this.type = new TypeRecord(type);
	}
	public void setType(TypeRecord type) {
		this.type = type;
	}
	public TypeRecord getType() {return this.type;}

	public void setPoint(boolean isPoint) {
		this.isPoint = isPoint;
	}
	public boolean equals(VarEntity var){
		if(var.type.equals(this.type)) return true;
		return false;
	}

}
