package entity;

import symtab.Scope;

public class ParameterEntity extends VarEntity{
    private int index = -1;
    public ParameterEntity(String name, String qualifiedName, Entity parent, Integer id, Location location, String type) {
        super(name, qualifiedName, parent, id, location, type);
    }

    public void setParent(Entity parent){this.parent = parent;}
    public void setQualifiedName(String qualifiedName){this.qualifiedName = qualifiedName;}

    public void setIndex(int index) {this.index = index; }
    public int getIndex(){ return this.index;}
}
