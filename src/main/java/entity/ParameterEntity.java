package entity;

public class ParameterEntity extends VarEntity{
    public ParameterEntity(String name, String qualifiedName, Entity parent, Integer id, Location location, String type) {
        super(name, qualifiedName, parent, id, location, type);
    }

    public void setParent(Entity parent){this.parent = parent;}
    public void setQualifiedName(String qualifiedName){this.qualifiedName = qualifiedName;}
}
