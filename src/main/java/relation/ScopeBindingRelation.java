package relation;

import entity.Entity;

public class ScopeBindingRelation extends BindingRelation{
    Entity dataAggregateEntity;
    public ScopeBindingRelation(String retype, String fromInfo, String toInfo, Integer fileID, Integer line, Integer Offset, Entity dataAggregateEntity) {
        super(retype, fromInfo, toInfo, fileID, line, Offset);
        this.dataAggregateEntity = dataAggregateEntity;
    }

    public Entity getDataAggregateEntity(){
        return this.dataAggregateEntity;
    }
}
