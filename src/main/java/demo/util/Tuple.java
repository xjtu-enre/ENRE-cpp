package demo.util;

public class Tuple<A, B> {

    public final A first;

    public final B second;

    public Tuple(A a, B b){
        first = a;
        second = b;
    }
    

    public String toString(){
        return "(" + first + ", " + second + ")";
    }

}