/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author confo
 */
public class Reader {

  public static Map<String, Map<String, Integer>> treeBankToMap(String path) {
    Map<String, Map<String, Integer>> data = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line, word, pos;
      String[] splits;
      Map<String, Integer> tmp;
      // initialize START and END keys to count total sentences in treebank
      tmp = new HashMap<>();
      tmp.put("START", 0);
      data.put("START", tmp);
      tmp = new HashMap<>();
      tmp.put("END", 0);
      data.put("END", tmp);
      // parse treebank
      while ((line = br.readLine()) != null) {
        if (!(line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) && line.charAt(0) != '#') { // ignore comments and empty lines
          splits = line.split("\t"); // splits[1] is token and splits[3] is PoS
          word = splits[1];
          pos = splits[3];
          if (data.containsKey(word)) { // the word has already been found
            tmp = data.get(word);
            if (tmp.containsKey(pos)) {  // the word was found with the same PoS
              data.get(word).put(pos, tmp.get(pos) + 1);
            } else {  // the word is found with this PoS for the first time
              data.get(word).put(pos, 1);
            }
          } else { // the word is found for the first time
            tmp = new HashMap<>();
            tmp.put(pos, 1);
            data.put(word, tmp);
          }
        }
        else if(line.trim().equals("")){ // update START and END for new sentence
          data.get("START").put("START", data.get("START").get("START") + 1);
          data.get("END").put("END", data.get("END").get("END") + 1);
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return data;
  }

  public static Map<String, Map<String, Integer>> treeBankToTagTransitions(String path) {
    Map<String, Map<String, Integer>> transitions = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line, prevPos, pos = "START";
      String[] splits;
      Map<String, Integer> tmp = new HashMap<>();
      // initialize END PoS
      for(Pos p : Pos.values()){
        tmp.put(p.name(), 0);
      }
      transitions.put("END", tmp);
      // Parse treebank
      while ((line = br.readLine()) != null) {
        if (!(line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) && line.charAt(0) != '#') { // ignore comments and empty lines
          splits = line.split("\t"); // splits[3] is PoS
          prevPos = pos;
          pos = splits[3];
          if (pos != null && prevPos != null) {
            if (transitions.containsKey(pos)) { // the PoS has already been found
              tmp = transitions.get(pos);
              if (tmp.containsKey(prevPos)) {  // the transition was found with the same PoS
                transitions.get(pos).put(prevPos, tmp.get(prevPos) + 1);
              } else {  // the transition is found with this PoS for the first time
                transitions.get(pos).put(prevPos, 1);
              }
            } else { // the PoS is found for the first time
              tmp = new HashMap<>();
              tmp.put(prevPos, 1);
              transitions.put(pos, tmp);
            }
          }
        } else if(line.trim().equals("")){  // the sentence in the treebank is finished
          tmp = transitions.get("END");
          transitions.get("END").put(pos, tmp.get(pos) + 1);
          pos = prevPos = "START";
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return transitions;
  }

  public static int countWord(Map<String, Map<String, Integer>> map, String word) {
    int res = 0;
    if (map.containsKey(word)) {
      for (Integer num : map.get(word).values()) {
        res += num;
      }
    }
    return res;
  }
  
  public static int countWordPos(Map<String, Map<String, Integer>> map, String word, String pos){
    int res = 0;
    if (map.containsKey(word)) {
      if(map.get(word).containsKey(pos)){
        res = map.get(word).get(pos);
      }
    }
    return res;
  }

  public static int countPos(Map<String, Map<String, Integer>> map, String pos) {
    int res = 0;
    for (Map<String, Integer> m : map.values()) {
      for (Map.Entry<String, Integer> entry : m.entrySet()) {
        if (entry.getKey().equals(pos)) {
          res += entry.getValue();
        }
      }
    }
    return res;
  }
  
  public static int countTransition(Map<String, Map<String, Integer>> transitions, String pos, String precedingPos){
    int res = 0;
    if(transitions.containsKey(pos)){
      if(transitions.get(pos).containsKey(precedingPos)){
        res = transitions.get(pos).get(precedingPos);
      }
    }
    return res;
  }
  
  public static Map<String, List<Pair>> treeBankToSentences(String path){
    Map<String, List<Pair>> res = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      String text = "";
      while ((line = br.readLine()) != null) {
        if(!line.trim().equals("")){
          if(line.charAt(0) == '#'){  // preamble
            if(line.contains("# text = ")){
              text = line.replace("# text = ", ""); // only actual text
            }
          } else { // do nothing between two sentences
            List<Pair> list = new ArrayList<>();
            while(line != null && !line.trim().equals("") && Character.isDigit(line.charAt(0))){ // words
              String[] splits = line.split("\t"); // splits[1] is token and splits[3] is PoS
              list.add(new Pair(splits[1], Pos.valueOf(splits[3])));
              line = br.readLine();
            }
            res.put(text, list);
          }
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return res;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String path = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-dev.conllu";
    final String TESTWORD = "declare", TESTPOS = "PROPN", PRECPOS = "PUNCT";
    Map<String, Map<String, Integer>> map = Reader.treeBankToMap(path);
    Map<String, Map<String, Integer>> transitions = Reader.treeBankToTagTransitions(path);
    System.out.println(map);
    System.out.println(transitions);
    System.out.println(Reader.treeBankToSentences(path));
    System.out.println("Values for \"" + TESTWORD + "\": " + map.get(TESTWORD));
    System.out.println("word \"" + TESTWORD + "\" appears " + Reader.countWord(map, TESTWORD) + " times");
    System.out.println("word \"" + TESTWORD + "\" appears with pos " + TESTPOS + " " + Reader.countWordPos(map, TESTWORD, TESTPOS) + " times");
    System.out.println("Pos " + TESTPOS + " appears " + Reader.countPos(map, TESTPOS) + " times");
    System.out.println("Pos " + TESTPOS + " appears after " + PRECPOS + " " + Reader.countTransition(transitions, TESTPOS, PRECPOS) + " times");
  }

}
