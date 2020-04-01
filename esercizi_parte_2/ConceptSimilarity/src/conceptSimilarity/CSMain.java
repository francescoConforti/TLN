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
    List<Double> wpResults = new ArrayList<>();
    List<Double> spResults = new ArrayList<>();
    List<Double> lcResults = new ArrayList<>();
    double[] valuesArr;
    double[] wpArr, spArr, lcArr;
    double wpMaxVal, spMaxVal, lcMaxVal;
    double wpVal, spVal, lcVal;
    int totalValues;
    
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
    totalValues = values.size();
    
    for (int i = 0; i < words1.size(); ++i) { // for every row
      List<IWordID> wordID1 = null;
      List<IWordID> wordID2 = null;
      boolean exception = false;
      wpMaxVal = Double.NEGATIVE_INFINITY;
      spMaxVal = Double.NEGATIVE_INFINITY;
      lcMaxVal = Double.NEGATIVE_INFINITY;
      try {
        IIndexWord idxWord1 = dict.getIndexWord(words1.get(i), POS.NOUN);
        wordID1 = idxWord1.getWordIDs();
        IIndexWord idxWord2 = dict.getIndexWord(words2.get(i), POS.NOUN);
        wordID2 = idxWord2.getWordIDs();
      }
      catch(NullPointerException e){
        wpResults.add(new Double(0));
        spResults.add(new Double(0));
        lcResults.add(new Double(0));
        exception = true;
      }
      if (!exception) {
        for (IWordID w1 : wordID1) {  // for each sense of the first word
          for (IWordID w2 : wordID2) {  // for each sense of the second word
            ISynsetID syn1 = w1.getSynsetID();
            ISynsetID syn2 = w2.getSynsetID();
            try {
              wpVal = sim.wuPalmer(syn1, syn2);
              spVal = Statistics.normalize(0, 2*19, sim.shortestPath(syn1, syn2));
              lcVal = Statistics.normalize(0, Math.log(2*19+1), sim.leakcockChodorow(syn1, syn2));
            } catch (NullPointerException e) {  // this happens even if synsets are not null
              wpVal = spVal = lcVal = 0;
            }
            if (wpVal > wpMaxVal) {
              wpMaxVal = wpVal;
            }
            if (spVal > spMaxVal) {
              spMaxVal = spVal;
            }
            if (lcVal > lcMaxVal) {
              lcMaxVal = lcVal;
            }
          }
        }
        wpResults.add(wpMaxVal);
        spResults.add(spMaxVal);
        lcResults.add(lcMaxVal);
      }
      System.out.println(i+1 + "\\" + totalValues);  // Print row number (for speed evaluation)
    }
    // Transform collections into arrays
    valuesArr = new double[values.size()];
    for (int i = 0; i < valuesArr.length; i++) {
      valuesArr[i] = values.get(i) / 10; // in the input file values are 0-10
    }
    wpArr = new double[wpResults.size()];
    for (int i = 0; i < wpArr.length; i++) {
      wpArr[i] = wpResults.get(i);
    }
    spArr = new double[spResults.size()];
    for (int i = 0; i < spArr.length; i++) {
      spArr[i] = spResults.get(i);
    }
    lcArr = new double[lcResults.size()];
    for (int i = 0; i < lcArr.length; i++) {
      lcArr[i] = lcResults.get(i);
    }
    // show results
    System.out.print("Pearson using Wu & Palmer: ");
    System.out.println(sim.pearson(valuesArr, wpArr));
    System.out.print("Spearman using Wu & Palmer: ");
    System.out.println(sim.spearman(valuesArr, wpArr));
    
    System.out.print("Pearson using shortest path: ");
    System.out.println(sim.pearson(valuesArr, spArr));
    System.out.print("Spearman using shortest path: ");
    System.out.println(sim.spearman(valuesArr, spArr));
    
    System.out.print("Pearson using Leakcock & Chodorow: ");
    System.out.println(sim.pearson(valuesArr, lcArr));
    System.out.print("Spearman using Leakcock & Chodorow: ");
    System.out.println(sim.spearman(valuesArr, lcArr));
  }

}
