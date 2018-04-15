package algorithms;

import java.util.List;

import static java.util.stream.Collectors.toList;

import main.Sentence;

public class BushyPath {
	private List<Sentence> sentences;
	private List<Sentence> summary;
	private String stats;
	int[][] common_words;
		
	public BushyPath(List<Sentence> sentences) {
		this.sentences = sentences;
		int n = sentences.size();
		common_words = new int[n][n];
		analyze();
		calculate_bushy_scores();
		summarize();
	}
	
	public List<Sentence> getSentences(){
		return sentences;
	}
	public List<Sentence> getSummary(){
		return summary;
	}
	public String getStats() {
		return stats;
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
	
	public void summarize() {
		double mean = sentences.stream()
							  .map(S -> S.bushy_score)
							  .reduce((float)0.0, (x,y)->x+y)/sentences.size();
		double stdev = Math.sqrt(sentences.stream()
							   .map(S -> Math.pow((S.bushy_score-mean), 2))
							   .reduce(0.0, (x,y)->x+y)/sentences.size());
		double cutoff = mean + (0.25)*stdev;
		summary = sentences.stream().filter(S ->S.bushy_score>=cutoff).collect(toList());
		
		//calculate stats:
		float num_words = sentences.stream().map(S -> S.sentence.split(" ").length).reduce(0, (x,y)->x+y);
		float summary_num_words = summary.stream().map(S -> S.sentence.split(" ").length).reduce(0, (x,y)->x+y);
		float shrinkage = 100 - 100*summary_num_words/num_words;
		stats = "extract: "+(int)num_words+"w ,"+
			    "summary: "+(int)summary_num_words+"w ,"+
				"Compression: "+shrinkage+"%";
		}
}
