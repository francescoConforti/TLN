/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptSimilarity;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
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
    int maxDepth = Integer.MIN_VALUE, depth;
    List<ISynsetID> ancestors1 = allAncestors(cs1);
    List<ISynsetID> ancestors2 = allAncestors(cs2);
    for(ISynsetID anc1 : ancestors1){
      for(ISynsetID anc2 : ancestors2){
        if(anc1.equals(anc2)){
          depth = findDepth(anc1);
          if(depth > maxDepth){
            lca = anc1;
            maxDepth = depth;
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
   * Code mostly from DuckDuckGo
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
   *
   * @param cs1
   * @param cs2
   * @return distance between the two synsets according to their depth
   * using the hypernym relation
   */
  public int distance(ISynsetID cs1, ISynsetID cs2){
    int dist, depth1, depth2, depthLca;
    depth1 = findDepth(cs1);
    depth2 = findDepth(cs2);
    if((allAncestors(cs2)).contains(cs1)){  // cs2 is descendant of cs1
      dist = depth2 - depth1;
    }
    else if((allAncestors(cs1)).contains(cs2)){  // cs1 is descendant of cs2
      dist = depth1 - depth2;
    }
    else{ // cs1 and cs2 are not directly related
      depthLca = findDepth(leastCommonAncestor(cs1, cs2));
      dist = (depth1 - depthLca) + (depth2 - depthLca);
    }
    return dist;
  }
  
  /**
   *
   * @return max depth of the taxonomy in wordnet
   */
  public int maxDepth(){
    int depth = 0;
    // get the root synset (entity)
    IIndexWord idxWord = dict.getIndexWord("entity", POS.NOUN);
    IWordID wordID = idxWord.getWordIDs().get(0); // 1 st and only meaning
    IWord word = dict.getWord(wordID);
    ISynset synset = word.getSynset();
// get the hypernyms
    List< ISynsetID> hyponyms
            = synset.getRelatedSynsets(Pointer.HYPONYM);
    for(ISynsetID hypo : hyponyms){
      depth = Math.max(depth, maxDepthAux(hypo));
    }
    return depth;
  }
  
  private int maxDepthAux(ISynsetID syn){
    int depth = 0;
    List< ISynsetID> hyponyms
            = dict.getSynset(syn).getRelatedSynsets(Pointer.HYPONYM);
    for(ISynsetID hypo : hyponyms){
      depth = Math.max(depth, maxDepthAux(hypo));
    }
    return depth +1;
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
