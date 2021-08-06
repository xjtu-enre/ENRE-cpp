package demo.entity;

public class Pointer extends VarEntity{
	
	boolean functionPoiner = false;
	public Pointer(String name, String qualifiedName, String type, Entity parent, Integer id) {
		super(name, qualifiedName, type, parent, id);
	}
	
	public void setFunctionPointer(boolean flag) {
		this.functionPoiner = flag;
	}
}
