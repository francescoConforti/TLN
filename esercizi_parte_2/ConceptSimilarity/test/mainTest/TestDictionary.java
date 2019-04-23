package mainTest;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.input.KeyCode.I;

public class TestDictionary {

  public void testDictionary() throws IOException {
// construct the URL to the Wordnet dictionary directory
    //String wnhome = System.getenv(" WNHOME ");
    //String path = wnhome + File.separator + " dict ";
    URL url = new URL("file", null, "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
// construct the dictionary object and open it
    IDictionary dict = new Dictionary(url);
    dict.open();
// look up first sense of the word " dog "
    IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
    IWordID wordID = idxWord.getWordIDs().get(0);
    IWord word = dict.getWord(wordID);
    System.out.println(" Id = " + wordID);
    System.out.println(" Lemma = " + word.getLemma());
    System.out.println(" Gloss = " + word.getSynset().getGloss());
  }

  public void testRAMDictionary(File wnDir) throws Exception {
// construct the dictionary object and open it
    IRAMDictionary dict = new RAMDictionary(wnDir, ILoadPolicy.NO_LOAD);
    dict.open();
// do something slowly
    trek(dict);
// now load into memory
    System.out.print("\nLoading Wordnet into memory ...");
    long t = System.currentTimeMillis();
    dict.load(true);
    System.out.printf("done (%1d msec ) \n", System.currentTimeMillis() - t);
// do the same thing again , only faster
    trek(dict);
  }

  public void trek(IDictionary dict) {
    int tickNext = 0;
    int tickSize = 20000;
    int seen = 0;
    System.out.print(" Treking across Wordnet ");
    long t = System.currentTimeMillis();
    for (POS pos : POS.values()) {
      for (Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.
              hasNext();) {
        for (IWordID wid : i.next().getWordIDs()) {
          seen += dict.getWord(wid).getSynset().getWords().size();
          if (seen > tickNext) {
            System.out.print('.');
            tickNext = seen + tickSize;
          }
        }
      }
    }
    System.out.printf(" done (%1d msec ) \n ", System.currentTimeMillis() - t);
    System.out.println(" In my trek I saw " + seen + " words ");
  }
  
  public void getSynonyms(IDictionary dict) {
// look up first sense of the word " dog "
    IIndexWord idxWord = dict.getIndexWord("dog", POS.NOUN);
    IWordID wordID = idxWord.getWordIDs().get(0); // 1 st meaning
    IWord word = dict.getWord(wordID);
    ISynset synset = word.getSynset();
// iterate over words associated with the synset
    for (IWord w : synset.getWords()) {
      System.out.println(w.getLemma());
    }
  }
  
  public void getHypernyms(IDictionary dict) {
// get the synset
    IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
    IWordID wordID = idxWord.getWordIDs().get(0); // 1 st meaning
    IWord word = dict.getWord(wordID);
    ISynset synset = word.getSynset();
// get the hypernyms
    List< ISynsetID> hypernyms
            = synset.getRelatedSynsets(Pointer.HYPERNYM);
// print out each h y p e r n y m s id and synonyms
    List< IWord> words;
    for (ISynsetID sid : hypernyms) {
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

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
    TestDictionary test = new TestDictionary();
    /*try {
      test.testDictionary();
    } catch (IOException ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    File file = new File("/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
    try {
      test.testRAMDictionary(file);
    } catch (Exception ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }*/
    URL url;
    IDictionary dict = null;
    try {
      url = new URL("file", null, "/home/confo/UNI/magistrale/TLN/esercizi_parte_2/ConceptSimilarity/altro/dict");
      dict = new Dictionary(url);
    } catch (MalformedURLException ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      dict.open();
    } catch (IOException ex) {
      Logger.getLogger(TestDictionary.class.getName()).log(Level.SEVERE, null, ex);
    }
    test.getSynonyms(dict);
    //test.getHypernyms(dict);
    
  }
}
