import java.text.BreakIterator;
import java.util.*;

import org.tartarus.snowball.ext.englishStemmer;


public class AgreementCalculator {
	StopWords stop;
	NegativeWords negative;
	BreakIterator boundary;
	List<String> keywords;
	int[] tweetVector;
	boolean agreeTopic;
	// whether stemming will be performed
	boolean stemming;
	static englishStemmer stemmer;
	// We place higher weight on disagreeing sentences.
	
	public AgreementCalculator(String tweet, boolean stemming) {
		tweet = tweet.replaceAll("(http(s)?:|#|@)\\S+", "");
		
		agreeTopic = true;
		stop = new StopWords();
		negative = new NegativeWords();
		boundary = BreakIterator.getWordInstance();
		keywords = new LinkedList<String>();
		stemmer = new englishStemmer();
		this.stemming = stemming;
		
		// Extract keywords from the tweet
		boundary.setText(tweet);
		// Break the sentence into words
		int start = boundary.first();
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String word = tweet.substring(start, end).toLowerCase();
			
			// If the word is not a stopword, add it to the keyword list
			if (!Character.isLetter(word.charAt(0))) {
				continue;
			}
			if (!stop.contains(word)) {
				keywords.add(getStem(word, stemming));
			}
			if (negative.contains(word)) {
				agreeTopic = false;
			}
		}
		
		//Create the tweet's vector (all 1s)
		tweetVector = new int[keywords.size()];
		Arrays.fill(tweetVector, 1);
	}
	
	public double checkAgreement(String sentence) {
		int[] sentenceVector = new int[tweetVector.length];
		Set<String> wordSet = new HashSet<String>();
		boolean sentenceAgreeTopic = true;
		
		// Put all words in the sentence into a set
		boundary = BreakIterator.getWordInstance();
		int start = boundary.first();
		boundary.setText(sentence);
		// While adding the words, check for topic agreement
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String word = sentence.substring(start, end);
			// If the word is not a stopword, add it to the keyword list
			if (!stop.contains(word)) {
				wordSet.add(getStem(word, stemming));
			}
			if (negative.contains(word)) {
				sentenceAgreeTopic = false;
			}
		}
		
		int index = 0;
		for (String aWord : keywords) {
			sentenceVector[index++] = (wordSet.contains(aWord)) ? 1 : 0;
		}
		
		// Calculate the angle between the tweetVector and the sentenceVector
		int scalerProduct = 0;
		double tweetMag = 0.0, sentenceMag = 0.0;
		double cos = 0;
		double angle;
		int length = tweetVector.length;
		for (int i = 0; i < length; ++i) {
			scalerProduct += tweetVector[i] * sentenceVector[i];
			tweetMag += tweetVector[i] * tweetVector[i];
			sentenceMag += sentenceVector[i] * sentenceVector[i];
		}
		
		if (sentenceMag == 0.0) {
			// if the sentence contains no keyword, we straitaway say
			// that it is not of a related topic
			return Integer.MAX_VALUE;
		}
		
		tweetMag = Math.sqrt(tweetMag);
		sentenceMag = Math.sqrt(sentenceMag);
		angle = Math.acos(((double) scalerProduct) / (tweetMag * sentenceMag));
		
		// The range of angle is just 0 to pi/2
		double rank = angle / (Math.PI / 2.0);
		
		// Now check for agreement on the topic
		if (agreeTopic ^ sentenceAgreeTopic) {
			// XOR
			rank *= -1.0;
			rank -= 0.00000000001;
		}
		
		
		// Just return the angle and agreement, let the calling code decide if
		// the sentence supports the tweet.
		return rank;
	}
	public double naiveCheckAgreement(String sentence) {
		int[] sentenceVector = new int[tweetVector.length];
		Set<String> wordSet = new HashSet<String>();
		
		// Put all words in the sentence into a set
		boundary = BreakIterator.getWordInstance();
		int start = boundary.first();
		boundary.setText(sentence);
		// While adding the words, check for topic agreement
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String word = sentence.substring(start, end);
			// If the word is not a stopword, add it to the keyword list
			if (!stop.contains(word)) {
				wordSet.add(getStem(word, stemming));
			}
		}
		
		int index = 0;
		for (String aWord : keywords) {
			sentenceVector[index++] = (wordSet.contains(aWord)) ? 1 : 0;
		}
		
		// Calculate the angle between the tweetVector and the sentenceVector
		int scalerProduct = 0;
		double tweetMag = 0.0, sentenceMag = 0.0;
		double cos = 0;
		double angle;
		int length = tweetVector.length;
		for (int i = 0; i < length; ++i) {
			scalerProduct += tweetVector[i] * sentenceVector[i];
			tweetMag += tweetVector[i] * tweetVector[i];
			sentenceMag += sentenceVector[i] * sentenceVector[i];
		}
		
		if (sentenceMag == 0.0) {
			// if the sentence contains no keyword, we straitaway say
			// that it is not of a related topic
			return Integer.MAX_VALUE;
		}
		
		tweetMag = Math.sqrt(tweetMag);
		sentenceMag = Math.sqrt(sentenceMag);
		angle = Math.acos(((double) scalerProduct) / (tweetMag * sentenceMag));
		// The range of angle is just 0 to pi/2
		double rank = angle / (Math.PI / 2.0);
		
		// Just return the angle and agreement, let the calling code decide if
		// the sentence supports the tweet.
		return rank;
	}
	
	static public String getStem(String word, boolean doStemming) {
		if (!doStemming) {
			// if stemming is turned off, don't do it.
			return word;
		}
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}
}
