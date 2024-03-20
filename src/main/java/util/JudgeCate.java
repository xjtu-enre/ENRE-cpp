package util;

import entity.*;

import java.util.ArrayList;

public class JudgeCate {

    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private static JudgeCate judgeCateInstance = new JudgeCate();


    public static JudgeCate getJudgeCateInstance() {
        return judgeCateInstance;
    }
    public void clear(){
        this.entities.clear();
    }
    public boolean isAlias (Entity en){
        if(en.getId() == -1) {
            return false;
        }
        return en instanceof AliasEntity;
    }

    public boolean isTemplate (Entity en){
        if(en.getId() == -1) {
            return false;
        }
        return en.getIsTemplate();
    }
    public boolean isBlock (Entity en){
        if(en.getId() == -1) {
            return false;
        }
        return en instanceof BlockEntity;
    }

    public boolean isClass (Entity en){
        if(en.getId() == -1) {
            return false;
        }
        return en instanceof ClassEntity;
    }

    public boolean isAggregate (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof DataAggregateEntity;
    }

    public boolean isEnum (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof EnumEntity;
    }

    public boolean isEnumerator (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof EnumeratorEntity;
    }

    public boolean isField (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof FieldEntity;
    }

    public boolean isFile (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof FileEntity;
    }

    public boolean isFunction (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof FunctionEntity;
    }

    public boolean isLabel (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof LabelEntity;
    }

    public boolean isLambda (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof LambdaFunctionEntity;
    }

    public boolean isMacro (Entity en){
        //System.out.println("en.getId"+ en.getId());
        if(en.getId() == -1){
            return false;
        }
        return en instanceof MacroEntity;
    }

    public boolean isMulti (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof MultiDeclareEntity;
    }

    public boolean isNamespaceAlias (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof NamespaceAliasEntity;
    }

    public boolean isNamespace (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof NamespaceEntity;
    }
    public boolean isParameter (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof ParameterEntity;
    }

    public boolean isStruct (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof StructEntity;
    }

//    public boolean isTypedef (Entity en){
//        if(en.getId() == -1){
//            return false;
//        }
//        return en instanceof TypedefEntity;
//    }
public boolean isTypedef(Entity en) {
    if (en.getId() == -1) {
        return false;
    }
    if (en instanceof TypedefEntity) {
        return true;
    }
    else
        return false;
}

    public boolean isUnion (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof UnionEntity;
    }

    public boolean isVar (Entity en){
        if(en.getId() == -1){
            return false;
        }
        return en instanceof VarEntity;
    }

    public Entity getEntityById(int id) {
        return entities.get(id);
    }

}
