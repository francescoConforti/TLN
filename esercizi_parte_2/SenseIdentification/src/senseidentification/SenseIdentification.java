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
  
  // returns a Map<term, List<synset>>
  public Map<String, List<String>> readSemEval(){
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
  
  // returns a Map<term, map<synset, List<vector dimensions>>>
  public Map<String, Map<String, List<Float>>> readNasari(){
    Map<String, Map<String, List<Float>>> res = new HashMap<>();
    try {
      File f = new File("data/mini_NASARI.tsv");
      Scanner scanner = new Scanner(f);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] data = line.trim().split(" ");
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
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SenseIdentification si = new SenseIdentification();
    Map<String, List<String>> semeval = si.readSemEval();
    Map<String, Map<String, List<Float>>> nasari = si.readNasari();
    System.out.println("semeval:\n\n");
    for (Map.Entry<String, List<String>> entry : semeval.entrySet()) {
      String key = entry.getKey();
      List<String> value = entry.getValue();
      System.out.print(key + ": ");
      for(String s : value){
        System.out.print(s + ", ");
      }
      System.out.println("");
    }
  }
  
}
