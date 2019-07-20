/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dizionario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import treeBank.Pair;
import treeBank.Pos;
import treeBank.Reader;
import treeBank.Viterbi;

/**
 *
 * @author confo
 */
public class Traduttore {
  
  private final String dictPath = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/dizionario/dict.txt";
  private final Map<String, String> translations;
  
  public Traduttore(){
    translations = new HashMap<>();
    parseDict();
  }
  
  private void parseDict(){
    try (BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
      String line;
      String[] splits;
      while ((line = br.readLine()) != null) {
        splits = line.split(" ");
        translations.put(splits[0], splits[1]);
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public String translate(List<Pair> viterbi){
    String output = "";
    // Simple grammatical corrections
    for(int i = 0; i < viterbi.size()-1; ++i){
      Pair p = viterbi.get(i);
      Pair next = viterbi.get(i+1);
      if(p.getPos().equals(Pos.AUX) || p.getPos().equals(Pos.VERB)){
        if(next.getPos().equals(Pos.PART)){
          Collections.swap(viterbi, i, i+1);
        }
      }
      if(p.getPos().equals(Pos.ADJ)){
        if(next.getPos().equals(Pos.NOUN)){
          Collections.swap(viterbi, i, i+1);
        }
      }
      if(p.getPos().equals(Pos.PROPN)){
        if(next.getPos().equals(Pos.PART)){
          Collections.swap(viterbi, i, i+1);
        }
      }
    }
    // Direct translation
    for(Pair p : viterbi){
      if(p.getPos().equals(Pos.PUNCT)){
        output = output + p.getWord() + " ";
      }
      else if(translations.get(p.getWord()) != null){
        output = output + translate(p.getWord()) + " ";
      }
      else{
        output = output + p.getWord() + " ";
      }
    }
    return output;
  }
  
  private String translate(String word){
    return translations.get(word);
  }
  
  public static void main(String[] args){
    String path_train = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-train.conllu";
    String input = "Your friends may escape, but you are doomed";
    input = input.toLowerCase();
    Traduttore t = new Traduttore();
    Viterbi v = new Viterbi(Reader.treeBankToMap(path_train), Reader.treeBankToTagTransitions(path_train));
    int[] posCount = new int[Pos.values().length];
    for(int i = 0; i < posCount.length; ++i){
      posCount[i] = Reader.countPos(Reader.treeBankToMap(path_train), Pos.values()[i].name());
    }
    List<Pair> viterbiResult = v.viterbi(input, posCount);
    System.out.println(viterbiResult);
    System.out.println(t.translate(viterbiResult));
  }
}
