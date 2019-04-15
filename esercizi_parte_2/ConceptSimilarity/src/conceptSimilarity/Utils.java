/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptSimilarity;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.Pointer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author confo
 */
public class Utils {
  
  private final IDictionary dict;
  
  public Utils(IDictionary dictionary){
    dict = dictionary;
  }
  
  /**
   * 
   * @param cs1
   * @param cs2
   * @return least common ancestor if it exists, null otherwise
   */
  public ISynsetID leastCommonAncestor(ISynsetID cs1, ISynsetID cs2){
    ISynsetID lca = null;
    int minDepth = Integer.MAX_VALUE;
    List<ISynsetID> ancestors1 = allAncestors(cs1);
    List<ISynsetID> ancestors2 = allAncestors(cs2);
    for(ISynsetID anc1 : ancestors1){
      for(ISynsetID anc2 : ancestors2){
        if(anc1.equals(anc2)){
          if(findDepth(anc1) < minDepth){
            lca = anc1;
          }
        }
      }
    }
    return lca;
  }
  
  /**
   * 
   * @param syn the synsetID
   * @return a list of all the ancestor SynsetIDs, including syn.
   */
  public List<ISynsetID> allAncestors(ISynsetID syn){
    // Initialize structures
    Set<ISynsetID> res = new HashSet<>();
    Queue<ISynsetID> q = new LinkedList<>();
    List<ISynsetID> hypernyms;
    res.add(syn);
    q.add(syn);
    // Store all hypernyms in a queue and go up until root for each sense
    while(!q.isEmpty()){
      syn = q.poll();
      hypernyms = dict.getSynset(syn).getRelatedSynsets(Pointer.HYPERNYM);
      res.addAll(hypernyms);
      for(ISynsetID s : hypernyms){
        q.add(s);
      }
    }
    hypernyms = new ArrayList<>(res);
    return hypernyms;
  }
  
  /**
   * 
   * @param syn
   * @return depth of the synset
   */
  public int findDepth(ISynsetID syn) {
		if (dict.getSynset(syn).getRelatedSynsets(Pointer.HYPERNYM).isEmpty()) { return 0; }
		List<Set<ISynsetID>> list = new ArrayList<>();
		Set<ISynsetID> set = new HashSet<>();
		set.add(syn);
		list.add(set);
		boolean topReached = false;
		int depth = -1;
		while (!topReached) {
			Set<ISynsetID> nextSet = new HashSet<>();
			for (ISynsetID s : list.get(list.size()-1)) {
				List<ISynsetID> hyperIDs = dict.getSynset(s).getRelatedSynsets(Pointer.HYPERNYM);
				if (!hyperIDs.isEmpty()) {
					for (ISynsetID hyperID : hyperIDs) { nextSet.add(hyperID); }
				} else {
					topReached = true;
					depth = list.size()-1;
					break;
				}
			}
			list.add(nextSet);
		}
		return depth;
	}
  
  /**
   * Code from the tutorial
   * @param syn
   */
  public void printSynsetIDList(List<ISynsetID> syn){
    syn.forEach((sid) -> {
      printSynsetID(sid);
    });
  }
  
  public void printSynsetID(ISynsetID sid){
    List< IWord> words;
    words = dict.getSynset(sid).getWords();
    System.out.print(sid + " { ");
    for (Iterator< IWord> i = words.iterator(); i.hasNext();) {
      System.out.print(i.next().getLemma());
      if (i.hasNext()) {
        System.out.print(" , ");
      }
    }
    System.out.println(" } ");
  }
  
}
