package entity;

import symtab.BaseScope;

public class LambdaFunctionEntity extends FunctionEntity{
    String Capture = "UNSPECIFIED";
    public LambdaFunctionEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
        super(name, qualifiedName, parent, id, scope, location);
    }
    void setCapture(String Capture){
        this.Capture = Capture;
    }
}
