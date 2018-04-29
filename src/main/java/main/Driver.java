package main;
import preprocessing.preprocessor;



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
			summary.append(S.sentence);
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
	

	

	
	public static void main(String[] args) throws Exception {
		String input_dir = args[0];
		
		File dir = new File(input_dir);
		File[] dir_listings = dir.listFiles();
		
		File fuzzy_output_dir = createDirectory("FUZZY");
		File bushy_output_dir = createDirectory("BUSHY");
		File wnet_output_dir = createDirectory("WORDNET");
		
		
		
		for(File input_file: dir_listings) {
		    	List<Sentence> sentences = new preprocessor(input_file).read_doc();
		    	List<Sentence> summary;
		    	
		    	String stats;
		    	Sentence title = sentences.get(0);
		    	sentences = sentences.subList(1, sentences.size());
		    	
		    	
		    	WordNet wnet = new WordNet(sentences);
		    	summary = wnet.getSummary();
		    	stats = wnet.getStats();
		    	printSummary(input_file.getName(), summary, wnet_output_dir, stats);
//		    		
//		    	Fuzzy fuzzy = new Fuzzy(title, sentences);
//		    	summary= fuzzy.getSummary();
//		    	stats = fuzzy.getStats();
//		    	printSummary(input_file.getName(), summary, fuzzy_output_dir, stats);
//		  	
//		    	BushyPath bushy = new BushyPath(sentences);
//		    	summary = bushy.getSummary();
//		    	stats = bushy.getStats();
//		    	printSummary(input_file.getName(), summary, bushy_output_dir,stats);
		}
		}

}