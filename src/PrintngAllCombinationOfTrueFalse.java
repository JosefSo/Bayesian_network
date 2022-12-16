import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintngAllCombinationOfTrueFalse {
    public static void printCombination(int num) {
        int permuteLen=(int) Math.pow(2,num);
        boolean b[]=new boolean[num];
        for(int i=0; i<b.length; i++)
            b[i]=true;

        for(int j=0;j<permuteLen;j++){
            for( int i=0;i<num;i++)
                System.out.print("  "+b[i]+"  ");
            System.out.println(" ");

            for(int i=num-1;i>=0;i--){
                if(b[i]==true ){
                    b[i]=false;
                    break;
                }
                else
                    b[i]=true;
            }
        }
    }

    public static List<List<String>> printCombination2(int num) {
        List<List<String>> myList = new ArrayList<>();
        int permuteLen=(int) Math.pow(2,num);
        //boolean b[]=new boolean[num];
        String[] b = new String[num];
        Arrays.fill(b, "True");

        for(int j=0;j<permuteLen;j++){
            List<String> temp = new ArrayList<>();
            for( int i=0;i<num;i++){
                System.out.print("  "+b[i]+"  ");
                temp.add(b[i]);
            }
            System.out.println(" ");
            myList.add(temp);

            for(int i=num-1;i>=0;i--){
                if(b[i].equals("True")){
                    b[i]="False";
                    break;
                }
                else
                    b[i]="True";
            }
        }
        return myList;
    }
    public static void main(String g[]){
        //printCombination(3);
        List<List<String>> myList = printCombination2(3);
        System.out.println(myList);
        System.out.println(myList.get(0).get(0));

    }
}
