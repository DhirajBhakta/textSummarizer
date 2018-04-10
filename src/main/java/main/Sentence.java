package main;

import java.util.Arrays;
import java.util.List;

public class Sentence {
	private String sentence;
	public List<String> chunks;
	
	public Sentence(String str) {
		this.sentence = str;
	}
	public List<String> getWords(){
		return Arrays.asList(sentence.split(" "));
	}
	@Override
	public String toString() {
		return "Sentence [sentence=" + sentence + ", chunks=" + chunks + "]";
	}
	
	
	

}
