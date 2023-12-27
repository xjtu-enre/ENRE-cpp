package cdt;

import entity.*;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.osgi.container.SystemModule;
import relation.Relation;
import relation.RelationRepo;
import symtab.DataAggregateSymbol;
import symtab.GlobalScope;
import symtab.Scope;
import symtab.Symbol;
import util.BuiltInDeal;
import util.Tuple;

import java.util.*;

public class TypeBinding {
    EntityRepo entityRepo;
    int entityRepoSize;
    private BuiltInDeal builtInDeal;
    int i = 0;

    public TypeBinding(EntityRepo entityrepo) {
        this.entityRepo = entityrepo;
        entityRepoSize = entityrepo.generateId();
        this.builtInDeal = new BuiltInDeal();
    }

    public void typeBindingDeal() {
        Set<String> record = new HashSet<String>();
        Iterator<Entity> iterator = entityRepo.entityIterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if(entity instanceof VarEntity){
                if(((VarEntity) entity).getTypeID() == -1){
                    int built_in_id = this.builtInDeal.DealBuiltInType(((VarEntity) entity).getType());
                    if(built_in_id != -1){
                        ((VarEntity) entity).setTypeID(built_in_id);
                        continue;
                    }
                    Scope scope = entity.getScope();
                    if(scope == null){
                        if(entity.getParent() == null) continue;
                        scope = entity.getParent().getScope();
                    }
                    Entity typedEntity = findTheTypedEntity(((VarEntity) entity).getType(), scope);
                    if(typedEntity != null){
                        ((VarEntity) entity).setTypeID(typedEntity.getId());
                    }
                    else{
                        Entity fileEntity = entity;
                        while(fileEntity!=null){
                            if(fileEntity instanceof FileEntity) break;
                            fileEntity = fileEntity.getParent();
                        }
                        String name = ((VarEntity) entity).getType();
                        if(fileEntity instanceof FileEntity){
                            for(FileEntity includeFile: ((FileEntity) fileEntity).getIncludeEntity()){
                                typedEntity = findTheTypedEntity(((VarEntity) entity).getType(), includeFile.getScope());
                                if(typedEntity != null){
                                    ((VarEntity) entity).setTypeID(typedEntity.getId());
                                    continue;
                                }
                            }
                        }
                        i++;
                        record.add(((VarEntity) entity).getType());
                    }
                }
            }
        }
        System.out.println("Type with no detected type: ");
        System.out.println(record);
        System.out.println("Number of types not detected: " + String.valueOf(record.size()));
        System.out.println("Number of entities with no type detected: " + String.valueOf(i));
    }


    public Entity findTheTypedEntity(String name, Scope current){
        if(name == null) return null;
        String[] scopeManages = name.split("::");
        if(scopeManages.length == 1){
            do{
                if(Objects.equals(current.getName(), scopeManages[0])){
                    if(current instanceof Symbol)
                        return entityRepo.getEntity(((Symbol) current).getEntityID());
                }
                if(current.getSymbol(scopeManages[0]) != null){
                    if(current.getSymbol(scopeManages[0]) instanceof DataAggregateSymbol){
                        return entityRepo.getEntity(current.getSymbol(scopeManages[0]).getEntityID());
                    }
                }
                if(current.getEnclosingScope() == null) return null;
                if(current.getEnclosingScope() != null)
                    if(current.getEnclosingScope() == current) return null;
                    current = current.getEnclosingScope();
            }while( current != null);
        }
        else if(scopeManages.length == 2){
            do{
                if(current.getSymbol(scopeManages[0]) != null){
                    if(current.getSymbol(scopeManages[0]) instanceof Scope){
                        current = (Scope) current.getSymbol(scopeManages[0]);
                        if(current.getSymbol(scopeManages[1]) != null){
                            if(current.getSymbol(scopeManages[1]) instanceof DataAggregateSymbol){
                                return entityRepo.getEntity(current.getSymbol(scopeManages[1]).getEntityID());
                            }
                        }
                    }
                    break;
                }
                if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
                if(current.getEnclosingScope() == current) return null;
            }while(current.getEnclosingScope() != null);
        }
        return null;
    }

    /**
     *
     */
    public void dealExternRelation(ArrayList<VarEntity> extern_var, ArrayList<FunctionEntity> extern_func){
        for(VarEntity var: extern_var){
            Entity en = entityRepo.getEntityByName(var.getName());
            if(en != null){
                var.setExternalId(en.getId());
            }
        }
        for(FunctionEntity func: extern_func){
            Entity en = entityRepo.getEntityByName(func.getName());
            if(en != null){
                func.setExternalId(en.getExternalId());
            }
        }
    }
}
