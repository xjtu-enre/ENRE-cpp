package util;

import entity.*;
import relation.*;
//import entity.properties.Location;
//import entity.properties.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
public class TestUtil {

//    public static List<RelationObj> getRelations(Map<Integer,ArrayList<Tuple<Integer, Relation>>> relationMap){
//        List<RelationObj> res = new ArrayList<>();
//        for(Map.Entry<Integer, ArrayList<Tuple<Integer, Relation>>> entry: relationMap.entrySet()){
//            Integer fromEntityId = entry.getKey();
//            ArrayList<Tuple<Integer,Relation>> toEntities = entry.getValue();
//            for (Tuple<Integer, Relation> toEntityTuple : toEntities){
//                Integer toEntityId = toEntityTuple.getFirst();
//                Relation relation = toEntityTuple.getSecond();
//                res.add(new RelationObj(fromEntityId, toEntityId, relation));
//            }
//        }
//        return res;
//    }

    public static <T> List<T> filter(List<T> list, Predicate<T> filter) {
        List<T> res = new ArrayList<>();
        for (T item : list) {
            if (filter.test(item)) {
                res.add(item);
            }
        }
        return res;
    }

    public static int[] expandLocationArray(Location loc, int[] gt) {
        int[] res = new int[]{
                loc.getStartLine(),
                loc.getStartOffset(),
                loc.getEndLine(),
                loc.getStartOffset(),
        };
        for (int i = 0; i < gt.length; i++) {
            if (gt[i] == -1) {
                res[i] = -1;
            }
        }
        return res;
    }

    public static void printEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            System.out.println(entity.getClass().getSimpleName() + ", " + entity.getQualifiedName() + ", " + entity.getLocation().getStartLine());
        }
    }

    public static void printRelations(List<RelationObj> relations) {
        for (RelationObj relation : relations) {
            System.out.println(relation.getRelation().getType() + ", " + relation.getRelation().getStartLine());
        }
    }
}
