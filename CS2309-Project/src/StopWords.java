/**
 * List of stopwords to be excluded in the matching process
 */
import java.io.*;
import java.util.HashSet;
public class StopWords {
	HashSet<String> stopWords;
	
	public StopWords() {
		stopWords = new HashSet<String>();
		try {
			// Load the list of words from a txt file StopWords.txt
			BufferedReader br = new BufferedReader(new FileReader("StopWords.txt"));
			while (br.ready()) {
				stopWords.add(br.readLine());
			}
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	
	/**
	 * checks whether the word appears in the stop word list
	 * word can contain upper case letters
	 * @param word
	 * @return
	 */
	public boolean contains(String word) {
		return stopWords.contains(word.toLowerCase());
	}
}
