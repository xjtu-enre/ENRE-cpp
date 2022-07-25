package entityType;

public class Type {
	String typeName;
	public Type(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {return this.typeName;}
	public boolean equals(Type type){
		if(type.getTypeName() == this.typeName) return true;
		return false;
	}
}
