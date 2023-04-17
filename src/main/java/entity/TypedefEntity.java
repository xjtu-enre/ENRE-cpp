package entity;

public class TypedefEntity extends Entity{
	String type;
	public TypedefEntity(String name, String qualifiedName, Entity parent, Integer id, Location location) {
		super(name, qualifiedName, parent, id, location);
		// TODO Auto-generated constructor stub
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return this.type;
	}
}
