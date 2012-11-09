import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
public class NegativeWords {
	HashSet<String> negativeWords;
	
	public NegativeWords() {
		negativeWords = new HashSet<String>();
		try {
			// Load the list of words from a txt file StopWords.txt
			BufferedReader br = new BufferedReader(new FileReader("NegativeWords.txt"));
			while (br.ready()) {
				negativeWords.add(br.readLine());
			}
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	
	/**
	 * checks whether the word appears in the negative word list
	 * word can contain upper case letters
	 * @param word
	 * @return
	 */
	public boolean contains(String word) {
		return negativeWords.contains(word.toLowerCase());
	}
}
