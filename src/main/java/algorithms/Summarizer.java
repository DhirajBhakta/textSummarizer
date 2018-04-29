package algorithms;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import main.Sentence;

public abstract class Summarizer {
	protected Sentence title;
	protected List<Sentence> sentences;
	protected List<Sentence> summary;
	protected String stats;
	protected enum algorithm {BUSHY, FUZZY, WORDNET};
	
	private float get_score(Sentence S,algorithm algo) {
		if(algo == algorithm.BUSHY)
			return S.bushy_score;
		else if(algo == algorithm.FUZZY)
			return S.fuzzy_score;
		else if(algo == algorithm.WORDNET)
			return S.wordnet_score;
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
	
	protected void summarize(algorithm algo, int max_words) {
		List<Sentence> sorted_sentences = new ArrayList<Sentence>(sentences);
		sorted_sentences.sort(new Comparator<Sentence>() {
		    @Override
		    public int compare(Sentence s1, Sentence s2) {
		        if(get_score(s1, algo) == get_score(s2, algo))
		            return 0;
		        return (get_score(s1, algo) > get_score(s2, algo)) ? -1 : 1;
		     }
		});
		int word_count =0;
		float MIN_SCORE=Float.MAX_VALUE;
		for(Sentence S: sorted_sentences) {
			word_count += S.getWords().size();
			MIN_SCORE = (get_score(S, algo) < MIN_SCORE) ? get_score(S,algo): MIN_SCORE;
			if(word_count > max_words)
				break;
		}
		final float threshold = MIN_SCORE;
		summary = sentences.stream().filter(S ->get_score(S, algo)>=threshold).collect(toList());
		calculate_stats();
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
