package demo.entity;

import java.io.Serializable;

public class Location implements Serializable{
	
	Integer startOfSet;
	Integer startLine;
	Integer endLine;
	Integer startColumn;
	Integer endColumn;
	
	String filePath;
	
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
    public void setStartColumn(int endColumn){
        this.endColumn = endColumn;
    }
    public Integer getEndColumn(){
        return startColumn;
    }
    public void setEndColumn(int endColumn){
        this.endColumn = endColumn;
    }
}
