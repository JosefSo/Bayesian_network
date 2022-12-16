// AI Algorithms Yosef Sokolov 337959928

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Ex1 {

    private static int plus=0;
    private static int multiply=0;

    private static void plus(){
        plus++;
    }
    private static void multiply(){
        multiply++;
    }

    private static void setPlusMultiply(){
        plus=0;
        multiply=0;
    }
    /**
     * READ THE DATA FROM XML.
     *
     * @param  fileName   a name of a file
     * @return         variablesCollection
     */
    private static variablesCollection readXML(String fileName){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            NodeList list_var = doc.getElementsByTagName("VARIABLE");
            NodeList list_def = doc.getElementsByTagName("DEFINITION");
            variablesCollection col = new variablesCollection();

            for (int temp = 0; temp < list_var.getLength(); temp++) {
                Node node = list_var.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("NAME").item(0).getTextContent();
                    Variable x = new Variable(name);
                    NodeList list_out = element.getElementsByTagName("OUTCOME");
                    for (int i = 0; i < list_out.getLength(); i++){
                        String outcome = element.getElementsByTagName("OUTCOME").item(i).getTextContent();
                        x.addOutcome(outcome);
                    }
                    col.addToCollection(x.getName(), x);
                }
            }
            for (int temp = 0; temp < list_def.getLength(); temp++) {
                Node node = list_def.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String for_ = element.getElementsByTagName("FOR").item(0).getTextContent();
                    NodeList list_giv = element.getElementsByTagName("GIVEN");
                    for (int i = 0; i < list_giv.getLength(); i++){
                        String given = element.getElementsByTagName("GIVEN").item(i).getTextContent();
                        col.getVariable(for_).addGiven(given);
                    }
                    String cpt = element.getElementsByTagName("TABLE").item(0).getTextContent();
                    col.getVariable(for_).updateCpt(cpt , col);
                    col.getVariable(for_).updateFactor(cpt , col);
                }
            }
            return col;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return new variablesCollection();
    }
    /** * MAIN . * @param  args   a String[] args */
    public static void main(String[] args) {
        try {
            FileWriter myWriter = new FileWriter("output.txt");
            try {
                File Questions = new File("input.txt");
                Scanner myReader = new Scanner(Questions);
                boolean firstLine = true;
                String fileNameXML = "";
                while (myReader.hasNextLine()) {
                    if (firstLine){
                        fileNameXML = myReader.nextLine();
                        firstLine = false;
                    }
                    else{
                        String ans = "";
                        try{
                            String data = myReader.nextLine();
                            variablesCollection col = readXML(fileNameXML);
                            col.setChildes();
                            setPlusMultiply();


                            // CALLING THE ALGO'S
                            factorsCollection facCol = new factorsCollection();
                            col.getCollection().forEach((k,v) -> facCol.addToCollection(v.getFactor()));

                            String[] queryANDnumber = data.split("\\)");
                            queryANDnumber[1] = queryANDnumber[1].substring(1,2);
                            queryANDnumber[0] = queryANDnumber[0]+")";


                            String query = queryANDnumber[0];
                            String numOfAlgo = queryANDnumber[1];

                            switch (numOfAlgo) {
                                case "1":
                                    System.out.println("I'm in 1"); //PRINT

                                    ans = String.format("%.5g", joint_distribution(query, col, facCol));
                                    ans = ans+","+plus+","+multiply;
                                    System.out.println(ans); //PRINT
                                    break;
                                case "2":
                                    System.out.println("I'm in 2"); //PRINT


                                    break;
                                case "3":
                                    System.out.println("I'm in 3"); //PRINT

                                    break;
                            }


                            // ans = String.format("%.5f", eliminate_join(data , col , facCol));

                            // ans = joint_distribution(data, col, facCol);

                            // System.out.println("ans= " + ans); //PRINT

                            // ans = ans+","+plus+","+multiply;


                        } catch (Exception e) {
                            ans = "";
                            e.printStackTrace();
                        }
                        myWriter.write(ans);
                        if (myReader.hasNextLine()){
                            myWriter.write("\n");
                        }
                    }
                }
                myReader.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            myWriter.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static List<List<String>> allCombination(int num) {
        List<List<String>> myList = new ArrayList<>();
        int permuteLen=(int) Math.pow(2,num);
        //boolean b[]=new boolean[num];
        String[] b = new String[num];
        Arrays.fill(b, "T");

        for(int j=0;j<permuteLen;j++){
            List<String> temp = new ArrayList<>();
            for( int i=0;i<num;i++){
                temp.add(b[i]);
            }
            myList.add(temp);

            for(int i=num-1;i>=0;i--){
                if(b[i].equals("T")){
                    b[i]="F";
                    break;
                }
                else
                    b[i]="T";
            }
        }
        return myList;
    }
    /**
     * collection to HashMap of Hashmaps with all probabilities
     *
     * @param  varCol    variablesCollection
     * @return         HashMap of Hashmaps
     */
    private static  HashMap<String, HashMap<String, Double>> coll2HashMap(variablesCollection varCol) {
        HashMap<String, HashMap<String, Double>> myCollection = new HashMap<String, HashMap<String, Double>>();
        for (String key : varCol.getCollection().keySet()){ // A-B-C order
            List<factorRow> facRowLst = varCol.getVariable(key).getFactor().getRows();
            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
            for (int i=0 ; i<facRowLst.size() ; i++){
                String keyStr = "";
                for (String key2 : facRowLst.get(i).getCases().keySet()) {
                    keyStr += (key2+"="+facRowLst.get(i).getCases().get(key2)+" ");
                }
                keyStr = keyStr.substring(0,keyStr.length()-1); // remove last char: " "
                Double probability = facRowLst.get(i).getP();
                tempHashMap.put(keyStr, probability);
            }
            myCollection.put(key, tempHashMap);
        }
        return myCollection;
    }
    /**
     * Find the correct request to HashMap.
     *
     * @param varCol    variablesCollection
     * @param current   a current var we're checking on
     * @param queryTF     the current variable with outcome: T/F
     * @param evidences the query (P(B=T|J=T,M=F)): return J,M
     * @param evidenceTF the query (P(B=T|J=T,M=F)): return J=T,M=F
     * @param hidden the query (P(B=T|J=T,M=F)): return A,E
     * @param hiddenTF the query (P(B=T|J=T,M=F)): return A=T,E=T
     * @return (String) request
     */
    private static String findRequest(variablesCollection varCol, String current, String queryTF, List<String> evidences, List<String> evidenceTF, List<String> hidden, List<String> hiddenTF){
        List<String> request = new ArrayList<>();
        List<String> given = varCol.getVariable(current).getGiven();

        // checking what is current (query, evidence, hidden) and giving it right outcome: T/F

        // query
        if(current.equals(""+queryTF.charAt(0))){
            request.add(queryTF);
        }
        // hidden
        else if(hidden.contains(current)){
            int idx = 0;
            for (String h : hidden){
                if (h.equals(current)){
                    request.add(hiddenTF.get(idx));
                }
                idx++;
            }
        }
        // evidence
        else if(evidences.contains(current)){
            int idx = 0;
            for (String e : evidences){
                if (e.equals((current))){
                    request.add(evidenceTF.get(idx));
                }
                idx++;
            }
        }
        // no given
        if (given.isEmpty()){
            String ans = "";
            for (String r : request){
                ans += r += " ";
            }
            ans = ans.substring(0, ans.length()-1); // remove last char: " "
            return ans;   // for example: A=T
        }
        // there are given (one or more)
        for (String g : given){
            // given in evidence
            if(evidences.contains(g)){
                for (int i = 0; i<evidences.size(); i++){
                    if(evidences.get(i).equals(g)){
                        request.add(evidenceTF.get(i));
                    }
                }
            }
            // given in hidden
            else if(hidden.contains(g)){
                for (int i = 0; i<hidden.size(); i++){
                    if(hidden.get(i).equals(g)){
                        request.add(hiddenTF.get(i));
                    }
                }
            }
            else { // query contains given
                request.add(queryTF);
            }
        }

        //sort to A-B-B on first character
        Collections.sort(request, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) - o2.charAt(0);
            }
        });

        String ans = "";
        for (String r : request){
            ans += r += " ";
        }
        ans = ans.substring(0, ans.length()-1); // remove last char: " "
        return ans; // for example: A=T B=F C=T
    }
    /**
     * Find the correct request to HashMap.
     *
     * @param varCol    variablesCollection
     * @param current   a current var we're checking on
     * @param queryTF     the current variable with outcome: T/F
     * @param evidences the query (P(B=T|J=T,M=F)): return J,M
     * @param evidenceTF the query (P(B=T|J=T,M=F)): return J=T,M=F
     * @param hidden the query (P(B=T|J=T,M=F)): return A,E
     * @param hiddenTF the query (P(B=T|J=T,M=F)): return A=T,E=T
     * @return (String) request
     */
    private static String findRequestTernary(variablesCollection varCol, String current, String queryTF, List<String> evidences, List<String> evidenceTF, List<String> hidden, List<String> hiddenTF){
        List<String> request = new ArrayList<>();
        List<String> given = varCol.getVariable(current).getGiven();

        // checking what is current (query, evidence, hidden) and giving it right outcome: T/F

        // query
        if(current.equals(""+queryTF.charAt(0))){
            request.add(queryTF);
        }
        // hidden
        else if(hidden.contains(current)){
            int idx = 0;
            for (String h : hidden){
                if (h.equals(current)){
                    request.add(hiddenTF.get(idx));
                }
                idx++;
            }
        }
        // evidence
        else if(evidences.contains(current)){
            int idx = 0;
            for (String e : evidences){
                if (e.equals((current))){
                    request.add(evidenceTF.get(idx));
                }
                idx++;
            }
        }
        // no given
        if (given.isEmpty()){
            String ans = "";
            for (String r : request){
                ans += r += " ";
            }
            ans = ans.substring(0, ans.length()-1); // remove last char: " "
            return ans;   // for example: A=T
        }
        // there are given (one or more)
        for (String g : given){
            // given in evidence
            if(evidences.contains(g)){
                for (int i = 0; i<evidences.size(); i++){
                    if(evidences.get(i).equals(g)){
                        request.add(evidenceTF.get(i));
                    }
                }
            }
            // given in hidden
            else if(hidden.contains(g)){
                for (int i = 0; i<hidden.size(); i++){
                    if(hidden.get(i).equals(g)){
                        request.add(hiddenTF.get(i));
                    }
                }
            }
            else { // query contains given
                request.add(queryTF);
            }
        }

        //sort to A-B-B on first character
        Collections.sort(request, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) - o2.charAt(0);
            }
        });

        String ans = "";
        for (String r : request){
            ans += r += " ";
        }
        ans = ans.substring(0, ans.length()-1); // remove last char: " "
        return ans; // for example: A=T B=F C=T
    }
    /**
     * Simple algorithm to find the right index for .getRows().get(index) request.
     *
     * @param  varCol    variablesCollection
     * @return         (int) index
     */
    private static int findIndex(variablesCollection varCol){
        int index = 0;

        return index;
    }
    public static List<String> getPermutations(HashMap<Character, List<String>> map) {
        // Create a list to store the permutations
        List<String> permutations = new ArrayList<>();

        // Get the list of keys (letters) from the map
        List<Character> keys = new ArrayList<>(map.keySet());

        // Recursively generate the permutations
        generatePermutations(map, keys, "", permutations);

        // Return the list of permutations
        return permutations;
    }
    private static void generatePermutations(HashMap<Character, List<String>> map, List<Character> keys, String currentPermutation, List<String> permutations) {
        // If there are no more keys to process, the current permutation is complete
        if (keys.isEmpty()) {
            permutations.add(currentPermutation);
            return;
        }

        // Get the next key
        char key = keys.get(0);

        // Get the list of characters associated with the key
        List<String> characters = map.get(key);

        // Remove the key from the list of keys
        keys.remove(0);

        // Generate the permutations for each character in the list
        for (String c : characters) {
            generatePermutations(map, keys, currentPermutation + c, permutations);
        }

        // Add the key back to the list of keys
        keys.add(0, key);
    }
    /**
     * Simple algorithm of joint distribution.
     *
     * @param  q   a given query
     * @param  varCol    variablesCollection
     * @param  facCol   factorsCollection
     * @return         (double) p
     */
    private static double joint_distribution(String q, variablesCollection varCol, factorsCollection facCol) {

        // clear no needed data from string
        q = q.replace("P" , "");
        q = q.replace("(" , "");
        q = q.replace(")" , "");
        String[] q_evidence = q.split("\\|");
        System.out.println("q_evidence= " + Arrays.toString(q_evidence)); // PRINT
        System.out.println("q= " + q); //PRINT

        String query = q_evidence[0];
        String[] evidencess = new String[0];
        String evidence = "";
        if (q_evidence.length>1) {
            evidence = q_evidence[1];
            evidencess = evidence.split(",");
        }
        // convert evidencess to List<String>
        List<String> evidences = new ArrayList<>();
        for (String e : evidencess) {
            evidences.add(e.trim());
        }

        System.out.println("evidences= " + Arrays.toString(evidencess)); //PRINT
        System.out.println("q= " + query); //PRINT

        String var = query.split("=")[0];
        String boolOfVar = query.split("=")[1];


        HashMap<String, HashMap<String, Double>> collection = coll2HashMap(varCol);

        Double ppp = collection.get("B").get("B=T ");

        List<String> notHiddenVars = new ArrayList<>();
        List<String> hiddenVars = new ArrayList<>();
        notHiddenVars.add(var);

        String temp = evidence.replace("=" , "");
        temp = temp.replace("," , "");
        temp = temp.replace("T" , "");
        temp = temp.replace("F" , "");

        System.out.println("temp: "+temp);

        for(int i = 0; i < temp.length(); i++){
            String str = "" + temp.charAt(i);
            notHiddenVars.add(str);
        }
        System.out.println("notHiddenVars: "+notHiddenVars);
        for(String key : collection.keySet()){
            if(!notHiddenVars.contains(key)){
                hiddenVars.add(key);
            }
        }
        System.out.println("hiddenVars: "+hiddenVars);

        System.out.println("childes of A: " + varCol.getVariable("A").getChildes());

        System.out.println("getGiven of B: " + varCol.getVariable("B").getGiven());

        System.out.println("getOutcome of A: " + varCol.getVariable("A").getOutcome());

        // P(B=T, J=T, M=T, A=T, E=T) + P(B=T, J=T, M=T, A=F, E=T) + P(B=T, J=T, M=T, A=T, E=F) + P(B=T, J=T, M=T, A=F, E=F) +
        // P(B=F, J=T, M=T, A=T, E=T) + P(B=F, J=T, M=T, A=F, E=T) + P(B=F, J=T, M=T, A=T, E=F) + P(B=F, J=T, M=T, A=F, E=F) =
        // P(B=T) * P(J=T|A=T) * P(M=T|A=T) * P(A=T|B=T,E=T) + P(B=T) * P(J=T|A=F) * P(M=T|A=F) * P(A=F|B=T,E=T)...

        // all temp vars for joint joint_distribution algo:
        String tempQuery = query;
        String justVarOfQuery = query.replace("=", ""); // delete =
        justVarOfQuery = justVarOfQuery.replace("T", ""); // delete T
        justVarOfQuery = justVarOfQuery.replace("F", ""); // delete F

        //List of all vars
        List<String> allVars = new ArrayList<>(collection.keySet()); // A B E J M
        int indexVar = 0;

        // All combinations of True/False x |hidden vars| in List
        List<List<String>> listCombination = allCombination(hiddenVars.size());
        // Add h1=v1...
        HashMap<Character, List<String>> map = new HashMap<>();
        for(String h : hiddenVars){
            List <String> outcomesOfH = varCol.getVariable(h).getOutcome();
            List<String> full = new ArrayList<>();
            for (String l : outcomesOfH){
                l = h + "=" + l;
                full.add(l);
            }
            map.put(h.charAt(0), full);
        }
        List<String> permutations = getPermutations(map);
        System.out.println("permutations: "+ permutations);


        // Temple list of hidden vars
        List<String> TempHiddenVars = new ArrayList<>(hiddenVars.size());
        // Deep copy
        TempHiddenVars.addAll(hiddenVars);
        // probability
        Double probOfQ;

        // take only first character from evidences and put it to list
        List<String> onlyEvidences = new ArrayList<>();
        for (String item : evidences) {
            onlyEvidences.add("" + item.charAt(0));
        }





        // CALCULATING P(B=T|J,M) and P(B=F|J,M)

        double ans = 0.0;
        double ansV1 = 0.0;
        double ansV2 = 0.0;
        double ansV3 = 0.0;
        int sizeOfVarOutcome = varCol.getVariable(justVarOfQuery).getOutcome().size();
        boolean isTernary = sizeOfVarOutcome>2;


        for (int i = 0; i < sizeOfVarOutcome; i++){ // for B=V1 and B=V2 (and B=V3)
            for (int j = 0; j < Math.pow(2, hiddenVars.size()); j++){ // size of 2^hidden vars (for all options of hidden vars)
                // query = "B=T", evidences = ["J=T", "M=T"], hiddenVars = ["A", "E"]

                // loop of all combinations of True/False x |hidden vars|
                for (int index = 0; index < hiddenVars.size(); index++){
                    TempHiddenVars.set(index, hiddenVars.get(index)+"="+listCombination.get(j).get(index));
                } // TempHiddenVars: ["A=F", "E=F"]

                double mult = 1.0;
                for (String currentVar : allVars) { // want to calculate: P(B=T) * P(J=T|A=T) * P(M=T|A=T) * P(A=T|B=T,E=T)
                    //String theRequest = findRequestTernary(varCol, currentVar, tempQuery, onlyEvidences ,evidences , hiddenVars, TempHiddenVars);
                    //mult*=collection.get(currentVar).get(theRequest);
                    int rightIndex = findIndex(varCol);
                    mult*=varCol.getVariable(currentVar).getFactor().getRows().get(rightIndex).getP();
                    multiply(); // multiply*
                }
                ans+=mult; plus(); // plus++
                System.out.println("ans: " + ans);

                multiply--;
            }//end inside for
            plus--;

            if (i==0) {//first
                ansV1=ans;
                ans = 0.0;
                // Change the contents of the variable
                if (tempQuery.charAt(2) == 'T'){//binary
                    tempQuery = tempQuery.replace('T', 'F');
                }
                else {//ternary
                    tempQuery = tempQuery.replace('1', '2');
                }
            }
            else if (i == 1){//second
                ansV2=ans;
                ans = 0.0;
                // Change the contents of the variable
                if (tempQuery.charAt(2) == 'F'){//binary
                    tempQuery = tempQuery.replace('T', 'F');
                }
                else {//ternary
                    tempQuery = tempQuery.replace('2', '3');
                }
            }
            else {//i==2
                ansV3=ans;
                ans = 0.0;
            }

        }//end outside for

        System.out.println("ansV1: " + ansV1);
        System.out.println("ansV2: " + ansV2);
        System.out.println("ansV3: " + ansV3);

        ans = 1/(ansV1+ansV2+ansV3); plus(); plus();
        ans = ans*ansV1;


//        ans = 1/(ansT+ansF); plus();
//        ans=ans*ansT;

        System.out.println("ans: " + ans);
        return ans;








        //return 0;
    }

    private static double eliminate_join(String s, variablesCollection varCol, factorsCollection facCol) {
        // clean data so we could work with
        String[] split_q_hidden = s.split(" ");
        String q = split_q_hidden[0];
        String hidden = "";
        if (split_q_hidden.length>1){
            hidden = split_q_hidden[1];
        }
        q = q.replace("P" , "");
        q = q.replace("(" , "");
        q = q.replace(")" , "");
        String[] q_evidence = q.split("\\|");
        String query = q_evidence[0];
        String[] evidences = new String[0];
        String evidence = "";
        if (q_evidence.length>1){
            evidence = q_evidence[1];
            evidences = evidence.split(",");
        }
        String ques = query.split("=")[0];
        String val = query.split("=")[1];

        // reduce non parents
        String[] evidences1 = new String[0];
        if (q_evidence.length>1){
            evidences1 = new String[evidences.length];
            for(int i=0 ; i<evidences.length ; i++) {
                evidences1[i] = evidences[i].split("=")[0];
            }
        }
        List<String> parents = relevantParentsNames(ques , evidences1 , varCol , facCol);
        for (String key : varCol.getCollection().keySet()) {
            for(int i=0 ; i<varCol.getVariable(key).getFactor().getParameters().size() ; i++){
                if (!parents.contains(key)){
                    facCol.removeFactorContains(key);
                }
            }
        }

        // reduce independent
        for (String key : varCol.getCollection().keySet()) {
            String checkBayesBall = key+"-"+ques+"|"+evidence;
            if (!evidence.contains(key) && bayesBall(checkBayesBall,varCol)){
                facCol.removeFactorContains(key);
            }
        }
        // reduce evidence
        for(int i=0 ; i<evidences.length ; i++){
            String k = evidences[i].split("=")[0];
            String v = evidences[i].split("=")[1];
            for (int j=0 ; j<facCol.getAllFactors().size() ; j++){
                if (facCol.getAllFactors().get(j).contains(k)){
                    // create a new factor without the hidden column that not from the right value
                    Factor a = new Factor(facCol.getAllFactors().get(j).getParameters() , facCol.getAllFactors().get(j).getRows() , k , v);
                    facCol.getAllFactors().remove(j);
                    facCol.addToCollectionIndex(a,j);
                    if (a.getRows().size()==1){
                        facCol.getAllFactors().remove(j);
                        j--;
                    }
                }
            }
        }

        boolean join = false;
        if (split_q_hidden.length>1){

            //hidden = "A-E"; // ADDED

            String[] hiddenOrder = hidden.split("-");
            // for each hidden, find all the factors that contain hidden
            for (int i=0 ; i<hiddenOrder.length ; i++){
                factorsCollection allHiddenFactors = new factorsCollection();
                for (int j=0 ; j<facCol.getAllFactors().size() ; j++){
                    if (facCol.getFactor(j).contains(hiddenOrder[i])){
                        allHiddenFactors.addToCollection(facCol.getFactor(j));
                        facCol.getAllFactors().remove(j);
                        j--;
                    }
                }
                // if there's only one, eliminate and finish
                if (allHiddenFactors.getAllFactors().size()==1){
                    Factor factor = eliminate(allHiddenFactors.getAllFactors().get(0),hiddenOrder[i]);
                    allHiddenFactors.getAllFactors().remove(0);
                    if (factor.getParameters().size()>0){
                        facCol.addToCollection(factor);
                    }
                }
                // if there are more than one factor, sort by size, join all, and eliminate
                sortFactors(allHiddenFactors.getAllFactors());
                boolean sizeBiggerThanOne = false;
                while (allHiddenFactors.getAllFactors().size() > 1){
                    Factor factor = join(allHiddenFactors.getFactor(0) , allHiddenFactors.getFactor(1));
                    join = true;
                    allHiddenFactors.getAllFactors().remove(0);
                    allHiddenFactors.getAllFactors().remove(0);
                    allHiddenFactors.addToCollectionIndex(factor,0);
                    sizeBiggerThanOne = true;
                }
                if (sizeBiggerThanOne){
                    Factor factor = eliminate(allHiddenFactors.getFactor(0),hiddenOrder[i]);
                    if (factor.getRows().size()>1){
                        facCol.addToCollection(factor);
                    }
                }
            }
        }

        // only relevant factors of query
        sortFactors(facCol.getAllFactors());
        Factor factor = facCol.getFactor(0);
        boolean moreThanOne = false;
        while (facCol.getAllFactors().size() > 1) {
            factor = join(facCol.getFactor(0), facCol.getFactor(1));
            facCol.getAllFactors().remove(0);
            facCol.getAllFactors().remove(0);
            facCol.addToCollectionIndex(factor,0);
            moreThanOne = true;
        }
        if (moreThanOne){
            factor = eliminate(facCol.getFactor(0),"pppppppppppppp");
        }
        // normalization of query
        if (join){
            double sum = factor.getRows().get(0).getP();
            for (int i=1 ; i<factor.getRows().size() ; i++){
                sum += factor.getRows().get(i).getP();
                plus();
            }
            for (int i=0 ; i<factor.getRows().size() ; i++){
                factor.getRows().get(i).setP(factor.getRows().get(i).getP() / sum);
            }
        }

        // find the answer in the last factor and return it
        for (int j=0 ; j<factor.getRows().size() ; j++){
            if (factor.getRows().get(j).getCases().get(ques).equals(val)){
                return factor.getRows().get(j).getP();
            }
        }
        return 0;
    }


    // function for sorting all factors by their size and by their Ascii value
    private static void sortFactors(List<Factor> allFactors) {
        allFactors.sort((o1 , o2) -> {
            Integer i1 = o1.getParameters().size();
            Integer i2 = o2.getParameters().size();
            int comp = i1.compareTo(i2);
            if (comp != 0){
                return comp;
            }
            Integer asciiValue1 = AsciiSum(o1.getParameters());
            Integer asciiValue2 = AsciiSum(o2.getParameters());
            return asciiValue1.compareTo(asciiValue2);
        });

    }
    private static Integer AsciiSum(List<String> parameters) {
        Integer ans = 0;
        for (int i=0 ; i<parameters.size() ; i++){
            for (int j=0 ; j<parameters.get(i).length() ; j++){
                ans += parameters.get(i).charAt(j);
            }
        }
        return ans;
    }

    // the function returns a list that contains all parents of query and evidence (variables that are relevant to the algorithm)
    private static List<String> relevantParentsNames(String query , String[] evidences , variablesCollection varCol , factorsCollection facCol){
        List<String> parents = new ArrayList();
        // add query and evidences to list of parents
        parents.add(query);
        for (int i=0 ; i<evidences.length ; i++){
            if (!parents.contains(evidences[i])){
                parents.add(evidences[i]);
            }
        }
        // add all given of query to list of parents
        for (int i=0 ; i<varCol.getVariable(query).getGiven().size() ; i++){
            if (!parents.contains(varCol.getVariable(query).getGiven().get(i))){
                parents.add(varCol.getVariable(query).getGiven().get(i));
            }
        }
        // add all given of evidences to list of parents
        for (int i=0 ; i<evidences.length ; i++){
            for (int j=0 ; j<varCol.getVariable(evidences[i]).getGiven().size() ; j++){
                if (!parents.contains(varCol.getVariable(evidences[i]).getGiven().get(j))){
                    parents.add(varCol.getVariable(evidences[i]).getGiven().get(j));
                }
            }
        }
        // add all parents of variables in list to list of parents
        int i=0;
        while (i< parents.size()){
            for (int j=0 ; j<varCol.getVariable(parents.get(i)).getGiven().size() ; j++){
                if (!parents.contains(varCol.getVariable(parents.get(i)).getGiven().get(j))){
                    parents.add(varCol.getVariable(parents.get(i)).getGiven().get(j));
                }
            }
            i++;
        }
        return parents;
    }

    // join two given factors
    private static Factor join(Factor a, Factor b) {
        List<factorRow> rows = new ArrayList();
        List<String> bothParameters = new ArrayList();
        // find common variables
        for (int i=0 ; i<a.getParameters().size() ; i++){
            for (int j=0 ; j<b.getParameters().size() ; j++){
                if (a.getParameters().get(i).equals(b.getParameters().get(j))){
                    bothParameters.add(b.getParameters().get(j));
                }
            }
        }
        // multiply each row with the same variables
        for (int i=0 ; i<a.getRows().size() ; i++){
            for (int j=0 ; j<b.getRows().size() ; j++){
                if (a.getRows().get(i).sameParam(b.getRows().get(j) , bothParameters)){
                    rows.add(new factorRow(a.getRows().get(i).getP()*b.getRows().get(j).getP()));
                    multiply();
                    for (int k=0 ; k<bothParameters.size() ; k++) {
                        rows.get(rows.size() - 1).addParam(bothParameters.get(k) , a.getRows().get(i).getCases().get(bothParameters.get(k)));
                    }
                    // add non-common variables that were in the two rows that were joined
                    for (int k=0 ; k<a.getParameters().size() ; k++){
                        if (!bothParameters.contains(a.getParameters().get(k))){
                            rows.get(rows.size() - 1).addParam(a.getParameters().get(k) , a.getRows().get(i).getCases().get(a.getParameters().get(k)));
                        }
                    }
                    for (int k=0 ; k<b.getParameters().size() ; k++){
                        if (!bothParameters.contains(b.getParameters().get(k))){
                            rows.get(rows.size() - 1).addParam(b.getParameters().get(k) , b.getRows().get(j).getCases().get(b.getParameters().get(k)));
                        }
                    }
                }
            }
        }
        // save the parameters for making a new factor
        List<String> parameters = new ArrayList<>();
        if (rows.size()>0){
            for (String key : rows.get(0).getCases().keySet()) {
                parameters.add(key);
            }
        }
        // make a new factor and return it
        Factor ans = new Factor(parameters , rows);
        return ans;
    }

    private static Factor eliminate(Factor a, String hidden) {
        // save all parameters of factor except for the hidden parameter
        List<String> parameters = new ArrayList<>();
        for (int i=0 ; i<a.getParameters().size() ; i++){
            if (!a.getParameters().get(i).equals(hidden)){
                parameters.add(a.getParameters().get(i));
            }
        }
        // delete key and value of hidden from each row and from parameters
        for (int i=0 ; i<a.getRows().size() ; i++){
            a.getRows().get(i).getCases().remove(hidden);
        }
        a.getParameters().remove(hidden);
        // if two rows have the same parameters, attach them to one row
        for (int i=0 ; i<a.getRows().size() ; i++){
            for (int j=i+1 ; j<a.getRows().size() ; j++){
                if (a.getRows().get(i).sameParam(a.getRows().get(j) , a.getParameters())){
                    a.getRows().get(i).setP(a.getRows().get(i).getP()+a.getRows().get(j).getP());
                    plus();
                    a.getRows().remove(j);
                    j -= 1;
                }
            }
        }
        return a;
    }

    private static boolean bayesBall(String s, variablesCollection col) {
        // clean data for working with
        col.unBeenSeenAll();
        col.unColoredAll();
        int index = s.indexOf('|');
        String[] qu = s.split("\\|");
        String firstVab = qu[0].split("-")[0];
        String secondVab = qu[0].split("-")[1];
        Variable first = col.getVariable(firstVab);
        Variable second = col.getVariable(secondVab);
        if (first.getColored() || second.getColored()){
            return true; // there is no route and so they are independent
        }
        String[] parts = s.substring(index+1).split(",");
        // color all evidences
        if (!parts[0].equals("")){
            //there is something not good here!
            for (int i=0 ; i<parts.length ; i++){
                String[] given = parts[i].split("=");
                col.getVariable(given[0]).setColored(true);
            }
        }
        // return true if there is no route, return false if there is a route
        return !route(col , first, second, false , first.getName());
    }

    // recursive loop for finding if there is a route between two variables
    private static boolean route(variablesCollection col , Variable first, Variable second, boolean prevWasFather , String cameFrom) {
        if (first.getName()==second.getName()){
            return true; // there is a route
        }
        if (first.getBeenSeenFromChild() && first.getBeenSeenFromFather()){
            return false; // we saw this variable from two directions which means there is no route
        }
        if ((first.getBeenSeenFromFather() && prevWasFather) || (first.getBeenSeenFromChild() && !prevWasFather)) {
            return false;
        } // color correct "beenSeen" depends on from whom we came from
        if (prevWasFather){
            first.setBeenSeenFromFather(true);
        }
        if (!prevWasFather){
            first.setBeenSeenFromChild(true);
        }
        // look for a route according to the bayes ball rules
        if ((first.getColored() && prevWasFather) || (!first.getColored() && !prevWasFather)){
            for (int i=0 ; i<first.getGiven().size() ; i++){
                if (route(col , col.getVariable(first.getGiven().get(i)) , second , false , first.getName())){
                    return true;
                }
            }
        }
        if (!first.getColored()){
            for (int i=0 ; i<first.getChildes().size() ; i++){
                if (route(col , col.getVariable(first.getChildes().get(i)) , second , true , first.getName())){
                    return true;
                }
            }
        }
        return false;
    }

}
