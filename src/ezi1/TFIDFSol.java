package ezi1;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class TFIDFSol {
	private Vector<String[]> db = new Vector<String[]>(); // the document
	private Vector<String> keywords = new Vector<String>();
	private TreeMap<String, Double> idfs = new TreeMap<String, Double>(); // idf
																			// value
																			// for
																			// each
																			// term
																			// in
																			// the
																			// vocabulary
	private TreeMap<String, Set<Integer>> invertedFile = new TreeMap<String, Set<Integer>>(); // term
																								// ->
																								// docIds
																								// of
																								// docs
																								// containing
																								// the
																								// term
	private Vector<TreeMap<String, Double>> tf = new Vector<TreeMap<String, Double>>(); // term
																						// x
																						// docId
																						// matrix
																						// with
																						// term
																						// frequencies

	public void setDb(Vector<String> db, Vector<String> titles) {
		for (int i = 0; i < db.size(); i++) {
			String tab[] = { db.get(i), titles.get(i) };
			this.db.add(tab);
		}
	}

	public void setKeywords(Vector<String> keywords) {
		this.keywords = keywords;
	}

	public Vector<String> getVectorKeywords() {
		return keywords;
	}

	// // lists the vocabulary
	// private void printVoc() {
	// System.out.println("Vocabulary:");
	// for (Map.Entry<String, Double> entry : idfs.entrySet()) {
	// System.out.println(entry.getKey() + ", idf = " + entry.getValue());
	// }
	// }

	// // lists the database
	// private void printDB() {
	// System.out.println("size of the database: " + db.size());
	// for (int i = 0; i < db.size(); i++) {
	// System.out.println("doc " + i + ": " + db.elementAt(i));
	// }
	// System.out.println("");
	// }

	// calculates the similarity between two vectors
	// each vector is a term -> weight map
	private double similarity(TreeMap<String, Double> v1,
			TreeMap<String, Double> v2) {
		double sum = 0;
		// iterate through one vector

		for (Map.Entry<String, Double> entry : v1.entrySet()) {
			String term = entry.getKey();
			// System.out.println("term: "+term);
			Double w1 = entry.getValue();
			// multiply weights if contained in second vector
			Double w2 = v2.get(term);
			if (w2 != null)
				sum += w1 * w2;
		}
		// TODO write the formula for computation of cosinus
		// note that v.values() is Collection<Double> that you may need to
		// calculate length of the vector
		// take advantage of vecLength() function
		// DONE
		// System.out.println(" vector2:"+vecLength(v2.values()));
		double sim = 0;
		if (vecLength(v1.values()) != 0 && vecLength(v2.values()) != 0)
			sim = sum / (vecLength(v1.values()) * vecLength(v2.values()));

		return sim;
	}

	// returns the length of a vector
	private double vecLength(Collection<Double> vec) {
		double sum = 0;
		for (Double d : vec) {
			sum += Math.pow(d, 2);
		}
		return Math.sqrt(sum);
	}

	// ranks a query to the documents of the database
	public Vector<DocScore> rank(String query) {
		System.out.println("");
		System.out.println("query = " + query);

		// get term frequencies for the query terms
		TreeMap<String, Double> termFreqs = getTF(query);
		
		if(termFreqs.size()>0){

		// construct the query vector
		// the query vector
		TreeMap<String, Double> queryVec = new TreeMap<String, Double>();

		// iterate through all query terms
		for (Map.Entry<String, Double> entry : termFreqs.entrySet()) {
			String term = entry.getKey();
			// TODO compute tfidf value for terms of query
			// DONE
			double tfidf = entry.getValue() * idf(term);

			queryVec.put(term, tfidf);
		}

		Set<Integer> union;
		TreeSet<String> queryTerms = new TreeSet<String>(termFreqs.keySet());

		// from the inverted file get the union of all docIDs that contain any
		// query term
		union = invertedFile.get(queryTerms.first());
		for (String term : queryTerms) {
			union.addAll(invertedFile.get(term));
		}

		// calculate the scores of documents in the union
		Vector<DocScore> scores = new Vector<DocScore>();
		for (Integer i : union) {
			String title = (db.get(i))[1];
			scores.add(new DocScore(similarity(queryVec, getDocVec(i)), i,
					title));
		}

		// sort and print the scores
		Collections.sort(scores);
		/*
		 * for (DocScore docScore : scores) { System.out.println("score of doc "
		 * + docScore.getDocId() + " = " + docScore.getScore()); }
		 */

		return scores;
		}
		return null;
	}

	// returns the idf of a term
	private double idf(String term) {
		return idfs.get(term);
	}

	// calculates the document vector for a given docID
	private TreeMap<String, Double> getDocVec(int docId) {
		TreeMap<String, Double> vec = new TreeMap<String, Double>();

		// get all term frequencies
		TreeMap<String, Double> termFreqs = tf.elementAt(docId);

		// for each term, tf * idf
		for (Map.Entry<String, Double> entry : termFreqs.entrySet()) {
			String term = entry.getKey();
			// TODO compute tfidf value for a given term
			// take advantage of idf() function
			// DONE
			double tfidf = getTF(term, docId) * idf(term);
			// System.out.println("getTf(" + term + "," + docId + ")= "
			// + getTF(term, docId) + "  ; tfidf: " + tfidf);
			vec.put(term, tfidf);
		}
		return vec;
	}

	// returns the term frequency for a term and a docID
	private double getTF(String term, int docId) {
		Double freq = tf.elementAt(docId).get(term);
		if (freq == null)
			return 0;
		else
			return freq;
	}

	// calculates the term frequencies for a document

	private double frequencyCounter(String doc, String keyword) {
		int count = 0;

		String[] contentArray = doc.split(" ");
		for (String string : contentArray) {
			if (string.equals(keyword))
				count++;
		}
		return (double) count;

	}

	private TreeMap<String, Double> getTF(String doc) {
		TreeMap<String, Double> termFreqs = new TreeMap<String, Double>();
		double max = 0;

		// tokenize document
		StringTokenizer st = new StringTokenizer(doc, " ");

		for (int i = 0; i < keywords.size(); i++) {

			String term = keywords.get(i);
			double count = 0;
			count = frequencyCounter(doc, term);
			if (count != 0)
				termFreqs.put(term, count);
			if (count > max)
				max = count;

		}
		for (int i = 0; i < termFreqs.size(); i++) {
			String term = keywords.get(i);

			if (termFreqs.get(term) != null)
				termFreqs.put(term, termFreqs.get(term) / max);

		}

		return termFreqs;
	}

	// -----------------------------------

	// init tf, invertedFile, and idfs
	public void init() {
		int docId = 0;
		// for all docs in the database
		for (String[] doc : db) {
			// get the tfs for a doc
			TreeMap<String, Double> termFreqs = getTF(doc[0]);

			// add to global tf vector
			tf.add(termFreqs);

			// for all terms
			for (String term : termFreqs.keySet()) {
				// add the current docID to the posting list
				Set<Integer> docIds = invertedFile.get(term);
				if (docIds == null)
					docIds = new TreeSet<Integer>();
				docIds.add(docId);
				invertedFile.put(term, docIds);
			}
			docId++;
		}

		// calculate idfs
		int dbSize = db.size();
		// for all terms
		for (Map.Entry<String, Set<Integer>> entry : invertedFile.entrySet()) {
			String term = entry.getKey();
			// get the size of the posting list, i.e. the document frequency
			int df = entry.getValue().size();
			// TODO write the formula for calculation of IDF
			// DONE
			idfs.put(term, Math.log10((double) dbSize / df));

		}
	}
}
