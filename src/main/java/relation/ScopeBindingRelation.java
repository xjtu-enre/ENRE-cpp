package relation;

import entity.Entity;

public class ScopeBindingRelation extends BindingRelation{
    Entity dataAggregateEntity;
    public ScopeBindingRelation(int retype, String fromInfo, String toInfo, Integer fileID, Integer line, Integer Offset, Entity dataAggregateEntity) {
        super(retype, fromInfo, toInfo, fileID, line, Offset);
        this.dataAggregateEntity = dataAggregateEntity;
    }
    public ScopeBindingRelation(int retype, String fromInfo, String toInfo, Integer fileID, Integer line, Integer Offset, Entity dataAggregateEntity, Integer parameterIndex){
        super(retype, fromInfo, toInfo, fileID, line, Offset);
        this.dataAggregateEntity = dataAggregateEntity;
        this.parameterIndex = parameterIndex;
    }

    public Entity getDataAggregateEntity(){
        return this.dataAggregateEntity;
    }
}
