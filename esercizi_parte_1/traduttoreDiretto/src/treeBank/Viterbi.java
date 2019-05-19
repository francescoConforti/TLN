/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author confo
 */
public class Viterbi {

  private final Map<String, Map<String, Integer>> map;
  private final Map<String, Map<String, Integer>> transitions;

  public Viterbi(Map<String, Map<String, Integer>> corpus, Map<String, Map<String, Integer>> transitions) {
    map = corpus;
    this.transitions = transitions;
  }

  public double posWordProbability(String word, String pos) {
    double res;
    if (Reader.countWord(map, word) <= 0) { // manage unknown words
      if (pos.equals("PROPN")) {
        res = 1;
      } else {
        res = 1 / (Pos.values().length - 2);
      }
    } else {
      // add 1 to log to exclude negatives
      res = Math.log(((double) Reader.countWordPos(map, word, pos) / Reader.countPos(map, pos))+1);
      if (Double.isInfinite(res)) {  // discard infinities caused by log
        res = 0;
      }
    }
    return res;
  }

  public double posPosProbability(String pos, String precedingPos) {
    // add 1 to log to exclude negatives
    double res = Math.log(((double) Reader.countTransition(transitions, pos, precedingPos) / Reader.countPos(map, precedingPos))+1);
    if (Double.isInfinite(res)) {  // discard infinities caused by log
      res = 0;
    }
    return res;
  }

  public List<Pair> viterbi(String text) {
    List<Pair> res = new ArrayList<>();
    String[] words = text.split("(?=\\p{Punct})|(?<=\\p{Punct})|\\W");  // split on whitespace and punctuation, keeping punctuation
    double[][] viterbiMatrix = new double[Pos.values().length][words.length]; // Start and End already in Pos (first and last)
    int[][] backpointer = new int[Pos.values().length][words.length];
    double currentViterbi, currentBackpointer, maxBackpointer = 0;
    Pos[] posValues = Pos.values().clone(); // for efficiency
    // initialization step
    for (int s = 1; s < posValues.length - 1; ++s) { // don't consider START and END
      double a = posPosProbability(posValues[s].name(), Pos.START.name());
      double b = posWordProbability(words[0], posValues[s].name());
      viterbiMatrix[s][0] = a * b;
      backpointer[s][0] = 0;
    }
    // recursion step
    for (int t = 1; t < words.length; ++t) {  // t = 0 in initialization
      for (int s = 1; s < posValues.length - 1; ++s) { // don't consider START and END
        for (int sprec = 1; sprec < posValues.length - 1; ++sprec) {
          double a = posPosProbability(posValues[s].name(), posValues[sprec].name());
          double b = posWordProbability(words[t], posValues[s].name());
          currentViterbi = viterbiMatrix[sprec][t - 1] * a * b;
          if (currentViterbi > viterbiMatrix[s][t]) {
            viterbiMatrix[s][t] = currentViterbi;
          }
          currentBackpointer = viterbiMatrix[sprec][t - 1] * a;
          if (currentBackpointer > maxBackpointer) {
            maxBackpointer = currentBackpointer;
            backpointer[s][t] = sprec;
          }
        }
        maxBackpointer = 0;
      }
    }
    // termination step
    for (int s = 1; s < posValues.length - 1; ++s) {
      double a = posPosProbability(Pos.END.name(), posValues[s].name());
      currentViterbi = viterbiMatrix[s][words.length - 1] * a;
      if (currentViterbi > viterbiMatrix[s][words.length - 1]) {
        viterbiMatrix[Pos.END.ordinal()][words.length - 1] = currentViterbi;
      }
      // here currentViterbi and currentBackpointer are the same
      if (currentViterbi > maxBackpointer) {
        maxBackpointer = currentViterbi;
        backpointer[Pos.END.ordinal()][words.length - 1] = s;
      }
    }
    // return the list of PoS associated with the words
    int pointer = backpointer[Pos.END.ordinal()][words.length - 1];
    //res.add(new Pair(words[words.length-1], posValues[pointer]));
    for (int i = words.length - 1; i >= 0; --i) {
      res.add(new Pair(words[i], posValues[pointer]));
      pointer = backpointer[pointer][i];
    }
    Collections.reverse(res);
    return res;
  }

  public static void main(String[] args) {
    String path = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/universal_dependency/ud-treebanks-v2.3/UD_English-GUM/en_gum-ud-dev.conllu";
    Viterbi v = new Viterbi(Reader.treeBankToMap(path), Reader.treeBankToTagTransitions(path));
    System.out.println(v.viterbi("In his self-appointed role of emperor, Norton issued numerous decrees on matters of the state."));
  }
}
