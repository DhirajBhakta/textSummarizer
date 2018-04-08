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

public class Driver {

  
    public static void main(String[] args) throws Exception {
       
    	preprocessor p=new preprocessor();
    	
    	p.read_doc();
    	
    }

}