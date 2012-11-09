/**
 * Logs debugging information
 * @author DIdiHL
 *
 */

import java.util.*;
import java.io.*;
public class Logger {
	static LinkedList<String> agreeingSentences = new LinkedList<String>();
	static LinkedList<String> disagreeingSentences = new LinkedList<String>();
	
	public static void logAgreeingSentence(String sentence) {
		agreeingSentences.add(sentence);
	}
	
	public static void logDisagreeingSentence(String sentence) {
		disagreeingSentences.add(sentence);
	}
	
	public static void overwrite() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("Log.txt"));
	}
	
	public static void printAll() throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter("Log.txt", true));
		
		// Print all agreeing sentences
		writer.write("Agreeing sentences:\n");
		for (String sentence : agreeingSentences) {
			writer.write(sentence + "\n");
		}
		agreeingSentences = new LinkedList<String>();
		writer.flush();
		
		// Print all disagreeing sentences
		writer.write("Disagreeing sentences:\n");
		for (String sentence : disagreeingSentences) {
			writer.write(sentence + "\n");
		}
		disagreeingSentences = new LinkedList<String>();
		writer.flush();
	}
}
