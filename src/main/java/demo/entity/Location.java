package demo.entity;

import java.io.Serializable;

public class Location implements Serializable{
	
	Integer length;
	Integer startLine;
	Integer endLine;
	Integer startColumn;

	
	String filePath;
	public Location(String filePath) {
		this.filePath = filePath;
	}
	public Location(int length, int startLine, int endLine, int startColumn, String filePath) {
		this.length = length;
		this.startLine = startLine;
		this.endLine = endLine;
		this.startColumn = startColumn;
		this.filePath = filePath;
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
    public Integer getStartColumn(){
        return startColumn;
    }
    public String getFileName() {
    	return this.filePath;
    }
    public Integer getEndColumn(){
        return startColumn;
    }

}
