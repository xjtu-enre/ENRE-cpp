package demo.entity;

public class VarEntity extends Entity{
	protected String type;
	protected Entity typeEntity;
	protected boolean isReference = false;
	
	public VarEntity(String name, String qualifiedName, String type, Entity parent, Integer id) {
		super(name,qualifiedName, parent, id);
		this.type = type;
	}
	public void setTypeEntity(Entity type) {
		this.typeEntity = type;
	}
	public void setReference(boolean flag) {
		this.isReference = flag;
	}
	public boolean getReference(boolean flag) {
		return this.isReference;
	}

}
