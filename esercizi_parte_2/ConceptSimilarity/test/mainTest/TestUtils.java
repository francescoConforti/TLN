/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainTest;

import conceptSimilarity.Utils;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author confo
 */
public class TestUtils {
  
  private final IDictionary dict;
  
  public TestUtils(IDictionary dictionary){
    dict = dictionary;
  }
  
  private void testDept(ISynsetID synset){
    Utils utils = new Utils(dict);
    int n = utils.findDepth(synset);
    System.out.println("depth of dog: " + n); 
  }
  
  private void testAllHypernyms(ISynsetID synset){
    Utils utils = new Utils(dict);
    List<ISynsetID> list = utils.allAncestors(synset);
    utils.printSynsetIDList(list);
  }
  
  private void testLcs(ISynsetID s1, ISynsetID s2){
    Utils utils = new Utils(dict);
    ISynsetID lca = utils.leastCommonAncestor(s1, s2);
    if(lca != null){
      System.out.println("Least common subsimer of");
      utils.printSynsetID(s1);
      System.out.println("having depth: " + utils.findDepth(s1) + " and");
      utils.printSynsetID(s2);
      System.out.println("having depth: " + utils.findDepth(s2) + " is:");
      utils.printSynsetID(lca);
      System.out.println("having depth: " + utils.findDepth(lca));
    }
    else{
      utils.printSynsetID(s1);
      System.out.println("and");
      utils.printSynsetID(s2);
      System.out.println("don't have a least common subsimer");
    }
  }
  
  private void testDistance(ISynsetID synset1, ISynsetID synset2){
    Utils utils = new Utils(dict);
    int n = utils.distance(synset1, synset2);
    System.out.print("Distance between ");
    utils.printSynsetID(synset1);
    System.out.print(" and ");
    utils.printSynsetID(synset2);
    System.out.println(" is: " + n);
  }
  
  private void testMaxDepth(){
    Utils utils = new Utils(dict);
    System.out.println("Max depth of the taxonomy: " + utils.maxDepth());
  }
  
  public static void main(String[] args){
    // create and open dictionary
    URL url;
    IDictionary dict = null;
    try {
      url = new URL("file", null, "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
      dict = new Dictionary(url);
    } catch (MalformedURLException ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      dict.open();
    } catch (IOException ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // get synset for a word
    IIndexWord idxWord = dict.getIndexWord("entity", POS.NOUN);
    IWordID wordID = idxWord.getWordIDs().get(0); // 1 st meaning
    ISynsetID synset1 = wordID.getSynsetID();
    // get synset for another word
    idxWord = dict.getIndexWord("armchair", POS.NOUN);
    wordID = idxWord.getWordIDs().get(0); // 1 st meaning
    ISynsetID synset2 = wordID.getSynsetID();
    
    // actual tests
    TestUtils t = new TestUtils(dict);
    t.testAllHypernyms(synset1);
    System.out.println("");
    t.testDept(synset1);
    System.out.println("");
    t.testLcs(synset1, synset2);
    System.out.println("");
    t.testDistance(synset1, synset2);
    System.out.println("");
    t.testMaxDepth();
  }
}
