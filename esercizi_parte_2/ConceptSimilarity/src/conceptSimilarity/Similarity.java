/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptSimilarity;

import edu.mit.jwi.item.ISynsetID;

/**
 *
 * @author confo
 */
public class Similarity {
  
  private final Utils utils;
  
  public Similarity(Utils utils){
    this.utils = utils;
  }
  
  public float wuPalmer(ISynsetID cs1, ISynsetID cs2){
    int num, den;
    num = 2 * utils.findDepth(utils.leastCommonAncestor(cs1, cs2));
    den = utils.findDepth(cs1) + utils.findDepth(cs2);
    return num / den;
  }
  
}
