package entity;

public class TypeRecord {
    String typeName;
    int entityID;
    public TypeRecord(String typeName) {
        this.typeName = typeName;
    }
    public TypeRecord(int entityID) {
        this.entityID = entityID;
    }

    public String getTypeName() {
        return this.typeName;
    }
    public int getEntityID(){
        return this.entityID;
    }
    public boolean equals(TypeRecord type){
        if(type.getTypeName() == this.typeName) return true;
        if(type.getEntityID() == this.entityID) return true;
        return false;
    }
}
