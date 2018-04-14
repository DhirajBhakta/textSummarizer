package main;
import preprocessing.preprocessor;

//
//public class Driver {
//	
//
//	public static void main(String[] args)throws Exception
//	{
//	
//		
//	preprocessor p=new preprocessor();
//	
//	p.read_doc();
//	
//	}
//
//}

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import algorithms.*;

public class Driver {

	public static void printSummary(Sentence title, List<Sentence> sentences) {
		System.out.println("SUMMARY:"+title.sentence+"\n-------------------------------------");
		for(Sentence S:sentences)
			System.out.print(S.sentence+"\n");
	}
    
	
	public static void main(String[] args) throws Exception {
       
    	List<Sentence> sentences = new preprocessor().read_doc();
    	List<Sentence> summary;
    	Sentence title = sentences.get(0);
    	sentences = sentences.subList(1, sentences.size());
    	
    	Fuzzy fuzzy = new Fuzzy(title, sentences);
    	summary= fuzzy.summary();
    	printSummary(title, summary);
  	
    	BushyPath bushy = new BushyPath(sentences);
    	summary = bushy.summary();
    	printSummary(title, summary);
    }

}