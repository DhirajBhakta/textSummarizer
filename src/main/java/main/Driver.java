package main;
import preprocessing.preprocessor;

import java.util.ArrayList;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import algorithms.*;

public class Driver {

	public static void printSummary(String filename, List<Sentence> sentences, File output_dir, String stats) {
		File output = new File(output_dir, filename);
		System.out.println("SUMMARY:"+filename+"------------->"+output_dir.getName()+"::::"+stats);
		StringBuilder summary = new StringBuilder("");
		for(Sentence S:sentences)
			summary.append(S.sentence+"\n");
		try (PrintStream out = new PrintStream(new FileOutputStream(output))){ 
			out.print(summary);
		}
		catch(Exception e){
			System.err.println(e.toString());
		}
	}
    
	


	public static boolean deleteDirectory(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDirectory(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
	
	public static File createDirectory(String dirName) {
		File dir = new File(dirName);
		if (dir.exists()) {
		    System.out.println("Deleting existing directory: " + dir.getName());
		    deleteDirectory(dir);
		}
		boolean result = false;
		try{
		    dir.mkdir();
		    result = true;
		    } 
	    catch(SecurityException se){
		    System.err.println("Failed to create directory:"+se.toString());
		    }        
		if(result)    
		    System.out.println("DIR created:"+dir.getName());  
		return dir;		
	}
	
    public static void reposition(List<Sentence> sentences) {
    	
    }
	

	
	public static void main(String[] args) throws Exception {
		String input_dir = args[0];
		
		File dir = new File(input_dir);
		File[] dir_listings = dir.listFiles();
		System.out.println("DIR sice:"+dir_listings.length);
		
		File fuzzy_output_dir = createDirectory("FUZZY");
		File bushy_output_dir = createDirectory("BUSHY");
		File wnet_output_dir = createDirectory("WORDNET");		
		File summary_output_dir = createDirectory("COMBO");
		
		int i=0;
		for(File input_file: dir_listings) {
				System.out.println("---------"+i);
				i = i+1;
		    	List<Sentence> sentences = new preprocessor(input_file).read_doc();
		    	List<Sentence> wnet_summary;
		    	List<Sentence> fuzzy_summary;
		    	List<Sentence> bushy_summary;
		    	List<Sentence> summary = new ArrayList<Sentence>();
		    	
		    	
		    	String stats;
		    	Sentence title = sentences.get(0);
		    	sentences = sentences.subList(1, sentences.size());
		    	
		    	
		    	WordNet wnet = new WordNet(sentences);
		    	wnet_summary = wnet.getSummary();
		    	stats = wnet.getStats();
		    	printSummary(input_file.getName(), wnet_summary, wnet_output_dir, stats);
		    		
		    	Fuzzy fuzzy = new Fuzzy(title, sentences);
		    	fuzzy_summary= fuzzy.getSummary();
		    	stats = fuzzy.getStats();
		    	printSummary(input_file.getName(), fuzzy_summary, fuzzy_output_dir, stats);
		  	
		    	BushyPath bushy = new BushyPath(sentences);
		    	bushy_summary = bushy.getSummary();
		    	stats = bushy.getStats();
		    	printSummary(input_file.getName(), bushy_summary, bushy_output_dir,stats);
		
		    	int word_count = 0;
		    	for(Sentence S: wnet_summary)
		    		if(fuzzy_summary.contains(S) && bushy_summary.contains(S)) {
		    			summary.add(S);
		    			word_count += S.getWords().size();
		    		}
		    	
		    	for(Sentence S: sentences) {
		    		if(word_count>100)
		    			break;
		    		if(!summary.contains(S) &&(wnet_summary.contains(S) || fuzzy_summary.contains(S) || bushy_summary.contains(S))) {
		    			summary.add(S);
		    			word_count += S.getWords().size();
		    		}
		    	}
		    	List<Sentence> _summary = new ArrayList<Sentence>();
		    	for(Sentence S: sentences) 
		    		if(summary.contains(S))
		    			_summary.add(S);
		    	summary = _summary;
		    	printSummary(input_file.getName(), summary, summary_output_dir,"");
		    	
		    }
		}

}