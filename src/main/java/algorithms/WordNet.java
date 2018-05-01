package algorithms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.Sentence;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class WordNet extends Summarizer{
	private HashMap<String, Integer> word_count;
	private HashMap<String, Integer> word_score;
	
	public  WordNet(List<Sentence> sentences) {
		this.sentences = sentences;
		word_count = new HashMap<String, Integer>();
		word_score = new HashMap<String, Integer>();
		try {
			JWNL.initialize(new FileInputStream("jwnl14-rc2/config/file_properties.xml"));
			_build_word_count();
			analyse();
			summarize(algorithm.WORDNET,60);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void _build_word_count() {
		for(Sentence S: sentences) {
			for(String word:S.chunks)
				word_count.put(word, word_count.getOrDefault(word, 0) +1);
		}
		Iterator<String> chunkItr = word_count.keySet().iterator();
		while(chunkItr.hasNext()) {
			try {
				String chunk = chunkItr.next();
				System.out.println(Dictionary.getInstance());
				IndexWord indexword = Dictionary.getInstance().lookupIndexWord(POS.VERB,chunk);
				if(indexword == null)
					indexword = Dictionary.getInstance().lookupIndexWord(POS.NOUN,chunk);
				if(indexword!=null) {
					Synset[] senses = indexword.getSenses();
					int score = word_count.get(chunk);
					for(Synset synset: senses)
						for(String _chunk: word_count.keySet()) {
							score += synset.containsWord(_chunk)?word_count.get(_chunk):0;
						}
					word_score.put(chunk, score);
				}
			} catch (JWNLException e) {
				e.printStackTrace();
			}	
		}
	}
	@Override
	protected void analyse() {
		for(Sentence S: sentences) 
			for(String chunk: S.chunks) 
				S.wordnet_score += word_score.getOrDefault(chunk,0);
	}
	
	

}
