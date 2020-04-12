/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senseidentification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author confo
 */
public class SenseIdentification {
  
  private final Map<String, List<String>> semEval;
  private final Map<String, Map<String, List<Float>>> nasari;
  
  public SenseIdentification(){
    semEval = readSemEval();
    nasari = readNasari();
  }
  
  public Map<String, List<String>> getSemEval() {
    return semEval;
  }

  public Map<String, Map<String, List<Float>>> getNasari() {
    return nasari;
  }
  
  /**
   * 
   * @return a Map<term, List<synset>>
   */
  private Map<String, List<String>> readSemEval(){
    Map<String, List<String>> res = new HashMap<>();
    String lastTerm = "";
    try {
      File f = new File("data/SemEval17_IT_senses2synsets.txt");
      Scanner scanner = new Scanner(f);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if(line.charAt(0) == '#'){
          lastTerm = line.substring(1).trim();
          res.put(lastTerm, new ArrayList<>());
        }
        else if(line.charAt(0) == 'b'){
          (res.get(lastTerm)).add(line.trim());
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return res;
  }
  
  /**
   * 
   * @return Map<term, map<synset, List<vector dimensions>>>
   */
  private Map<String, Map<String, List<Float>>> readNasari(){
    Map<String, Map<String, List<Float>>> res = new HashMap<>();
    try {
      File f = new File("data/mini_NASARI.tsv");
      Scanner scanner = new Scanner(f);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] data = line.trim().split("\t");
        String synset = data[0].split("__")[0];
        String term = data[0].split("__")[1];
        List<Float> vector = new ArrayList<>();
        for(int i = 1; i < data.length; ++i){
          vector.add(new Float(data[i]));
        }
        if(res.containsKey(term)){
          res.get(term).put(synset, vector);
        }else{
          HashMap<String, List<Float>> tmp = new HashMap<>();
          tmp.put(synset, vector);
          res.put(term, tmp);
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return res;
  }
  
  /**
   * 
   * @param term1
   * @param term2
   * @return an array of two strings, where each item is a synset
   */
  public String[] similarity(String term1, String term2){
    String[] res = new String[2];
    Map<String, List<Float>> n1 = nasari.get(term1);
    Map<String, List<Float>> n2 = nasari.get(term2);
    double similarity = 0, maxSimilarity = 0;
    for(Map.Entry<String, List<Float>> entry1 : n1.entrySet()){
      for(Map.Entry<String, List<Float>> entry2 : n2.entrySet()){
        similarity = Math.abs(cosineSimilarity(entry1.getValue(), entry2.getValue()));
        if(similarity > maxSimilarity){
          maxSimilarity = similarity;
          res[0] = entry1.getKey();
          res[1] = entry2.getKey();
        }
      }
    }
    return res;
  }
  
  /**
   * 
   * @param v1
   * @param v2
   * @return cosine similarity for the two vectors
   */
  private double cosineSimilarity(List<Float> v1, List<Float> v2){
    double num = 0, sum1 = 0, sum2 = 0;
    // suppose both lists have the same lenght (300)
    for(int i = 0; i < v1.size(); ++i){
      float f1 = v1.get(i);
      float f2 = v2.get(i);
      num += f1 * f2;
      sum1 += Math.pow(f1, 2);
      sum2 += Math.pow(f2, 2);
    }
    return num / (Math.sqrt(sum1) * Math.sqrt(sum2));
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
  }
  
}
