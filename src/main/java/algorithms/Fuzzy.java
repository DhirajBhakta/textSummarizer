package algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.io.InputStream;

import net.sourceforge.jFuzzyLogic.FIS;

import main.Sentence;






public class Fuzzy extends Summarizer{
	private HashMap<String, Integer> word_count;
	private float maxlen;
	private float total_len;
	private FIS fis;
	

	public Fuzzy(Sentence title, List<Sentence> sentences) {
		InputStream is = getClass().getResourceAsStream("fuzzyInference.fcl");
        this.fis = FIS.load(is,true);
        if( fis == null ) { 
            System.err.println("Can't load FCL file");
            return;
        }
		this.title = title;
		this.sentences = sentences;
		analyse();
		word_count = new HashMap<String, Integer>();
		_build_word_count();
		calculate_features();
		calculate_fuzzy_scores();
		summarize(algorithm.FUZZY,100);		
	}

	
	private void _build_word_count() {
		for(Sentence S: sentences) {
			for(String word:S.chunks)
				word_count.put(word, word_count.getOrDefault(word, 0) +1);
		}
	}
	protected void analyse() {
		this.total_len =0;
		this.maxlen = 0;
		for(Sentence S: sentences) { 
			maxlen = maxlen<S.chunks.size()? S.chunks.size(): maxlen;
			total_len += S.chunks.size();
		}
	}
	
	private void calculate_features(){
		float f1_max=Float.MIN_VALUE;
		float f2_max=Float.MIN_VALUE;
		float f3_max=Float.MIN_VALUE;
		float f4_max=Float.MIN_VALUE;
		float f5_max=Float.MIN_VALUE;
		float f1_min=Float.MAX_VALUE;
		float f2_min=Float.MAX_VALUE;
		float f3_min=Float.MAX_VALUE;
		float f4_min=Float.MAX_VALUE;
		float f5_min=Float.MAX_VALUE;
		
		for(Sentence S: sentences) {
			S.f2 = S.chunks.size()/maxlen;
			f2_max = S.f2>f2_max ?S.f2 :f2_max;
			f2_min = S.f2<f2_min ?S.f2 :f2_min;
			
			S.f3 = (float)1.0/(sentences.indexOf(S)+1);
			f3_max = S.f3>f3_max ?S.f3 :f3_max;
			f3_min = S.f3<f3_min ?S.f3 :f3_min;
			
			HashSet<String> chunkSet = new HashSet<String>(S.chunks);
			float common_words = 0;
			float common_title_words = 0;
			for(String word: S.chunks) {
				common_words += (word_count.get(word) - Collections.frequency(chunkSet, word) >0)? 1 : 0;
				common_title_words += title.chunks.contains(word)?1 : 0;
			}
			S.f1 = common_title_words/title.chunks.size();
			f1_max = S.f1>f1_max ?S.f1 :f1_max;
			f1_min = S.f1<f1_min ?S.f1 :f1_min;
			S.f4 = common_words/total_len;
			f4_max = S.f4>f4_max ?S.f4 :f4_max;
			f4_min = S.f4<f4_min ?S.f4 :f4_min;
			
			S.f5 = S.chunks.stream().filter(word -> word.matches("\\d+")).count();
			f5_max = S.f2>f5_max ?S.f5 :f5_max;
			f5_min = S.f2<f5_min ?S.f5 :f5_min;
		}
		
		final float f1_den = f1_max - f1_min;
		final float f2_den = f2_max - f2_min;
		final float f3_den = f3_max - f3_min;
		final float f4_den = f4_max - f4_min;
		final float f5_den = f5_max - f5_min;
		for(Sentence S: sentences) {
			S.f1 = (S.f1 - f1_min)/f1_den;
			S.f2 = (S.f2 - f2_min)/f2_den;
			S.f3 = (S.f3 - f3_min)/f3_den;
			S.f4 = (S.f4 - f4_min)/f4_den;
//			S.f5 = (S.f5 - f5_min)/f5_den;
////			S.f5 = (S.f5==Float.NaN)? 0 :S.f5;
		}		
	}
	
	private void calculate_fuzzy_scores() {
		for(Sentence S: sentences) {
			fis.setVariable("title", S.f1);
			fis.setVariable("s_len", S.f2);
			fis.setVariable("s_loc", S.f3);
			fis.setVariable("s_cntr", S.f4);
			fis.setVariable("num_data", S.f5);
			fis.evaluate();
			S.fuzzy_score = (float)fis.getVariable("output").getValue();			
		}
	}

}
