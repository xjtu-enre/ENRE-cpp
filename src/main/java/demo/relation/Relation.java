package demo.relation;

import demo.entity.Entity;

public class Relation {
	Entity fromEntity;
	Entity toEntity;
	String type;
	public Relation(Entity fromEntity, Entity toEntity, String type) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
	}
	@Override
	public String toString() {
		return "Realtion [fromEntity = " + fromEntity.getQualifiedName() + ", toEntity = " + toEntity.getQualifiedName() +
				", type = " + type +"]";
	}
	public Entity getFromEntity() {
		return fromEntity;
	}
	public Entity getToEntity() {
		return toEntity;
	}
	public String getType() {
		return type;
	}
}
