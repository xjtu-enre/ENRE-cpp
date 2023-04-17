package entity;

import entity.TypeRecord;

public class VarEntity extends Entity{
	protected String type = null;
	protected String value = null;
	protected boolean isReference = false;
	protected boolean isPoint = false;
	/* Bind the type entity ID to the current var entity */
	protected Integer TypeID = -1;
	
	public VarEntity(String name, String qualifiedName,  Entity parent, Integer id, Location location, String type) {
		super(name,qualifiedName, parent, id, location);
		this.type = type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {return this.type;}

	public void setPoint(boolean isPoint) {
		this.isPoint = isPoint;
	}
	public boolean equals(VarEntity var){
		if(var.type.equals(this.type)) return true;
		return false;
	}

	public void setTypeID(Integer id){
		this.TypeID = id;
	}
	public Integer getTypeID(){
		return TypeID;
	}

}
