//TO-DO Add necessary imports
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;

public class AutoComplete{

  //TO-DO: Add instance variable: you should have at least the tree root

  private static final char SENTINEL = '^';
  private DLBNode root; //root of the trie

  public static void main(String[] args) throws java.io.IOException {
    AutoComplete autocomplete = new AutoComplete("dict8.txt");

  }

  public AutoComplete(String dictFile) throws java.io.IOException {
    //TO-DO Initialize the instance variables
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      add(word);
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */


  //add word to the trie, takes in the word as a StringBuilder
  public void add(StringBuilder word){
    //TO-DO Implement this method

    if (root == null) {
      root = new DLBNode(word.charAt(0), 0);
    }

    DLBNode currentNode = root;
    if (currentNode.data != word.charAt(0)) {
      DLBNode currentRootNode = currentNode;
      while (currentRootNode.sibling != null) {
        if (currentRootNode.sibling.data == word.charAt(0)) {
          break;
        }
        currentRootNode = currentRootNode.sibling;
      }
      if (currentRootNode.sibling == null) {
        currentRootNode.sibling = new DLBNode(word.charAt(0), 0);
      }
      currentNode = currentRootNode.sibling;
    }

    for (int i=1; i<word.length(); i++) {
       char currentLetter = word.charAt(i);
       if (currentNode.child == null) {
         currentNode.child = new DLBNode(currentLetter, 0);
         currentNode = currentNode.child;
       } else {
        if (currentNode.child.data != currentLetter) {
          DLBNode currentSiblingNode = currentNode.child;
          while (currentSiblingNode.sibling != null && currentSiblingNode.sibling.data != currentLetter) {
            currentSiblingNode = currentSiblingNode.sibling;
          }
          if (currentSiblingNode.sibling == null) {
            currentSiblingNode.sibling = new DLBNode(currentLetter, 0);
          }
          currentNode = currentSiblingNode.sibling;
        } else {
          currentNode = currentNode.child;
        }
      }
    }

    currentNode.isWord = true;





  }

  //increment the score of word, takes in the StringBuilder word
  public void notifyWordSelected(StringBuilder word){
    //TO-DO Implement this method
  DLBNode result = throughMethod(word);
    result.score++;
  }

  //get the score of word (return the score of the word), takes in the StringBuilder word
  public int getScore(StringBuilder word){
    //TO-DO Implement this method
    DLBNode result = throughMethod(word);
    return result.score;
  }
// method to loop through all the possibilties of words (children and siblings included)
  public DLBNode throughMethod(StringBuilder word)
  {
    if (root == null)
    {
      return null;
    }
    DLBNode currentNode = root;
    if (currentNode.data != word.charAt(0)) {
      DLBNode currentRootNode = currentNode;
      while (currentRootNode.sibling != null) {
        if (currentRootNode.sibling.data == word.charAt(0)) {
          break;
        }
        currentRootNode = currentRootNode.sibling;
      }
      currentNode = currentRootNode.sibling;
    }

    for (int i=1; i<word.length(); i++) {
      if (currentNode == null)
      {
        return null;
      }
       char currentLetter = word.charAt(i);
       if (currentNode.child == null) {
         System.out.println("ERROR");
         return null;
       } else {
        if (currentNode.child.data != currentLetter) {
          DLBNode currentSiblingNode = currentNode.child;
          while (currentSiblingNode.sibling != null && currentSiblingNode.sibling.data != currentLetter) {
            currentSiblingNode = currentSiblingNode.sibling;
          }
          if (currentSiblingNode.sibling == null) {
            System.out.println("ERROR");
            return null;
          }
          currentNode = currentSiblingNode.sibling;
        } else {
          currentNode = currentNode.child;
        }
      }
    }
    return currentNode;
  }

  /**
   * Part 2: retrieve word suggestions in sorted order.
   */

  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    //TO-DO Implement this method
    ArrayList<Suggestion> s = new ArrayList<Suggestion>();
    DLBNode currentNode = throughMethod(word);

    if (currentNode == null)
    {
      return s;
    }
    if (currentNode.isWord) {
      s.add(new Suggestion(new StringBuilder(word), currentNode.score));
    }
    retrieveWords(word, s, currentNode.child);
    Collections.sort(s);
    return s;

  }

  // takes in the StringBuilder word, the arrayList suggestionList, and the currentNode of the DLBNode, and
  //if the current node is not null and not a word in the dictionary, it is added to the dictioonary
  //if the current node is a word in the dictionary, it is added to the suggestion list, then method
  //is recursively called at the child and sibling
  private void retrieveWords(StringBuilder word, ArrayList<Suggestion> suggestionList, DLBNode currentNode)
  {
    if(currentNode != null){
      word.append(currentNode.data);

      if(currentNode.isWord){
        Suggestion s = new Suggestion(new StringBuilder(word), currentNode.score);
        suggestionList.add(s);
      }
      retrieveWords(word, suggestionList, currentNode.child);
      word.deleteCharAt(word.length() - 1);
      retrieveWords(word, suggestionList, currentNode.sibling);


    }
  }

  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }


  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair.
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion> {
    //TO-DO Fill in the fields and methods for this class. Make sure to have them public as they will be accessed from the test program A2Test.java.
    public StringBuilder word;
    public int score;

    public Suggestion(StringBuilder word, int score)
    {
      this.word = word;
      this.score = score;
    }

    public int compareTo(Suggestion s){
      if(score==s.score)
      return 0;
      else if(score>s.score)
      return -1;
      else
      return 1;
    }
  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
