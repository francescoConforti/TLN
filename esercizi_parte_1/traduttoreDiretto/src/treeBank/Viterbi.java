/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

import java.util.List;
import java.util.Map;

/**
 *
 * @author confo
 */
public class Viterbi {
  
  private final Map<String, Map<String, Integer>> map;
  private final Map<String, Map<String, Integer>> transitions;
  
  public Viterbi(Map<String, Map<String, Integer>> corpus, Map<String, Map<String, Integer>> transitions){
    map = corpus;
    this.transitions = transitions;
  }
  
  public double posWordProbability(String pos, String word){
    return Math.log((double) Reader.countWordPos(map, word, pos) / Reader.countPos(map, pos));
  }
  
  public double posPosProbability(String pos, String precedingPos){
    return Math.log((double) Reader.countTransition(transitions, precedingPos, pos) / Reader.countPos(map, precedingPos));
  }
  
  public List<Pair> viterbi(String text){
    String[] words = text.split("(?=\\p{Punct})|(?<=\\p{Punct})|\\W");  // split on whitespace and punctuation, keeping punctuation
    double[][] viterbiMatrix = new double[Pos.values().length][words.length]; // Start and End already in Pos (first and last)
    int[][] backpointer = new int[Pos.values().length][words.length];
    Pos[] posValues = Pos.values().clone(); // for efficiency
    // initialization step
    for(int s = 1; s < posValues.length -1; ++s){ // don't consider START and END
      double a = posPosProbability(posValues[s].name(), Pos.START.name());
      double b = posWordProbability(words[0], posValues[s].name());
      viterbiMatrix[s][0] = a * b;
      backpointer[s][0] = 0;
    }
    // recursion step
    for(int t = 1; t < words.length; ++t){  // t = 0 in initialization
      for(int s = 1; s < posValues.length -1; ++s){ // don't consider START and END
        double currentViterbi, currentBackpointer, maxBackpointer = 0;
        for(int sprec = 1; sprec < posValues.length -1; ++sprec){
          double a = posPosProbability(posValues[s].name(), posValues[sprec].name());
          double b = posWordProbability(words[t], posValues[s].name());
          currentViterbi = viterbiMatrix[sprec][t -1] * a * b;
          if(currentViterbi > viterbiMatrix[s][t]){
            viterbiMatrix[s][t] = currentViterbi;
          }
          currentBackpointer = viterbiMatrix[sprec][t-1] * a;
          if(currentBackpointer > maxBackpointer){
            maxBackpointer = currentBackpointer;
            backpointer[s][t] = sprec;
          }
        }
      }
    }
    // TODO: termination step
    return null;
  }
  
  public class Pair{
    
    private final String word, pos;
    
    public Pair(String word, String pos){
      this.word = word;
      this.pos = pos;
    }

    public String getWord(){
      return word;
    }

    public String getPos(){
      return pos;
    }
  }
  
  public static void main(String[] args){
    String path = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDirect/universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-dev.conllu";
    Viterbi v = new Viterbi(Reader.treeBankToMap(path), Reader.treeBankToTagTransitions(path));
    v.viterbi("This is an english sentence.");
  }
}
