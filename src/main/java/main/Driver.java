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
	
	public static final int WORD_LIMIT=60;

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
	

	
	
	public static List<Sentence> combine_2_summaries_by_union(List<Sentence> sentences, 
			List<Sentence> summary1, 
			List<Sentence> summary2) {
		
		List<Sentence> final_summary = new ArrayList<Sentence>();
		int word_count = 0;
		for(Sentence S: sentences) {
			if(word_count>WORD_LIMIT)
				break;
			if(summary1.contains(S) || summary2.contains(S)) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		}		
		return final_summary;
	}
	
	public static List<Sentence> combine_3_summaries_by_union(List<Sentence> sentences, 
			List<Sentence> summary1, 
			List<Sentence> summary2,
			List<Sentence> summary3) {
		
		List<Sentence> final_summary = new ArrayList<Sentence>();
		int word_count = 0;
		for(Sentence S: sentences) {
			if(word_count>WORD_LIMIT)
				break;
			if(summary1.contains(S) || summary2.contains(S) || summary3.contains(S)) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		}		
		return final_summary;
	}
	
	public static List<Sentence> combine_2_summaries_by_intersection_union(List<Sentence> sentences, 
			List<Sentence> summary1, 
			List<Sentence> summary2) {
		
		List<Sentence> final_summary = new ArrayList<Sentence>();
		int word_count = 0;
		for(Sentence S: summary1)
			if(summary2.contains(S)) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		for(Sentence S: sentences) {
			if(word_count>WORD_LIMIT)
				break;
			if(!final_summary.contains(S) && (summary1.contains(S) || summary2.contains(S))) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		}
		List<Sentence> _final_summary = new ArrayList<Sentence>();
		for(Sentence S: sentences)
			if(final_summary.contains(S))
				_final_summary.add(S);
		
		return _final_summary;
		
	}
	
	

	
	
	public static List<Sentence> combine_3_summaries_by_intersection_union(List<Sentence> sentences, 
			List<Sentence> summary1, 
			List<Sentence> summary2,
			List<Sentence> summary3) {
		
		List<Sentence> final_summary = new ArrayList<Sentence>();
		int word_count = 0;
		for(Sentence S: summary1)
			if(summary2.contains(S) && summary3.contains(S)) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		for(Sentence S: sentences) {
			if(word_count>WORD_LIMIT)
				break;
			if(!final_summary.contains(S) && (summary1.contains(S) || summary2.contains(S) || summary3.contains(S))) {
				final_summary.add(S);
				word_count+=S.getWords().size();
			}
		}
		List<Sentence> _final_summary = new ArrayList<Sentence>();
		for(Sentence S: sentences)
			if(final_summary.contains(S))
				_final_summary.add(S);
		
		return _final_summary;		
	}

	
	public static void main(String[] args) throws Exception {
		String input_dir = args[0];
		
		File dir = new File(input_dir);
		File[] dir_listings = dir.listFiles();
		System.out.println("DIR sice:"+dir_listings.length);
		
		File wnet_output_dir = createDirectory("WORDNET");
		File fuzzy_output_dir = createDirectory("FUZZY");
		File bushy_output_dir = createDirectory("BUSHY");
		
		File wnet_fuzzy_output_dir = createDirectory("WORDNET_FUZZY");
		File wnet_bushy_output_dir = createDirectory("WORDNET_BUSHY");
		File bushy_fuzzy_output_dir = createDirectory("BUSHY_FUZZY");
		File wnet_fuzzy_union_output_dir = createDirectory("WORDNET_FUZZY_UNION");
		File wnet_bushy_union_output_dir = createDirectory("WORDNET_BUSHY_UNION");
		File bushy_fuzzy_union_output_dir = createDirectory("BUSHY_FUZZY_UNION");
		File combo_output_dir = createDirectory("COMBO");
		File combo_union_output_dir = createDirectory("COMBO_UNION");
		
		
		
			
		
		int i=0;
		for(File input_file: dir_listings) {
				System.out.println("---------"+i);
				i = i+1;
		    	List<Sentence> sentences = new preprocessor(input_file).read_doc();
		    	List<Sentence> wnet_summary;
		    	List<Sentence> fuzzy_summary;
		    	List<Sentence> bushy_summary;
		    	List<Sentence> summary = new ArrayList<Sentence>();
		    	
		    	String filename = input_file.getName();
		    	
		    	String stats;
		    	Sentence title = sentences.get(0);
		    	sentences = sentences.subList(1, sentences.size());
		    	
		    	
		    	WordNet wnet = new WordNet(sentences);
		    	wnet_summary = wnet.getSummary();
		    	stats = wnet.getStats();
		    	printSummary(filename+"_system1.txt", wnet_summary, wnet_output_dir, stats);
		    		
		    	Fuzzy fuzzy = new Fuzzy(title, sentences);
		    	fuzzy_summary= fuzzy.getSummary();
		    	stats = fuzzy.getStats();
		    	printSummary(filename+"_system2.txt", fuzzy_summary, fuzzy_output_dir, stats);
		  	
		    	BushyPath bushy = new BushyPath(sentences);
		    	bushy_summary = bushy.getSummary();
		    	stats = bushy.getStats();
		    	printSummary(filename+"_system3.txt", bushy_summary, bushy_output_dir,stats);
		
		    	
		    	
		    	
		    	summary = combine_2_summaries_by_intersection_union(sentences, wnet_summary, fuzzy_summary);
		    	printSummary(filename+"_system12.txt", summary,wnet_fuzzy_output_dir,"");
		    	
		    	summary = combine_2_summaries_by_intersection_union(sentences, wnet_summary, bushy_summary);
		    	printSummary(filename+"_system13.txt", summary,wnet_bushy_output_dir,"");
		    	
		    	summary = combine_2_summaries_by_intersection_union(sentences, bushy_summary, fuzzy_summary);
		    	printSummary(filename+"_system23.txt", summary,bushy_fuzzy_output_dir,"");
		    	
		    	summary = combine_2_summaries_by_union(sentences, wnet_summary, fuzzy_summary);
		    	printSummary(filename+"_system12U.txt", summary,wnet_fuzzy_union_output_dir,"");
		    	
		    	summary = combine_2_summaries_by_union(sentences, wnet_summary, bushy_summary);
		    	printSummary(filename+"_system13U.txt", summary,wnet_bushy_union_output_dir,"");

		    	summary = combine_2_summaries_by_union(sentences, bushy_summary, fuzzy_summary);
		    	printSummary(filename+"_system23U.txt", summary,bushy_fuzzy_union_output_dir,"");

		    	summary = combine_3_summaries_by_intersection_union(sentences, wnet_summary, fuzzy_summary, bushy_summary);
		    	printSummary(filename+"_system123.txt", summary,combo_output_dir,"");

		    	summary = combine_3_summaries_by_union(sentences, wnet_summary, fuzzy_summary, bushy_summary);
		    	printSummary(filename+"_system123U.txt", summary,combo_union_output_dir,"");

		    }
		}

}