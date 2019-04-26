/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptSimilarity;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author confo
 */
public class CSMain {
  
  public static void main(String[] args){
    List<String> words1 = new ArrayList<>();
    List<String> words2 = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    List<Double> results = new ArrayList<>();
    double[] valuesArr;
    double[] resultsArr;
    double maxSimilarityVal = Double.NEGATIVE_INFINITY;
    double similarityVal;
    
    // create and open dictionary
    URL url = null;
    try {
      url = new URL("file", null, "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
    } catch (MalformedURLException ex) {
      Logger.getLogger(CSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
// construct the dictionary object and open it
    IDictionary dict = new Dictionary(url);
    try {
      dict.open();
    } catch (IOException ex) {
      Logger.getLogger(CSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    Utils utils = new Utils(dict);
    Similarity sim = new Similarity(utils);
    
    // read from CSV
    String file = "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/"
            + "ConceptSimilarity/altro/input/WordSim353.csv";
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      String[] parts;
      br.readLine();  // first line is not data
      while ((line = br.readLine()) != null) {
        parts = line.split(",");
        words1.add(parts[0]);
        words2.add(parts[1]);
        values.add(new Double(parts[2]));
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CSMain.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(CSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // Wu & Palmer
    for (int i = 0; i < words1.size(); ++i) { // for every row
      List<IWordID> wordID1 = null;
      List<IWordID> wordID2 = null;
      boolean exception = false;
      maxSimilarityVal = Double.NEGATIVE_INFINITY;
      try {
        IIndexWord idxWord1 = dict.getIndexWord(words1.get(i), POS.NOUN);
        wordID1 = idxWord1.getWordIDs();
        IIndexWord idxWord2 = dict.getIndexWord(words2.get(i), POS.NOUN);
        wordID2 = idxWord2.getWordIDs();
      }
      catch(NullPointerException e){
        results.add(new Double(0));
        exception = true;
      }
      if (!exception) {
        for (IWordID w1 : wordID1) {  // for each sense of the first word
          for (IWordID w2 : wordID2) {  // for each sense of the second word
            ISynsetID syn1 = w1.getSynsetID();
            ISynsetID syn2 = w2.getSynsetID();
            try {
              similarityVal = sim.wuPalmer(syn1, syn2) * 10; // in the input file values are 0-10
            } catch (NullPointerException e) {  // this happens even if synsets are not null
              similarityVal = 0;
            }
            if (similarityVal > maxSimilarityVal) {
              maxSimilarityVal = similarityVal;
            }
          }
        }
        results.add(maxSimilarityVal);
      }
    }
    // Transform collections into arrays
    valuesArr = new double[values.size()];
    for (int i = 0; i < valuesArr.length; i++) {
      valuesArr[i] = values.get(i);
    }
    resultsArr = new double[results.size()];
    for (int i = 0; i < resultsArr.length; i++) {
      resultsArr[i] = results.get(i);
    }
    // show results
    System.out.print("Pearson using Wu & Palmer: ");
    System.out.println(sim.pearson(valuesArr, resultsArr));
    System.out.print("Spearman using Wu & Palmer: ");
    System.out.println(sim.spearman(valuesArr, resultsArr));
    
    results.clear();
    
    // shortest path
    for (int i = 0; i < words1.size(); ++i) { // for every row
      List<IWordID> wordID1 = null;
      List<IWordID> wordID2 = null;
      boolean exception = false;
      maxSimilarityVal = Double.NEGATIVE_INFINITY;
      try {
        IIndexWord idxWord1 = dict.getIndexWord(words1.get(i), POS.NOUN);
        wordID1 = idxWord1.getWordIDs();
        IIndexWord idxWord2 = dict.getIndexWord(words2.get(i), POS.NOUN);
        wordID2 = idxWord2.getWordIDs();
      }
      catch(NullPointerException e){
        results.add(new Double(0));
        exception = true;
      }
      if (!exception) {
        for (IWordID w1 : wordID1) {  // for each sense of the first word
          for (IWordID w2 : wordID2) {  // for each sense of the second word
            ISynsetID syn1 = w1.getSynsetID();
            ISynsetID syn2 = w2.getSynsetID();
            try {
              similarityVal = sim.shortestPath(syn1, syn2) / 10;
            } catch (NullPointerException e) {  // this happens even if synsets are not null
              similarityVal = 0;
            }
            if (similarityVal > maxSimilarityVal) {
              maxSimilarityVal = similarityVal;
            }
          }
        }
        results.add(maxSimilarityVal);
      }
    }
    // Transform collections into arrays
    valuesArr = new double[values.size()];
    for (int i = 0; i < valuesArr.length; i++) {
      valuesArr[i] = values.get(i);
    }
    resultsArr = new double[results.size()];
    for (int i = 0; i < resultsArr.length; i++) {
      resultsArr[i] = results.get(i);
    }
    // show results
    System.out.print("Pearson using shortest path: ");
    System.out.println(sim.pearson(valuesArr, resultsArr));
    System.out.print("Spearman using shortest path: ");
    System.out.println(sim.spearman(valuesArr, resultsArr));
    
    results.clear();
    
    // leakcock chodorow
    for (int i = 0; i < words1.size(); ++i) { // for every row
      List<IWordID> wordID1 = null;
      List<IWordID> wordID2 = null;
      boolean exception = false;
      maxSimilarityVal = Double.NEGATIVE_INFINITY;
      try {
        IIndexWord idxWord1 = dict.getIndexWord(words1.get(i), POS.NOUN);
        wordID1 = idxWord1.getWordIDs();
        IIndexWord idxWord2 = dict.getIndexWord(words2.get(i), POS.NOUN);
        wordID2 = idxWord2.getWordIDs();
      }
      catch(NullPointerException e){
        results.add(new Double(0));
        exception = true;
      }
      if (!exception) {
        for (IWordID w1 : wordID1) {  // for each sense of the first word
          for (IWordID w2 : wordID2) {  // for each sense of the second word
            ISynsetID syn1 = w1.getSynsetID();
            ISynsetID syn2 = w2.getSynsetID();
            try {
              similarityVal = sim.leakcockChodorow(syn1, syn2) * 10; // in the input file values are 0-10
            } catch (NullPointerException e) {  // this happens even if synsets are not null
              similarityVal = 0;
            }
            if (similarityVal > maxSimilarityVal) {
              maxSimilarityVal = similarityVal;
            }
          }
        }
        results.add(maxSimilarityVal);
      }
    }
    // Transform collections into arrays
    valuesArr = new double[values.size()];
    for (int i = 0; i < valuesArr.length; i++) {
      valuesArr[i] = values.get(i);
    }
    resultsArr = new double[results.size()];
    for (int i = 0; i < resultsArr.length; i++) {
      resultsArr[i] = results.get(i);
    }
    // show results
    System.out.print("Pearson using Leakcock & Chodorow: ");
    System.out.println(sim.pearson(valuesArr, resultsArr));
    System.out.print("Spearman using Leakcock & Chodorow: ");
    System.out.println(sim.spearman(valuesArr, resultsArr));
  }

}
