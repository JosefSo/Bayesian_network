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


                            // ----------- CALLING THE ALGO'S ----------- //
                            factorsCollection facCol = new factorsCollection();
                            col.getCollection().forEach((k,v) -> facCol.addToCollection(v.getFactor()));

                            String[] queryANDnumber = data.split("\\)");
                            queryANDnumber[1] = queryANDnumber[1].substring(1,2);
                            queryANDnumber[0] = queryANDnumber[0]+")";


                            String query = queryANDnumber[0];
                            String numOfAlgo = queryANDnumber[1];

                            switch (numOfAlgo) {
                                case "1":
                                    ans = String.format("%.5g", basic_inference(query, col, facCol));
                                    ans = ans+","+plus+","+multiply;
                                    break;
                                case "2":
                                    ans = String.format("%.5g", eliminate_join(query, col, facCol, "2"));
                                    ans = ans+","+plus+","+multiply;
                                    break;
                                case "3":
                                    ans = String.format("%.5g", eliminate_join(query, col, facCol, "3"));
                                    ans = ans+","+plus+","+multiply;
                                    break;
                            }


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


    /**
     * sort string in order A-B-C include numbers (1 2 3) (we want to put it ordered to collection)
     *
     * @param  str    String that need to sort
     * @return         Sorted string
     */
    private static String sortListt(String str){

        String[] splited = str.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(splited));

        // Sort the list using a custom comparator
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                // Split the strings into lists of strings and numbers
                List<String> s1Parts = split(s1);
                List<String> s2Parts = split(s2);

                // Compare the parts one by one
                for (int i = 0; i < Math.max(s1Parts.size(), s2Parts.size()); i++) {
                    // If one of the lists is shorter, treat the missing parts as empty strings
                    String s1Part = i < s1Parts.size() ? s1Parts.get(i) : "";
                    String s2Part = i < s2Parts.size() ? s2Parts.get(i) : "";

                    // If both parts are numbers, compare them as numbers
                    if (isNumeric(s1Part) && isNumeric(s2Part)) {
                        int result = Integer.compare(Integer.parseInt(s1Part), Integer.parseInt(s2Part));
                        if (result != 0) {
                            return result;
                        }
                    }
                    // If both parts are not numbers, compare them as strings
                    else {
                        int result = s1Part.compareTo(s2Part);
                        if (result != 0) {
                            return result;
                        }
                    }
                }

                // If all parts are equal, the strings are also equal
                return 0;
            }

            // Helper function to split a string into a list of strings and numbers
            private List<String> split(String s) {
                List<String> parts = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (Character.isDigit(c)) {
                        sb.append(c);
                    } else {
                        if (sb.length() > 0) {
                            parts.add(sb.toString());
                            sb.setLength(0);
                        }
                        parts.add(String.valueOf(c));
                    }
                }
                if (sb.length() > 0) {
                    parts.add(sb.toString());
                }
                return parts;
            }

            // Helper function to check if a string represents a number
            private boolean isNumeric(String s) {
                try {
                    Integer.parseInt(s);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });

        String ans = "";
        for(String s : list){
            ans+=s+" ";
        }
        ans = ans.substring(0, ans.length()-1); // remove last char: " "
        return ans;
    }
    /**
     * sort string in order A-B-C (we want to put it ordered to collection)
     *
     * @param  str    String that need to sort
     * @return         Sorted string
     */
    private static String sortString(String str){
        String[] splited = str.split(" ");
        List<String> myList = new ArrayList<>(Arrays.asList(splited));
        //sort to A-B-B on first character
        Collections.sort(myList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) - o2.charAt(0);
            }
        });
        String ans = "";
        for(String s : myList){
            ans+=s+" ";
        }
        ans = ans.substring(0, ans.length()-1); // remove last char: " "
        return ans;
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
                keyStr = sortListt(keyStr);
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
    private static String findRequestTernary(
            variablesCollection varCol, String current, String queryTF,
            List<String> evidences, List<String> evidenceTF,
            List<String> hidden, List<String> hiddenTF){

        List<String> request = new ArrayList<>();
        List<String> given = varCol.getVariable(current).getGiven();

        // checking what is current (query, evidence, hidden) and giving it right outcome: T/F

        // query
        String[] splittedQuery = queryTF.split("=");
        if(current.equals(splittedQuery[0])){
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


        String ans = "";
        for (String r : request){
            ans += r += " ";
        }
        ans = sortListt(ans); // sort
        return ans; // for example: A=T B=F C=T
    }

    public static List<String> getPermutations(HashMap<String, List<String>> map) {
        // Create a list to store the permutations
        List<String> permutations = new ArrayList<>();

        // Get the list of keys (letters) from the map
        List<String> keys = new ArrayList<>(map.keySet());

        // Recursively generate the permutations
        generatePermutations(map, keys, "", permutations);

        // Return the list of permutations
        return permutations;
    }
    private static void generatePermutations(HashMap<String, List<String>> map, List<String> keys, String currentPermutation, List<String> permutations) {
        // If there are no more keys to process, the current permutation is complete
        if (keys.isEmpty()) {
            permutations.add(currentPermutation);
            return;
        }

        // Get the next key
        String key = keys.get(0);

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
     * Simple algorithm of basic inference.
     *
     * @param  q   a given query
     * @param  varCol    variablesCollection
     * @param  facCol   factorsCollection
     * @return         (double) p - probability of query
     */
    private static double basic_inference(String q, variablesCollection varCol, factorsCollection facCol) {

        // ----- clear no needed data from string ------ //
        q = q.replace("P" , "");
        q = q.replace("(" , "");
        q = q.replace(")" , "");
        String[] q_evidence = q.split("\\|");
        String query = q_evidence[0];
        String[] evidencess = new String[0];
        String evidence = "";
        if (q_evidence.length>1) {
            evidence = q_evidence[1];
            evidencess = evidence.split(",");
        }
        // --- convert evidencess to List<String> ---- //
        List<String> evidences = new ArrayList<>();
        for (String e : evidencess) {
            evidences.add(e.trim());
        }

        // -------------------- HASHMAP COLLECTION --------------------------- //
        HashMap<String, HashMap<String, Double>> collection = coll2HashMap(varCol);

        // ---------- HIDDEN & NOT HIDDEN ---------- //
        List<String> notHiddenVars = new ArrayList<>();
        List<String> hiddenVars = new ArrayList<>();
        String justVarOfQuery = query.split("=")[0];
        notHiddenVars.add(justVarOfQuery);
        String []splitedTemp1 = evidence.split(",");
        for(int i = 0; i < splitedTemp1.length; i++) {
            String []splittted = splitedTemp1[i].split("=");
            notHiddenVars.add(splittted[0]);
        }
        for(String key : collection.keySet()){
            if(!notHiddenVars.contains(key)){
                hiddenVars.add(key);
            }
        }// ------- END: HIDDEN & NOT HIDDEN ------- //

        // ------ definition of all temp vars for joint joint_distribution algo -------- //
        String tempQuery = "";
        for(int i = 0; i< query.length(); i++){
            tempQuery += ""+query.charAt(i);
        }
        List<String> allVars = new ArrayList<>(collection.keySet()); //List of all vars: A B E J M

        // ----- Generating all combinations of |True/False or V1,V2,V3| x |hidden vars| in List ------- //
        HashMap<String, List<String>> map = new HashMap<>();
        for(String h : hiddenVars){
            List <String> outcomesOfH = varCol.getVariable(h).getOutcome();
            List<String> full = new ArrayList<>();
            for (String l : outcomesOfH){
                l = h + "=" + l+ " ";
                full.add(l);
            }
            map.put(h, full);
        }
        List<String> tempPermutations = getPermutations(map);
        List<List<String>> permutationsOfHiddenVars = new ArrayList<>();
        for(int i = 0; i < tempPermutations.size(); i++){
            String[] one = tempPermutations.get(i).split(" ");
            permutationsOfHiddenVars.add(Arrays.asList(one));
        }

        // --- take only evidence without its outcome and put it to list --- //
        List<String> onlyEvidences = new ArrayList<>();
        for (String item : evidences) {
            String[] splitted = item.split("=");
            onlyEvidences.add(splitted[0]);
        }

        List<String> TempHiddenVars = new ArrayList<>(hiddenVars.size());  // -> Temple list of hidden vars
        double ans = 0.0;  double ansV1 = 0.0; double ansV2 = 0.0; double ansV3 = 0.0;
        int sizeOfVarOutcome = varCol.getVariable(justVarOfQuery).getOutcome().size();
        boolean isTernary = sizeOfVarOutcome>2;

        // P(B=T, J=T, M=T, A=T, E=T) + P(B=T, J=T, M=T, A=F, E=T) + P(B=T, J=T, M=T, A=T, E=F) + P(B=T, J=T, M=T, A=F, E=F) +
        // P(B=F, J=T, M=T, A=T, E=T) + P(B=F, J=T, M=T, A=F, E=T) + P(B=F, J=T, M=T, A=T, E=F) + P(B=F, J=T, M=T, A=F, E=F) =
        // P(B=T) * P(J=T|A=T) * P(M=T|A=T) * P(A=T|B=T,E=T) + P(B=T) * P(J=T|A=F) * P(M=T|A=F) * P(A=F|B=T,E=T)...


        // ------------------------------ basic_inference ALGORITHM ----------------------------------- //
        for (int i = 0; i < sizeOfVarOutcome; i++){ // for B=V1 and B=V2 (and B=V3)
            for (List<String> permutationsOfHiddenVar : permutationsOfHiddenVars) { // size of (all options of hidden vars)
                // query = "B=T", evidences = ["J=T", "M=T"], hiddenVars = ["A", "E"]
                // hiddenPermutations: [[A=T, E=T], [A=T, E=F], [A=F, E=T], [A=F, E=F]]
                TempHiddenVars = permutationsOfHiddenVar;
                double mult = 1.0;
                for (String currentVar : allVars) { // want to calculate: P(B=T) * P(J=T|A=T) * P(M=T|A=T) * P(A=T|B=T,E=T)
                    String theRequest = findRequestTernary(varCol, currentVar, tempQuery, onlyEvidences, evidences, hiddenVars, TempHiddenVars);
                    mult *= collection.get(currentVar).get(theRequest);
                    multiply(); // multiply*
                }
                ans += mult;
                plus(); // plus++
                multiply--;
            }//end inside for
            plus--;

            // ------- Change the contents of the variable -------- //
            String[] splitedTempQuery = tempQuery.split("=");
            if (i==0 || i==1 || i==2) {//first, second, (third)
                if (i == 0){
                    ansV1=ans;
                    ans = 0.0;
                }
                else if (i == 1){
                    ansV2=ans;
                    ans = 0.0;
                }
                else {
                    ansV3=ans;
                    ans = 0.0;
                }
                // Change the contents of the variable
                if (splitedTempQuery[1].equals("T")){
                    tempQuery = tempQuery.replace('T', 'F');
                }
                else if (splitedTempQuery[1].equals("F")){
                    tempQuery = tempQuery.replace('F', 'T');
                }
                else if(splitedTempQuery[1].equals("v1")){// change v1 to v2
                    splitedTempQuery[1] = "v2";
                    String newString = "";
                    for(int idx = 0; idx < splitedTempQuery.length; idx++){
                        newString+=splitedTempQuery[idx]+"=";
                    }
                    newString = newString.substring(0, newString.length()-1);
                    tempQuery = newString;
                }
                else if (splitedTempQuery[1].equals("v2")){
                    splitedTempQuery[1] = "v3";
                    String newString = "";
                    for(int idx = 0; idx < splitedTempQuery.length; idx++){
                        newString+=splitedTempQuery[idx]+"=";
                    }
                    newString = newString.substring(0, newString.length()-1);
                    tempQuery = newString;
                }
                else if (splitedTempQuery[1].equals("v3")){
                    splitedTempQuery[1] = "v1";
                    String newString = "";
                    for(int idx = 0; idx < splitedTempQuery.length; idx++){
                        newString+=splitedTempQuery[idx]+"=";
                    }
                    newString = newString.substring(0, newString.length()-1);
                    tempQuery = newString;
                }

            }
        }
        // -------- NORMALIZATION --------- //
        ans = ansV1/(ansV1+ansV2+ansV3); plus();
        if(ansV3 != 0.0){ plus(); }

//        for (String key : collection.keySet()){
//            System.out.print(key + " [");
//            for (String key2 : collection.get(key).keySet()){
//                System.out.print("["+"key: {" +key2+"}" + " value: " + collection.get(key).get(key2)+"] ");
//            }
//            System.out.println("]");
//        }

        return ans;
    }


    /**
     * Inference with Variable Elimination Algorithm.
     *
     * @param  s   a given query
     * @param  varCol    variablesCollection
     * @param  facCol   factorsCollection
     * @param  numOfAlgo   number of algo (2 or 3)
     * @return         (double) p - probability of query
     */
    private static double eliminate_join(String s, variablesCollection varCol, factorsCollection facCol, String numOfAlgo) {
        if(numOfAlgo.equals("2")){
            s = returnQuery(s, varCol);
        }
        else{
            s = returnQueryHeuristic(s, varCol);
        }

        // ----------- clean the data ----------- //
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

        // ----------  reduce non parents ----------- //
        String[] evidences1 = new String[0];
        if (q_evidence.length>1){
            evidences1 = new String[evidences.length];
            for(int i=0 ; i<evidences.length ; i++) {
                evidences1[i] = evidences[i].split("=")[0];;
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

        // ---------- reduce independent ----------- //
        for (String key : varCol.getCollection().keySet()) {
            String checkBayesBall = key+"-"+ques+"|"+evidence;
            if (!evidence.contains(key) && bayesBall(checkBayesBall,varCol)){
                facCol.removeFactorContains(key);
            }
        }
        // ----------  reduce evidence  ----------- //
        for (String item : evidences) {
            String k = item.split("=")[0];
            String v = item.split("=")[1];
            for (int j = 0; j < facCol.getAllFactors().size(); j++) {
                if (facCol.getAllFactors().get(j).contains(k)) {
                    // ---- create a new factor without the hidden column that not from the right value ----------- //
                    Factor a = new Factor(facCol.getAllFactors().get(j).getParameters(), facCol.getAllFactors().get(j).getRows(), k, v);
                    facCol.getAllFactors().remove(j);
                    facCol.addToCollectionIndex(a, j);
                    if (a.getRows().size() == 1) {
                        facCol.getAllFactors().remove(j);
                        j--;
                    }
                }
            }
        }

        boolean join = false;
        if (split_q_hidden.length>1){
            String[] hiddenOrder = hidden.split("-");
            // ---- for each hidden, find all the factors that contain hidden ----------- //
            for (String value : hiddenOrder) {
                factorsCollection allHiddenFactors = new factorsCollection();
                for (int j = 0; j < facCol.getAllFactors().size(); j++) {
                    if (facCol.getFactor(j).contains(value)) {
                        allHiddenFactors.addToCollection(facCol.getFactor(j));
                        facCol.getAllFactors().remove(j);
                        j--;
                    }
                }
                // ------- if there's only one, eliminate and finish ----------- //
                if (allHiddenFactors.getAllFactors().size() == 1) {
                    Factor factor = eliminate(allHiddenFactors.getAllFactors().get(0), value);
                    allHiddenFactors.getAllFactors().remove(0);
                    if (factor.getParameters().size() > 0) {
                        facCol.addToCollection(factor);
                    }
                }
                // --- if there are more than one factor, sort by size, join all, and eliminate --- //
                sortFactors(allHiddenFactors.getAllFactors());
                boolean sizeBiggerThanOne = false;
                while (allHiddenFactors.getAllFactors().size() > 1) {
                    Factor factor = join(allHiddenFactors.getFactor(0), allHiddenFactors.getFactor(1));
                    join = true;
                    allHiddenFactors.getAllFactors().remove(0);
                    allHiddenFactors.getAllFactors().remove(0);
                    allHiddenFactors.addToCollectionIndex(factor, 0);
                    sizeBiggerThanOne = true;
                }
                if (sizeBiggerThanOne) {
                    Factor factor = eliminate(allHiddenFactors.getFactor(0), value);
                    if (factor.getRows().size() > 1) {
                        facCol.addToCollection(factor);
                    }
                }
            }
        }

        // ---- only relevant factors of query ----- //
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
            factor = eliminate(facCol.getFactor(0),"hidden");
        }
        // -------- normalization of query --------- //
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

        // --- find the answer in the last factor and return it --- //
        for (int j=0 ; j<factor.getRows().size() ; j++){
            if (factor.getRows().get(j).getCases().get(ques).equals(val)){
                return factor.getRows().get(j).getP();
            }
        }
        return 0;
    }

    /**
     * function for sorting all factors by their size and by their Ascii value
     *
     * @param  allFactors    List of all factors
     */
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
    /**
     * function for summation of all parameters in a List
     *
     * @param  parameters    List of String
     * @return (int)ans - summation
     */
    private static int AsciiSum(List<String> parameters) {
        int answer = 0;
        for (String parameter : parameters) {
            for (int j = 0; j < parameter.length(); j++) {
                answer += parameter.charAt(j);
            }
        }
        return answer;
    }


    /**
     * Function returns a list that contains all parents of query and evidence (variables that are relevant to the algorithm)
     * @param  query    the query
     * @param  evidences    String List of evidences
     * @param  varCol    variables Collection
     * @param  facCol    factors Collection
     * @return (List<String>)parents - all needed parents
     */
    private static List<String> relevantParentsNames(String query , String[] evidences , variablesCollection varCol , factorsCollection facCol){
        List<String> parents = new ArrayList<>();
        // ----- add query and evidences to list of parents ----- //
        parents.add(query);
        for (String evidence : evidences) {
            if (!parents.contains(evidence)) {
                parents.add(evidence);
            }
        }
        // ----- add all given of query to list of parents ----- //
        for (int i=0 ; i<varCol.getVariable(query).getGiven().size() ; i++){
            if (!parents.contains(varCol.getVariable(query).getGiven().get(i))){
                parents.add(varCol.getVariable(query).getGiven().get(i));
            }
        }
        // ----- add all given of evidences to list of parents ----- //
        for (String evidence : evidences) {
            for (int j = 0; j < varCol.getVariable(evidence).getGiven().size(); j++) {
                if (!parents.contains(varCol.getVariable(evidence).getGiven().get(j))) {
                    parents.add(varCol.getVariable(evidence).getGiven().get(j));
                }
            }
        }
        // ----- add all parents of variables in list to list of parents ------ //
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


    /**
     *join two given factors
     * @param  a    the query
     * @param  b    String List of evidences
     * @return (Factor) - factor of join two factors
     */
    private static Factor join(Factor a, Factor b) {
        List<factorRow> rows = new ArrayList<>();
        List<String> bothParameters = new ArrayList<>();
        // find common variables
        for (int i=0 ; i<a.getParameters().size() ; i++){
            for (int j=0 ; j<b.getParameters().size() ; j++){
                if (a.getParameters().get(i).equals(b.getParameters().get(j))){
                    bothParameters.add(b.getParameters().get(j));
                }
            }
        }
        // -------  multiply each row with the same variables -------- //
        for (int i=0 ; i<a.getRows().size() ; i++){
            for (int j=0 ; j<b.getRows().size() ; j++){
                if (a.getRows().get(i).sameParam(b.getRows().get(j) , bothParameters)){
                    rows.add(new factorRow(a.getRows().get(i).getP()*b.getRows().get(j).getP()));
                    multiply();
                    for (String bothParameter : bothParameters) {
                        rows.get(rows.size() - 1).addParam(bothParameter, a.getRows().get(i).getCases().get(bothParameter));
                    }
                    // ---- add non-common variables that were in the two rows that were joined ---- //
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
        // ----- save the parameters for making a new factor ----- //
        List<String> parameters = new ArrayList<>();
        if (rows.size()>0){
            parameters.addAll(rows.get(0).getCases().keySet());
        }
        // ---- make a new factor and return it ---- //
        return new Factor(parameters , rows);
    }
    /**
     * Eliminate a hidden parameter
     * @param  a    Factor
     * @param  hidden    String of hidden
     * @return (Factor) - a
     */
    private static Factor eliminate(Factor a, String hidden) {
        // ----- delete key and value of hidden from each row and from parameters ----- //
        for (int i=0 ; i<a.getRows().size() ; i++){
            a.getRows().get(i).getCases().remove(hidden);
        }
        a.getParameters().remove(hidden);
        // ---- if two rows have the same parameters, attach them to one row ---- //
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
    /**
     * bayesBall algorithm
     * @param  s    String
     * @param  col    variablesCollection
     * @return (boolean) - True/False
     */
    private static boolean bayesBall(String s, variablesCollection col) {
        // ---- clean data for working with ---- //
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
        // ----- color all evidences ------ //
        if (!parts[0].equals("")){
            //there is something not good here!
            for (int i=0 ; i<parts.length ; i++){
                String[] given = parts[i].split("=");
                col.getVariable(given[0]).setColored(true);
            }
        }
        // --- return true if there is no route, return false if there is a route --- //
        return !route(col , first, second, false , first.getName());
    }


    /**
     * recursive loop for finding if there is a route between two variables
     *
     * @param  col    variablesCollection
     * @param  first    Variable
     * @param  second    Variable
     * @param  prevWasFather    boolean: if previous Was Father
     * @param  cameFrom    where it came from
     * @return (boolean) - True/False
     */
    private static boolean route(variablesCollection col , Variable first, Variable second, boolean prevWasFather , String cameFrom) {
        if (Objects.equals(first.getName(), second.getName())){
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
        // ----------- look for a route according to the bayes ball rules ----------- //
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

    /**
     * Function that returns updated query with right heuristic order (A-B-C)
     *
     * @param  str    String of query
     * @param  varCol    variablesCollection
     * @return (String) - updated query with right heuristic order
     */
    private static String returnQuery(String str, variablesCollection varCol){
        // clear no needed data from string
        String q = "";
        for (int i = 0; i < str.length(); i++){q+=""+str.charAt(i);}
        q = q.replace("P" , "");
        q = q.replace("(" , "");
        q = q.replace(")" , "");
        String[] q_evidence = q.split("\\|");

        String query = q_evidence[0];
        String[] evidencess = new String[0];
        String evidence = "";
        if (q_evidence.length>1) {
            evidence = q_evidence[1];
            evidencess = evidence.split(",");
        }
        String var = query.split("=")[0];
        String boolOfVar = query.split("=")[1];
        HashMap<String, HashMap<String, Double>> collection = coll2HashMap(varCol);
        List<String> notHiddenVars = new ArrayList<>();
        notHiddenVars.add(var);
        String temp = evidence.replace("=" , "");
        temp = temp.replace("," , "");
        temp = temp.replace("T" , "");
        temp = temp.replace("F" , "");
        for(int i = 0; i < temp.length(); i++){
            String str1 = "" + temp.charAt(i);
            notHiddenVars.add(str1);
        }
        String stringHidden = "";
        for(String key : collection.keySet()){
            if(!notHiddenVars.contains(key)){
                stringHidden+=key+" ";
            }
        }
        stringHidden=stringHidden.substring(0, stringHidden.length()-1);
        stringHidden = sortListt(stringHidden);
        stringHidden= stringHidden.replace(" ","-");
        return str + " " + stringHidden;
    }
    /**
     * Function that returns updated query with right heuristic order (not A-B-C)
     *
     * @param  str    String of query
     * @param  varCol    variablesCollection
     * @return (String) - updated query with right heuristic order
     */
    public static String returnQueryHeuristic(String str, variablesCollection varCol){
        return str;
    }

}

