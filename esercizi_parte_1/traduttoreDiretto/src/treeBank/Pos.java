/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

// Source: https://universaldependencies.org/u/pos/index.html
/**
 *
 * @author confo
 */
public enum Pos {
  START,  // for Viterbi
  ADJ, ADP, PUNCT,
  ADV, AUX, SYM,
  INTJ, CCONJ, X,
  NOUN, DET, 	 
  PROPN, NUM, 	 
  VERB, PART, 	 
  PRON, SCONJ,
  END // for Viterbi
}
