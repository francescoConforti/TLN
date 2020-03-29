/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordsensedisambiguation;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author confo
 */
public class WordSenseDisambiguation {
  
  IDictionary dict;
  
  public WordSenseDisambiguation(IDictionary dictionary){
    dict = dictionary;
  }

  // suppose only one term to analyze in the sentence
  public String findPoly(String[] sentence){
    String poly = "";
    for(int i = 1; i < sentence.length; ++i){ // 0 is "-"
      if(sentence[i].matches("\\*\\*\\S+\\*\\*")){
        poly = sentence[i].replace("*", "");
      }
    }
    return poly;
  }
  
  public ISynset simplifiedLesk(String word, String[] context){
    try {
      dict.open();
    } catch (IOException ex) {
      Logger.getLogger(WordSenseDisambiguation.class.getName()).log(Level.SEVERE, null, ex);
    }
    IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
    IWordID bestSense = idxWord.getWordIDs().get(0);
    int maxOverlap = 0;
    for (IWordID sense : idxWord.getWordIDs()) {
      
    }
    return null;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String path = "./sentences.txt";
    URL url = null;
    try {
      url = new URL("file", null, "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
    } catch (MalformedURLException ex) {
      Logger.getLogger(WordSenseDisambiguation.class.getName()).log(Level.SEVERE, null, ex);
    }
    IDictionary dict = new Dictionary(url);
    WordSenseDisambiguation wsd = new WordSenseDisambiguation(dict);
    String line, polysemic;
    String[] words;
    
    // sentences.txt
    File file = new File(path);
    String encoding = "UTF-8";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
              new FileInputStream(file), encoding));
      
      while((line = reader.readLine()) != null){
        // prepare words
        words = line.split("\\s|\\.");
        if (words.length > 0 && words[0].equals("-")) { // legal sentence
          polysemic = wsd.findPoly(words);
          // System.out.println(polysemic);  // DEBUG
        }
        // process
        
      }
      
      reader.close();
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(WordSenseDisambiguation.class.getName()).log(Level.SEVERE, null, ex);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(WordSenseDisambiguation.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(WordSenseDisambiguation.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
