package searchEngine;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNet {
	
	public WordNet()
	{
		
		System.setProperty("wordnet.database.dir", "C:\\Program Files\\WordNet\\2.1\\dict\\");
	}
	
	
	public void searchExtendedWords(String word)
	{
		NounSynset nounSynset; 
		NounSynset[] hyponyms;
		
		String words[] = word.split("\\s+");
		
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		
		for (int m=0; m<words.length; m++)
		{
			Synset[] synsets = database.getSynsets(words[m], SynsetType.NOUN);

			System.out.println("\n"+"------for word: "+words[m]+"--------");
			for (int i = 0; i < synsets.length; i++) 
			{ 
				nounSynset = (NounSynset)(synsets[i]); 
				hyponyms = nounSynset.getHyponyms(); 
				
				for (int j = 0; j < hyponyms.length;j++)
				{ 
	    
					String hyponymWord =""+ hyponyms[j];
					hyponymWord = (String) hyponymWord.substring(hyponymWord.indexOf('[')+1, hyponymWord.lastIndexOf(']'));
					for(String term : hyponymWord.split(","))
				        System.out.println(term);
				}
			}
		}
	}
}
