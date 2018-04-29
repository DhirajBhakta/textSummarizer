package algorithms;

import static java.util.stream.Collectors.toList;
import java.util.List;

import main.Sentence;

public abstract class Summarizer {
	protected Sentence title;
	protected List<Sentence> sentences;
	protected List<Sentence> summary;
	protected String stats;
	protected enum algorithm {BUSHY, FUZZY};
	
	public Summarizer() {
		
	}
	
	private float get_score(Sentence S,algorithm algo) {
		if(algo == algorithm.BUSHY)
			return S.bushy_score;
		else if(algo == algorithm.FUZZY)
			return S.fuzzy_score;
		return 0;
	}
	
	public List<Sentence> getSentences(){
		return this.sentences;
	}
	public List<Sentence> getSummary(){
		return this.summary;
	}
	public String getStats() {
		return stats;
	}
	
	abstract protected void analyse();
	
	protected void summarize(algorithm algo, double offset_fraction) {
		double mean = sentences.stream()
							  .map(S -> get_score(S, algo))
							  .reduce((float)0.0, (x,y)->x+y)/sentences.size();
		double stdev = Math.sqrt(sentences.stream()
							   .map(S -> Math.pow((get_score(S, algo) - mean), 2))
							   .reduce(0.0, (x,y)->x+y)/sentences.size());
		double cutoff = mean + (offset_fraction)*stdev;
		summary = sentences.stream().filter(S ->S.fuzzy_score>=cutoff).collect(toList());
		
		
		
	}
	
	protected void calculate_stats() {
		float num_words = sentences.stream().map(S -> S.sentence.split(" ").length).reduce(0, (x,y)->x+y);
		float summary_num_words = summary.stream().map(S -> S.sentence.split(" ").length).reduce(0, (x,y)->x+y);
		float shrinkage = 100 - 100*summary_num_words/num_words;
		stats = "extract: "+(int)num_words+"w ,"+
			    "summary: "+(int)summary_num_words+"w ,"+
				"Compression: "+shrinkage+"%";
	}
	
	

}
