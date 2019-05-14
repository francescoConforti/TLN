/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
  
  public List<Pair> Viterbi(String text){
    String[] words = text.split("(?=\\p{Punct})|(?<=\\p{Punct})|\\W");  // split on whitespace and punctuation, keeping punctuation
    double[][] viterbiMatrix = new double[Pos.values().length][words.length]; // Start and End already in Pos (first and last)
    int[][] backpointer = new int[Pos.values().length][words.length];
    // TODO probabilities after START
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
}
