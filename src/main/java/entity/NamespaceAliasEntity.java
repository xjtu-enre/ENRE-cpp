package entity;

public class NamespaceAliasEntity extends AliasEntity{
    public String toNamespaceName = null;
    public NamespaceEntity toNamespaceEntity = null;

    public NamespaceAliasEntity(String simpleName, String qualifiedName, Entity parent, Integer id, String originTypeName, Location location) {
        super(simpleName, qualifiedName, parent, id, originTypeName, location);
    }

    public void setToNamespaceName(String namespaceName){
        this.toNamespaceName = namespaceName;
    }
    public String getToNamespaceName(){
        return this.toNamespaceName;
    }

    public void setToNamespaceEntity(NamespaceEntity namespaceEntity){
        this.toNamespaceEntity = namespaceEntity;
    }
    public NamespaceEntity getToNamespaceEntity(){
        return this.toNamespaceEntity;
    }

}
