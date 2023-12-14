package relation;

import entity.Entity;
import entity.Location;

public class Relation {
	Entity fromEntity;
	Entity toEntity;
	int type;
	Integer startLine;
	Integer startOffset;
	Integer fileID;
	Integer parameterIndex=-1;

	public Relation(Entity fromEntity, Entity toEntity, int type) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
	}

	public Relation(Entity fromEntity, Entity toEntity, int type, Integer fileID, Integer line, Integer Offset) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
		this.fileID = fileID;
		this.startLine = line;
		this.startOffset = Offset;
	}

	public Relation(Entity fromEntity, Entity toEntity, int type, Integer fileID, Integer line, Integer Offset, Integer parameterIndex) {
		this.fromEntity = fromEntity;
		this.toEntity = toEntity;
		this.type = type;
		this.fileID = fileID;
		this.startLine = line;
		this.startOffset = Offset;
		this.parameterIndex = parameterIndex;
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
	public int getType() {
		return this.type;
	}
	public Integer getFileID() {return this.fileID;}
	public Integer getStartLine() {return this.startLine; }
	public Integer getStartOffset() {return this.startOffset; }
	public Integer getParameterIndex() { return this.parameterIndex; }
}
