package demo.entity;

import java.util.ArrayList;
import java.util.List;

public class AliasEntity extends Entity{
	private String originName;
	Entity referToEntity;
	//别名， 对别名操作相当于对该变量进行操作
	public AliasEntity() {
		
	}
	public AliasEntity(String simpleName, String qualifiedName,  Entity parent, Integer id, String originTypeName, Location location) {
		super(simpleName, qualifiedName, parent, id, location);
		this.originName = originTypeName;
	}
	public void setReferToEntity(Entity referToEntity) {
		this.referToEntity = referToEntity;
	}

}
