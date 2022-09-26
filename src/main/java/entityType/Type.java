package entityType;

public class Type {
	String typeName;
	int entityID;
	public Type(String typeName) {
		this.typeName = typeName;
	}
	public Type(int entityID) {
		this.entityID = entityID;
	}

	public String getTypeName() {
		return this.typeName;
	}
	public int getEntityID(){
		return this.entityID;
	}
	public boolean equals(Type type){
		if(type.getTypeName() == this.typeName) return true;
		if(type.getEntityID() == this.entityID) return true;
		return false;
	}
}
