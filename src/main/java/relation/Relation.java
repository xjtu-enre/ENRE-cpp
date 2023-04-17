package relation;

import entity.Entity;
import entity.Location;

public class Relation {
	Entity fromEntity;
	Entity toEntity;
	String type;
	Integer startLine;
	Integer startOffset;
	Integer fileID;

	public Relation(Entity fromEntity, Entity toEntity, String type) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
	}

	public Relation(Entity fromEntity, Entity toEntity, String type, Integer fileID, Integer line, Integer Offset) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
		this.fileID = fileID;
		this.startLine = line;
		this.startOffset = Offset;
	}
	@Override
	public String toString() {
		return "Realtion [fromEntity = " + fromEntity.getQualifiedName() + ", toEntity = " + toEntity.getQualifiedName() +
				", type = " + type +"]";
	}
	public Entity getFromEntity() {
		return this.fromEntity;
	}
	public Entity getToEntity() {
		return this.toEntity;
	}
	public String getType() {
		return this.type;
	}
	public Integer getFileID() {return this.fileID;}
	public Integer getStartLine() {return this.startLine; }
	public Integer getStartOffset() {return this.startOffset; }
}
