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
  
  private final String dictPath = "./dizionario/dict.txt";
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
        splits = line.split("\t");
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
    for(int i = 0; i < viterbi.size(); ++i){
      Pair p = viterbi.get(i);
      if(translations.get(p.getWord()) != null){
        if(viterbi.get(i+1) != null){
          String compositeWord = p.getWord() + " " + viterbi.get(i+1).getWord();
          if(translations.get(compositeWord) != null){
            output = output + translate(compositeWord) + " ";
            ++i;
          }
          else{
            output = output + translate(p.getWord()) + " ";
          }
        }
      }
      else if(p.getPos().equals(Pos.PUNCT)){
        output = output + p.getWord() + " ";
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
    String path_train = "./universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-train.conllu";
    String[] input = { "The black droid then lowers Vader's mask and helmet onto his head.",
                       "These are not the droids you're looking for.",
                       "Your friends may escape, but you're doomed."};
    for(int i = 0; i < input.length; ++i){
      String eng = input[i].toLowerCase();
      Traduttore t = new Traduttore();
      Viterbi v = new Viterbi(Reader.treeBankToMap(path_train), Reader.treeBankToTagTransitions(path_train));
      int[] posCount = new int[Pos.values().length];
      for(int j = 0; j < posCount.length; ++j){
        posCount[j] = Reader.countPos(Reader.treeBankToMap(path_train), Pos.values()[j].name());
      }
      List<Pair> viterbiResult = v.viterbi(eng, posCount);
      System.out.println("Frase " + (i+1));
      System.out.println(viterbiResult);
      System.out.println(t.translate(viterbiResult));
      System.out.println("");
    }
  }
}
