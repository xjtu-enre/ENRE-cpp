package demo.util;

public class Tuple<A, B> {

    public final A first;

    public final B second;

    public Tuple(A a, B b){
        first = a;
        second = b;
    }
    public A getFirst() {
    	return this.first;
    }
    public B getSecond() {
    	return this.second;
    }
    public String toString(){
        return "(" + first + ", " + second + ")";
    }

}