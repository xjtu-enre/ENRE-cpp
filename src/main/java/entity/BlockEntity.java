package entity;

import symtab.BaseScope;

public class BlockEntity extends DataAggregateEntity{

    public BlockEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
        super(name, qualifiedName, parent, id, scope, location);
    }
}
