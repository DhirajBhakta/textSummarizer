package preprocessing;

import preprocessing.Wordnet;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.text.html.HTMLDocument.Iterator;

import constants.Constants;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;



import main.Driver;


public class preprocessor {
	
	protected StanfordCoreNLP pipeline;
	
	
	public preprocessor()
	{
	
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
	}
	
	public void read_doc()throws Exception
	  {
		
	  // We need to provide file path as the parameter:
	  // double back quote is to avoid compiler interpret words
	  // like \test as \t (i.e. as a escape sequence)
	  File file = new File("/home/srivalya/textSummarizer/src/sample_text1");
	 
	  BufferedReader br = new BufferedReader(new FileReader(file));
	 
	  String st;
	  
	  String[] words  = new String[0];
	  ArrayList<String> word= new ArrayList<String>();
	  
	  while ((st = br.readLine()) != null)
	  {
		  words=st.split(" ");
		  
		  for (String s: words) {           
		      
			    String result = s.replaceAll("[^\\w\\s]","");
			    

			    String numRegex   = ".*[0-9].*";
			    String alphaRegex = ".*[A-Z].*";

			    if (!result.matches(numRegex) && !result.matches(alphaRegex)) {
			    	
			    	word.add(result);
			    	   
			    }
			    else {
			    	
			    	result = result.replaceAll("\\d","");
			    	word.add(result);
	
			    }
		      		       
		    }
		    
	  }
	  
	   
	  ArrayList<String> word_new= new ArrayList<String>();
	  for(String b : word)
	  		{
      				
				 if (Constants.stopwords.contains(b.toString()))
				 {
					 System.out.println(b.toString());
					 
				 }
				 else
				 {
					 word_new.add(b);
				 }
				
	  		}
	  
	  
	  
	  preprocessor slem = new preprocessor();
	  
	  String text = "";
	 
	  for (String s : word_new)
	  {
	     text += s + "\t";
	  }
	  
	  
      System.out.println(slem.lemmatize(text));
     
      
      //Wordnet w = new Wordnet();
	  //w.searchWord("book");
	
	  }	
	
	
	 public List<String> lemmatize(String documentText)
	    {
	        List<String> lemmas = new LinkedList<String>();
	        // Create an empty Annotation just with the given text
	        Annotation document = new Annotation(documentText);
	        
	        this.pipeline.annotate(document);
	        // Iterate over all of the sentences found
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            // Iterate over all tokens in a sentence
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                // Retrieve and add the lemma for each word into the
	                // list of lemmas
	                lemmas.add(token.get(LemmaAnnotation.class));
	            }
	        }
	        return lemmas;
	    }
	 
	
	 


}
