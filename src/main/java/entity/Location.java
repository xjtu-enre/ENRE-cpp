package entity;

import java.io.Serializable;

public class Location implements Serializable{
	
	Integer length;
	Integer startLine;
	Integer endLine;
	Integer startOffset;
	
	Integer fileID;
	public Location(Integer fileID) {
		this.fileID = fileID;
	}
	public Location(int length, int startLine, int endLine, int startOffset, Integer fileID) {
		this.length = length;
		this.startLine = startLine;
		this.endLine = endLine;
		this.startOffset = startOffset;
		this.fileID = fileID;
	}
	public Integer getStartLine(){
        return startLine;
    }
    public void setStartLine(int endLine){
        this.endLine = endLine;
    }
    public Integer getEndLine(){
        return startLine;
    }
    public void setEndLine(int endLine){
        this.endLine = endLine;
    }
    public Integer getStartOffset(){
        return startOffset;
    }
    public Integer getFile() {
    	return this.fileID;
    }
    public Integer getEndOffset(){
        return startOffset;
    }

}
