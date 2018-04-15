package algorithms;

import java.util.List;

import main.Sentence;

public class BushyPath extends Summarizer{
	int[][] common_words;
		
	public BushyPath(List<Sentence> sentences) {
		this.sentences = sentences;
		int n = sentences.size();
		common_words = new int[n][n];
		analyse();
		calculate_bushy_scores();
		summarize(0.25);
	}
		
	protected void analyse() {
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

}
