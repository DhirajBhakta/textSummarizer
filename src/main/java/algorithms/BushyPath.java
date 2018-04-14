package algorithms;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

import edu.stanford.nlp.ling.tokensregex.PhraseTable.TokenList;
import main.Sentence;

public class BushyPath {
	List<Sentence> sentences;
	
	int[][] common_words;
	
	
	public BushyPath(List<Sentence> sentences) {
		this.sentences = sentences;
		int n = sentences.size();
		common_words = new int[n][n];
		analyze();
		calculate_bushy_scores();
	}
	
	public List<Sentence> getSentences(){
		return sentences;
	}
	private void analyze() {
		for(int i=0; i<sentences.size(); i++)
			for(int j=i+1; j<sentences.size(); j++) {				
				int wc=0;
				List<String> chunks = sentences.get(i).chunks;
				for(String word: sentences.get(j).chunks) 
					wc +=  chunks.contains(word)? 1:0;
				common_words[i][j] = wc;
				common_words[j][i] = wc;			
			}		
	}
	
	private void calculate_bushy_scores() {
		for(int i=0;i<sentences.size();i++) {
			int score=0;
			for(int j=0;j<sentences.size();j++) 
				score+= common_words[i][j]>0 ? 1 :0;
			sentences.get(i).bushy_score = score;
		}		
	}
	
	public List<Sentence> summary() {
		double mean = sentences.stream()
							  .map(S -> S.bushy_score)
							  .reduce((float)0.0, (x,y)->x+y)/sentences.size();
		double stdev = Math.sqrt(sentences.stream()
							   .map(S -> Math.pow((S.bushy_score-mean), 2))
							   .reduce(0.0, (x,y)->x+y)/sentences.size());
		double cutoff = mean + (0.25)*stdev;
		return sentences.stream().filter(S ->S.bushy_score>=cutoff).collect(toList());
		}
}
