package ezi1;

public class DocScore implements Comparable<DocScore> {
	private double score;
	private int docId;
	private String doc;

	public DocScore(double score, int docId, String doc) {
		this.score = score;
		this.docId = docId;
		this.doc=doc;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int compareTo(DocScore docScore) {
		if (score > docScore.score)
			return -1;
		if (score < docScore.score)
			return 1;
		return 0;
	}
}