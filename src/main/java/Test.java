import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        int n = 1;
        for (int i=0; i<3; i=i+1) {
            n=2*n;
        }
        for (int i=0; i<3; i=i+1) {
            n=2*n;
        }
        System.out.println(n);
        int a;
        int b;
        int c;
        int d;
        int e;
        a = 1;
        b = 3;
        c = 5;
        d = 7;
        e = 9;
        int z = -3;
        int z1 = 6;
        System.out.println("A: " + (a + b * ( c - d * e )));
        System.out.println("B: " + (- a + b * ( c - d * e )));
        System.out.println("C: " + (a * ( b * c - d * e )));
        System.out.println("D: " + (a * b * c - d * e));
        System.out.println("E: " + (-z-z1));
    }



}
