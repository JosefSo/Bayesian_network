import java.util.*;
import java.math.*;

public class Main {


    public static void main(String[] args) {
        System.out.println("Hello world!");
        int length = 2;
        for (BigInteger i = BigInteger.ZERO ; ! i.testBit(length) ; i = i.add(BigInteger.ONE)) {
            for (int j = length - 1 ; j >= 0 ; j--) {
                System.out.print(Boolean.valueOf( i.testBit(j) ).toString() + " ");
            }
            System.out.println();
        }
        // ---------------------------------------------- ----------------------------------------------

//        int permutations = 2;
//        int l = 2;
//        for( long i = 0; i < permutations; i++ ) {
//            printBinary( i, l );
//        }

        // ---------------------------------------------- ----------------------------------------------
//        List<String> values = new ArrayList<>();
//        int l = 2;
//        for (int i = l - 1; i >= 0; i--)
//            System.out.print(values[i]);
//
//        System.out.println();
//
//        for (int i = 0 ; i < l ; i++) {
//            if (values[i] == false) {
//                values[i] = true;
//                break;
//            } else {
//                values[i] = false;
//            }
//        }


        }
//    public static void printBinary(long number, int length ) {
//
//        long current = 1 << length;
//
//        while( current > 0 ) {
//            System.out.print(number & current == current ? "true " : "false " );
//            current >>> 1;
//        }
//    }
}