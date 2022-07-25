package symtab;

import entity.DataAggregateEntity;
import entity.Entity;
import entity.Location;

public class BlockScope extends DataAggregateEntity {

    public BlockScope(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
        super(name, qualifiedName, parent, id, scope, location);
    }
}
