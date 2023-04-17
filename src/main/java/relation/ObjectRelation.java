package relation;

import entity.Entity;

public class ObjectRelation {
    Entity fromEntity;
    String object;
    String toEntity;
    String type;
    Integer startLine;
    Integer startOffset;
    Integer fileID;
    public ObjectRelation(String object, Entity fromEntity, String toEntity, String type, Integer fileID, Integer line, Integer Offset) {
        this.object = object;
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
        this.type = type;
        this.fileID = fileID;
        this.startLine = line;
        this.startOffset = Offset;
    }

    public String getObject(){return this.object; }
    public Entity getFromEntity() {
        return this.fromEntity;
    }
    public String getToEntity() {
        return this.toEntity;
    }
    public String getType() {
        return this.type;
    }
    public Integer getFileID() {return this.fileID;}
    public Integer getStartLine() {return this.startLine; }
    public Integer getStartOffset() {return this.startOffset; }
}
