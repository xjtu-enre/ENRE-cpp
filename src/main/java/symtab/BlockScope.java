package symtab;

import entity.DataAggregateEntity;
import entity.Entity;
import entity.Location;

public class BlockScope extends DataAggregateSymbol {

    public BlockScope(String name, Integer id) {
        super(name, id);
    }
}
