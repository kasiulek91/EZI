package searchEngine;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNet {

	private static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator rcs = new JiangConrath(db);

	// private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
	// new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
	// new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };

	// private static RelatednessCalculator rcs = new Lin(db);

	public WordNet() {
		System.setProperty("wordnet.database.dir",
				"C:\\Program Files\\WordNet\\2.1\\dict\\");
	}

	public Vector<String> searchExtendedWords(String word) {
		Vector<String> returnSet = new Vector<String>();
		returnSet.add(word);

		String words[] = word.split("\\s+");
		int size = words.length;

		if (size == 1) {
			returnSet.addAll(searchExtensionOneWord(words[0], 4));
		} else {

			returnSet.addAll(joinWords(searchExtensionOneWord(words[0], 2),
					Arrays.copyOfRange(words, 1, size), true));
			returnSet.addAll(joinWords(
					searchExtensionOneWord(words[size - 1], 2),
					Arrays.copyOfRange(words, 0, size - 1), false));
			for (String w : returnSet) {
				System.out.println("***" + w + "***");
			}
		}
		return returnSet;
	}

	private Vector<String> joinWords(Vector<String> extension, String[] other,
			Boolean isFirst) {
		Vector<String> resultVector = new Vector<String>();
		String tmpOther = "";
		for (int i = 0; i < other.length; i++) {
			tmpOther += other[i] + " ";
		}
		tmpOther = tmpOther.substring(0, tmpOther.length() - 1);

		for (int i = 0; i < extension.size(); i++) {
			String s = "";
			if (isFirst) {
				s = extension.elementAt(i) + " " + tmpOther;
			} else {
				s = tmpOther + " " + extension.elementAt(i);
			}
			resultVector.add(s);
		}
		return resultVector;
	}

	private Vector<String> searchExtensionOneWord(String w, int length) {
		Vector<String> resultVector = new Vector<String>();
		Map<String, Double> wordsMap = new HashMap<String, Double>();
		NounSynset nounSynset;
		NounSynset[] hyponyms;
		WordNetDatabase database = WordNetDatabase.getFileInstance();

		Synset[] synsets = database.getSynsets(w, SynsetType.NOUN);

		for (int i = 0; i < synsets.length; i++) {
			nounSynset = (NounSynset) (synsets[i]);
			hyponyms = nounSynset.getHyponyms();

			for (int j = 0; j < hyponyms.length; j++) {

				String hyponymWord = "" + hyponyms[j];
				hyponymWord = (String) hyponymWord.substring(
						hyponymWord.indexOf('[') + 1,
						hyponymWord.lastIndexOf(']'));
				for (String term : hyponymWord.split(",")) {
					WS4JConfiguration.getInstance().setMFS(true);
					double s = rcs.calcRelatednessOfWords(w, term);
					wordsMap.put(term, s);
				}
			}
		}

		int i = 0;
		for (Map.Entry<String, Double> entry : entriesSortedByValues(wordsMap)) {
			resultVector.add(entry.getKey());
			i++;
			if (i >= length) {
				break;
			}
		}
		return resultVector;
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e2.getValue().compareTo(e1.getValue());
						return res != 0 ? res : 1; // Special fix to preserve
													// items with equal values
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

}
