package preprocessing;

//import preprocessing.Wordnet;
//import edu.mit.jwi.morph.WordnetStemmer;

import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import static java.util.stream.Collectors.toList;

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
import main.Sentence;


public class preprocessor {
	
	protected StanfordCoreNLP pipeline;
	
	public preprocessor() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
	}
	
	public List<Sentence> read_doc()throws Exception{
	  File file = new File("sample_text");
	  BufferedReader br = new BufferedReader(new FileReader(file));
	 
	  String line;
	  List<Sentence> sentences= new ArrayList<Sentence>();
	  
	  //Split lines into sentences(separated by . )
	  while ((line = br.readLine()) != null) {
		  BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		  iterator.setText(line);
		  int start = iterator.first();
		  for (int end = iterator.next();
		      end != BreakIterator.DONE;
		      start = end, end = iterator.next()) {
			  sentences.add(new Sentence(line.substring(start,end)));
		  }
	  }
	  
	  //Stopword removal + lemmatization
	  for(Sentence S: sentences) {
		  S.chunks = S.getWords().stream()
				  .filter(word -> !Constants.stopwords.contains(word))
				  .collect(toList());
		  String text = String.join("\t",S.chunks.stream().toArray(String[]::new));
		  S.chunks = lemmatize(text);
	  }
	  return sentences;
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
