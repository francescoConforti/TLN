/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treeBank;

/**
 *
 * @author confo
 */
public class Pair {

  private final String word;
  private final Pos pos;

  public Pair(String word, Pos pos) {
    this.word = word;
    this.pos = pos;
  }

  public String getWord() {
    return word;
  }

  public Pos getPos() {
    return pos;
  }
  
  @Override
  public String toString(){
    return "{" + word + ", " + pos.name() + "}";
  }
}
