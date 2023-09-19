package relation;

public class BindingRelation {
    String RelationType;
    String fromEntityInfo;
    String toEntityInfo;
    Integer startLine;
    Integer startOffset;
    Integer fileID;
    public BindingRelation(String retype,  String eninfor, Integer fileID, Integer line, Integer Offset) {
        this.RelationType = retype;
        this.toEntityInfo = eninfor;
        this.fileID = fileID;
        this.startLine = line;
        this.startOffset = Offset;
    }
    public BindingRelation(String retype,  String fromEntityInfo, String toEntityInfo, Integer fileID, Integer line, Integer Offset) {
        this.RelationType = retype;
        this.fromEntityInfo = fromEntityInfo;
        this.toEntityInfo = toEntityInfo;
        this.fileID = fileID;
        this.startLine = line;
        this.startOffset = Offset;
    }
    public String getLocationInfor() {
        return this.toEntityInfo;
    }
    public String getFromEntityInfo(){
        return this.fromEntityInfo;
    }
    public String getRelationType() {return this.RelationType;}
    public Integer getFileID() {return this.fileID;}
    public Integer getStartLine() {return this.startLine; }
    public Integer getStartOffset() {return this.startOffset; }
}