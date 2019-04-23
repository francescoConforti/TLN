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
  
  public double wuPalmer(ISynsetID cs1, ISynsetID cs2){
    int num, den;
    num = 2 * utils.findDepth(utils.leastCommonAncestor(cs1, cs2));
    den = utils.findDepth(cs1) + utils.findDepth(cs2);
    return num / den;
  }
  
  /**
   * return double for consistency with other measures
   * @param cs1
   * @param cs2
   * @return
   */
  public double shortestPath(ISynsetID cs1, ISynsetID cs2){
    return 2 * utils.maxDepth() - utils.distance(cs1, cs2);
  }
  
  public double leakcockChodorow(ISynsetID cs1, ISynsetID cs2){
    int num = utils.distance(cs1, cs2);
    int den = 2 * utils.maxDepth();
    return -Math.log(num / den);
  }
  
}
