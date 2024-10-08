package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnionFind {
    public HashMap<Integer, Integer> fatherMap;
    public HashMap<Integer, Integer> sizeMap;

    public UnionFind() {
        fatherMap = new HashMap<Integer, Integer>();
        sizeMap = new HashMap<Integer, Integer>();
        fatherMap.clear();
        sizeMap.clear();
    }

    public boolean isInSet(Integer node){
        Integer father = fatherMap.get(node);
        if(father == null) return false;
        return true;
    }

    private Integer findHead(Integer node) {
        Integer father = fatherMap.get(node);
        if(father == null){
            fatherMap.put(node, node);
            sizeMap.put(node, 1);
        }
        father = fatherMap.get(node);
        while (father != node) {
            father = findHead(father);
        }
        fatherMap.put(node, father);
        return father;
    }

    public boolean isSameSet(Integer aNode, Integer bNode) {
        return findHead(aNode) == findHead(bNode);
    }

    public void union(Integer aNode, Integer bNode) {
        if (aNode == null || bNode == null || aNode.equals(bNode)) {
            return;
        }
        Integer aHead = findHead(aNode);
        Integer bHead = findHead(bNode);
        if (aHead != bHead) {
            int aSize = sizeMap.get(aHead);
            int bSize = sizeMap.get(bHead);
            if (aSize <= bSize) {
                fatherMap.put(aHead, bHead);
                sizeMap.put(bHead, aSize + bSize);
            } else {
                fatherMap.put(bHead, aHead);
                sizeMap.put(aHead, aSize + bSize);
            }
        }
    }
    public List<Integer> getNodes(Integer node){
        Integer head = this.findHead(node);
        List<Integer> nodes = new ArrayList<Integer>();
        for(Integer key:fatherMap.keySet()){
            if(fatherMap.get(key) == head){
                nodes.add(key);
            }
        }
        return nodes;
    }
}