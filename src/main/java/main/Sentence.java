package main;

import java.util.Arrays;
import static java.util.stream.Collectors.toList;
import java.util.List;

public class Sentence {
	public String sentence;
	public List<String> chunks;
	public float f1;
	public float f2;
	public float f3;
	public float f4;
	public float f5;
	public float fuzzy_score;
	public float bushy_score;
	
	
	
	
	public Sentence(String str) {
		this.sentence = str;
	}
	public float getFuzzyScore() {
		return fuzzy_score;
	}
	public List<String> getWords(){
		return Arrays.asList(sentence.split(" ")).stream()
					.map(word -> word.replaceAll("[^\\w\\s]",""))
					.filter(word -> word.length()>0)
					.collect(toList());
	}
	@Override
	public String toString() {
		return "\n\nSentence [sentence=" + sentence + ", chunks=" + chunks + "\n, f1=" + f1 + "\n, f2=" + f2 + "\n, f3=" + f3
				+ "\n, f4=" + f4 + "\n, f5=" + f5 + ",\n fuzzy_score=" + fuzzy_score + ",\n bushy_score=" + bushy_score + "]";
	}
	
	
	

}
