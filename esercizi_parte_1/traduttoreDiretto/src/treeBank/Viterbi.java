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
  private final double NEWWORD = 0.00001;
  private final double NEWPOS = 0.00001;
  private final double NEWTRANS = 0.00001;

  public Viterbi(Map<String, Map<String, Integer>> corpus, Map<String, Map<String, Integer>> transitions) {
    map = corpus;
    this.transitions = transitions;
  }

  public double posWordProbability(String word, Pos pos, int[] posCount) {
    double res;
    if (Reader.countWord(map, word) <= 0) { // manage unknown words
      if (pos.equals(Pos.PROPN)) {
        res = NEWWORD;
      } else {
        res = NEWWORD / (Pos.values().length - 2);
      }
    } else {
      // add 1 to log to exclude negatives
      double wordPos = Reader.countWordPos(map, word, pos.name());
      if(wordPos <= 0){
        wordPos = NEWPOS;
      }
      res = Math.log((wordPos / posCount[pos.ordinal()])+1);
      if (Double.isInfinite(res)) {  // discard infinities caused by log
        res = 0;
      }
    }
    return res;
  }

  public double posPosProbability(Pos pos, Pos precedingPos, int[] posCount) {
    // add 1 to log to exclude negatives
    double transitionCount = Reader.countTransition(transitions, pos.name(), precedingPos.name());
    if(transitionCount <= 0){
      transitionCount = NEWTRANS;
    }
    double res = Math.log((transitionCount / posCount[precedingPos.ordinal()])+1);
    if (Double.isInfinite(res)) {  // discard infinities caused by log
      res = 0;
    }
    return res;
  }

  public List<Pair> viterbi(String text, int[] posCount) {
    List<Pair> res = new ArrayList<>();
    String[] words = text.replaceAll("\\s+(?=[!\"#$%&()*+,./:;<=>?@\\^_{|}~`\\[\\]“”])", "").split("\\s+|(?=[!\"#$%&'()*+,./:;<=>?@\\^_{|}~`\\[\\]“”])|(?<=[!\"#$%&()*+,./:;<=>?@\\^_{|}~`\\[\\]“”])");  //split on whitespace and punctuation, keeping punctuation
    double[][] viterbiMatrix = new double[Pos.values().length][words.length]; // Start and End already in Pos (first and last)
    int[][] backpointer = new int[Pos.values().length][words.length];
    double currentViterbi, currentBackpointer, maxBackpointer = 0;
    Pos[] posValues = Pos.values().clone(); // for efficiency
    // initialization step
    for (int s = 1; s < posValues.length - 1; ++s) { // don't consider START and END
      double a = posPosProbability(posValues[s], Pos.START, posCount);
      double b = posWordProbability(words[0], posValues[s], posCount);
      viterbiMatrix[s][0] = a * b;
      backpointer[s][0] = 0;
    }
    // recursion step
    for (int t = 1; t < words.length; ++t) {  // t = 0 in initialization
      for (int s = 1; s < posValues.length - 1; ++s) { // don't consider START and END
        for (int sprec = 1; sprec < posValues.length - 1; ++sprec) {
          double a = posPosProbability(posValues[s], posValues[sprec], posCount);
          double b = posWordProbability(words[t], posValues[s], posCount);
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
      double a = posPosProbability(Pos.END, posValues[s], posCount);
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
    String path_train = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/universal_dependency/ud-treebanks-v2.3/UD_English-LinES/en_lines-ud-train.conllu";
    String path_test = "/home/confo/UNI/magistrale/TLN/esercizi_parte_1/traduttoreDiretto/universal_dependency/ud-treebanks-v2.3/UD_English-LinES/en_lines-ud-test.conllu";
    boolean isSentenceEqual;
    Map<String, List<Pair>> sentences_test = Reader.treeBankToSentences(path_test);
    Viterbi v = new Viterbi(Reader.treeBankToMap(path_train), Reader.treeBankToTagTransitions(path_train));
    List<Pair> viterbiResult;
    Pair viterbiPair = null;
    Pair entryPair = null;
    int equalSentences = 0, equalWords = 0, totalSentences = sentences_test.size(), totalWords = 0, numSentence = 1;
    int[] posCount = new int[Pos.values().length];
    for(int i = 0; i < posCount.length; ++i){
      posCount[i] = Reader.countPos(Reader.treeBankToMap(path_train), Pos.values()[i].name());
    }
    for(Map.Entry<String, List<Pair>> entry : sentences_test.entrySet()){
      viterbiResult = v.viterbi(entry.getKey(), posCount);
      if(viterbiResult.size() == entry.getValue().size()){
        isSentenceEqual = true;
        for(int i = 0; i < viterbiResult.size(); ++i){
          viterbiPair = viterbiResult.get(i);
          entryPair = entry.getValue().get(i);
          if(viterbiPair.getWord().equals(entryPair.getWord()) && viterbiPair.getPos().equals(entryPair.getPos())){ // both words and both PoS are the same
            ++equalWords;
            ++totalWords;
          } else{
            ++totalWords;
            isSentenceEqual = false;
          }
        }
        if(isSentenceEqual){
          ++equalSentences;
        }
      }
      System.out.println("another sentence done " + numSentence + "\\" + totalSentences);
      System.out.println(entry.getKey());
      System.out.println("Viterbi:");
      System.out.println(viterbiResult);
      System.out.println("Treebank:");
      System.out.println(entry.getValue());
      System.out.println("\n");
      ++numSentence;
    }
    System.out.println("Performance of Viterbi on test set:");
    System.out.println("Guessed " + equalSentences + " sentences right out of " + totalSentences);
    System.out.println("Guessed " + equalWords + " words right out of " + totalWords + " analyzed (" + (double)equalWords/totalWords + ")");
  }
}
