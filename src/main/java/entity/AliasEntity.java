package entity;

public class AliasEntity extends Entity{
	private String originName;
	Entity referToEntity;

	public AliasEntity(String simpleName, String qualifiedName,  Entity parent, Integer id, String originTypeName, Location location) {
		super(simpleName, qualifiedName, parent, id, location);
		this.originName = originTypeName;
	}
	public void setReferToEntity(Entity referToEntity) {
		this.referToEntity = referToEntity;
	}

}
