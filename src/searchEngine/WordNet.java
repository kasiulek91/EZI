package searchEngine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNet {

	private static ILexicalDatabase db = new NictWordNet();
	// private static RelatednessCalculator[] rcs = {
	// new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new
	// WuPalmer(db),
	// new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
	// };

	private static RelatednessCalculator rcs = new WuPalmer(db);

	public WordNet() {
		System.setProperty("wordnet.database.dir",
				"C:\\Program Files\\WordNet\\2.1\\dict\\");
	}

	public Vector<String> searchExtendedWords(String word) {
		Vector<String> returnSet = new Vector<String>();
		Map<String, Double> wordsMap = new HashMap<String, Double>();
		NounSynset nounSynset;
		NounSynset[] hyponyms;

		String words[] = word.split("\\s+");

		WordNetDatabase database = WordNetDatabase.getFileInstance();

		for (int m = 0; m < words.length; m++) {
			Synset[] synsets = database.getSynsets(words[m], SynsetType.NOUN);

			System.out.println("\n" + "------for word: " + words[m]
					+ "--------");
			for (int i = 0; i < synsets.length; i++) {
				nounSynset = (NounSynset) (synsets[i]);
				hyponyms = nounSynset.getHyponyms();

				for (int j = 0; j < hyponyms.length; j++) {

					String hyponymWord = "" + hyponyms[j];
					hyponymWord = (String) hyponymWord.substring(
							hyponymWord.indexOf('[') + 1,
							hyponymWord.lastIndexOf(']'));
					for (String term : hyponymWord.split(",")) {
						// System.out.println(term);
						// returnSet.add(term);

						WS4JConfiguration.getInstance().setMFS(true);
						double s = rcs.calcRelatednessOfWords(word, term);
						// System.out.println(rcs.getClass().getName() + ": "
						// + word + "-" + term + "\t" + s);
						wordsMap.put(term, s);
					}
				}
			}

		}
		int i=0;
		for (Map.Entry<String, Double> entry : entriesSortedByValues(wordsMap)) {
			System.out.println("Key : " + entry.getKey() + " Value : "
					+ entry.getValue());
			
			returnSet.add(entry.getKey());
			i++;
			if(i>=5) {
				break;
			}
		}

		return returnSet;
	}
	
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}
