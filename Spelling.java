import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Spelling {

    static ArrayList<Pair<String,Integer>> Data = new ArrayList<>();    //static ArrayList to hold the imported data pairs

    public Spelling() {
    }

    public class TrieNode {
        Map<Character, TrieNode> children;
        char c;
        boolean isWord;

        public TrieNode(char c) {
            this.c = c;
            children = new HashMap<>();
        }

        public TrieNode() {
            children = new HashMap<>();
        }

        public void insert(String word) {                     //function to insert elements into the trie
            if (word == null || word.isEmpty())
                return;
            char firstChar = word.charAt(0);
            TrieNode child = children.get(firstChar);
            if (child == null) {
                child = new TrieNode(firstChar);
                children.put(firstChar, child);
            }

            if (word.length() > 1)
                child.insert(word.substring(1));
            else
                child.isWord = true;
        }

    }

    public Map<String,Integer> hashData(ArrayList<Pair<String,Integer>> list){                    //hashmap to store imported <word,frequency>
        HashMap<String,Integer> frequency = new HashMap<>();
        for(Pair pair : list){
            frequency.put(pair.getWord().toString(), (int) Long.parseLong(pair.getNum().toString()));
        }
        return frequency;
    }

    TrieNode root;

    public Spelling(ArrayList<Pair<String,Integer>> list) {           //inserts data words from file into trie
        root = new TrieNode();
        for (Pair pair : list){
            String x = pair.getWord().toString();
            root.insert(x);
        }
    }

    public boolean find(String prefix, boolean exact) {              //to find prefixes
        TrieNode lastNode = root;
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return false;
        }
        return !exact || lastNode.isWord;
    }

    public void suggestHelper(TrieNode root, List<String> list, StringBuffer curr) {        //adds words with specified prefix to a list
        if (root.isWord) {
            list.add(curr.toString());
        }

        if (root.children == null || root.children.isEmpty())
            return;

        for (TrieNode child : root.children.values()) {
            suggestHelper(child, list, curr.append(child.c));
            curr.setLength(curr.length() - 1);
        }
    }

    public List<String> suggesting(String prefix) {               //returns words with specific prefix in a list
        List<String> list = new ArrayList<>();
        TrieNode lastNode = root;
        StringBuffer curr = new StringBuffer();
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return list;
            curr.append(c);
        }
        suggestHelper(lastNode, list, curr);
        return list;
    }

    public List<String> checkFreq(List<String> list, int count){          //takes a list and the count and finds the words in the
        if(list.size()==0||list.size()<count)                             //list with the greatest frequency and returns that list
            return list;
        List<String> words = new ArrayList<>();
        Map<String,Integer> freq;
        freq = hashData(Data);
        while(count>0) {
            String add=list.get(0);
            for (int i = 0; i < list.size()-1; i++) {
                if(freq.get(list.get(i+1))>freq.get(add))
                    add = list.get(i+1);
            }
            words.add(add);
            list.remove(add);
            count--;
        }
        return words;
    }

    public List<List<String>> suggest(String token, int count){          //uses helper functions to return a list of lists that has
        List<List<String>> finList = new ArrayList<>();                  //a list for every character in token, and in
        for (int i = 0; i < token.length(); i++) {                       //each of those lists is 'count' number of suggested
            String x = "";                                               //words
            for (int j = 0; j <= i; j++) {
                x += token.charAt(j);
            }
            List<String> list;
            list = suggesting(x);
            if(list.size()==0){
                list = finList.get(x.length()-2);
                finList.add(list);
                continue;
            }
            list = checkFreq(list,count);
            if(list.size()<count&&list.size()!=0){
                int need = count - list.size();
                for (int j = 0; j < finList.get(x.length()-2).size()&&need>0; j++) {
                    if(!list.contains(finList.get(x.length()-2).get(j))) {
                        list.add(finList.get(x.length() - 2).get(j));
                        need--;
                    }
                }
            }
            finList.add(list);
        }
        return finList;
    }

    public ArrayList<Pair<String, Integer>> getFile(String path) throws Exception {     //extracts the data from the given file into
        ArrayList<Pair<String,Integer>> list = new ArrayList<>();                       //an arraylist of pairs
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if(Objects.equals(values[1], "count"))
                continue;
            Pair pair = new Pair(values[0], Long.parseLong(values[1]));
            list.add(pair);
        }
        return list;
    }


    public static void main(String[] args) throws Exception {
        String path = args[0];
        Spelling tries = new Spelling();
        Data = tries.getFile(path);
        Spelling trie = new Spelling(Data);
        System.out.println(trie.suggest("apple", Integer.parseInt(args[1])));    //test word is "apple" 
    }

}


