import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class sorting {



    public static void main(String g[]){
        // right order
        List <String> abc = new ArrayList<>();
        abc.add("b=F"); abc.add("a=F"); abc.add("c=T");

        //sort abc on first character
        Collections.sort(abc, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) - o2.charAt(0);
            }
        });
        System.out.println(abc);

        //sort abc on first character

    }
}
