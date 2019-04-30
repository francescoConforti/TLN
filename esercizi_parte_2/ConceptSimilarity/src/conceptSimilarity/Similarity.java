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
    double num, den;
    ISynsetID lca = utils.leastCommonAncestor(cs1, cs2);
    int depthLca = utils.findDepth(lca);
    num = 2 * depthLca;
    den = (depthLca + utils.descendantDistance(lca, cs1)) + (depthLca + utils.descendantDistance(lca, cs2));
    return (double)(num / den);
  }
  
  /**
   * return double for consistency with other measures
   * @param cs1
   * @param cs2
   * @return
   */
  public double shortestPath(ISynsetID cs1, ISynsetID cs2){
    //return (double)(2 * utils.maxDepth() - utils.distance(cs1, cs2));
    return (double)(2 * 19 - utils.distance(cs1, cs2));  // removing the computation greatly improves performance
  }
  
  public double leakcockChodorow(ISynsetID cs1, ISynsetID cs2){
    double num = utils.distance(cs1, cs2) + 1;
    //double den = 2 * utils.maxDepth();
    double den = 2 * 19 + 1;  // removing the computation greatly improves performance
    double n = num / den;
    /*if(n <= 0){
      n = Double.MIN_VALUE;
    }*/
    return -Math.log(n);
  }
  
  /**
   * x and y must have the same length
   * @param x
   * @param y
   * @return Pearson correlation coefficient
   */
  public double pearson(double[] x, double[] y){
    double num = Statistics.covariance(x, y, x.length);
    double den = Statistics.standardDeviation(x) * Statistics.standardDeviation(y);
    return num / den;
  }
  
  /**
   * x and y must have the same length
   * @param x
   * @param y
   * @return spearman rank correlation coefficient
   */
  public double spearman(double[] x, double[] y){
    double[] xRanks = Statistics.ranks(x);
    double[] yRanks = Statistics.ranks(y);
    double num = Statistics.covariance(xRanks, yRanks, xRanks.length);
    double den = Statistics.standardDeviation(xRanks) * Statistics.standardDeviation(yRanks);
    return num / den;
  }
  
}
