import java.util.*;
import java.io.*;
public class Checker {
	static ArticleCollection articles;
	static double threshold;// the threshold for the angle between vectors
	static final int agreeFactor = 1, disagreeFactor = 2;
	static boolean initialized = false;
	
	static public void initialize(double aThreshold) throws IOException{
		articles = new ArticleCollection();
		Checker.threshold = aThreshold;
		initialized = true;
	}
	
	/**
	 * naive method with stemming and negative word agreement matching
	 * @param tweet
	 * @return
	 */
	static public boolean verify(String tweet) {
		if (!initialized) {
			throw new RuntimeException("Initialize first plz!");
		}
		int numArticleAgree = 0, numArticleDisagree = 0;
		// Iterate through all articles we have found and check
		// for agreement
		AgreementCalculator calculator = new AgreementCalculator(tweet, true);
		
		for (Article article : articles) {
			// Iterate through all sentences in an article and check
			// for agreement
			
			int numAgreement = 0, numDisagreement = 0;
			
			// Criteria: if their angle is smaller than a threshold and
			// they both agree or disagree with the topic, the tweet and
			// the sentence are said to be in agreement.
			for (String sentence : article) {
				// if abs(result) is smaller than threshold, they are of the same
				// topic
				// if result >= 0 they are in agreement
				
				double result = calculator.checkAgreement(sentence);
				if (Math.abs(result) <= threshold && result >= 0) {
					++numAgreement;
					Logger.logAgreeingSentence(sentence + "agree? " + (result >= 0));//for debugging
				} else if (Math.abs(result) <= threshold && result < 0) {
					++numDisagreement;
					Logger.logDisagreeingSentence(sentence);//for debugging
				}
			}
			
			// if number of agreement is greater than number of disagreement,
			// the article agrees with the tweet in general
			numAgreement *= agreeFactor;
			numDisagreement *= disagreeFactor;
			if (numAgreement > numDisagreement) {
				++numArticleAgree;
			} else if (numDisagreement > numAgreement) {
				++numArticleDisagree;
			}
		}
		
		// If number of agreeing articles is larger than that of disagreeing
		// articles, the tweet is verified and is not a roomer
		if (numArticleAgree * agreeFactor > numArticleDisagree * disagreeFactor) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * naive method with stemming(optional
	 * @param tweet
	 * @param stemming  T will turn on stemming
	 * @return
	 */
	static public boolean naiveVerifyStemming(String tweet, boolean stemming) {
		if (!initialized) {
			throw new RuntimeException("Initialize first plz!");
		}
		// Iterate through all articles we have found and check
		// for agreement
		AgreementCalculator calculator = new AgreementCalculator(tweet, stemming);
		
		int i = 0;
		for (Article article : articles) {
			// Iterate through all sentences in an article and check
			// for agreement
			
			int numAgreement = 0;
			
			// Criteria: if their angle is smaller than a threshold and
			// the tweet and the sentence are said to be in agreement.
			for (String sentence : article) {
				// if abs(result) is smaller than threshold, they are of the same
				// topic
				// if result >= 0 they are in agreement
				double result = calculator.naiveCheckAgreement(sentence);
				if (result <= threshold) {
					++numAgreement;
					Logger.logAgreeingSentence(sentence);
				}
			}
			
			// if number of agreement is greater than number of disagreement,
			// the article agrees with the tweet in general
			if (numAgreement > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("Tweets"));
		Checker checker = new Checker();
		Logger.overwrite();
		while (reader.ready()) {
			String tweet = reader.readLine();
			boolean result = !checker.verify(tweet);
			Logger.logDisagreeingSentence("------\n" + tweet);
			Logger.logDisagreeingSentence("Is the tweet rumor? " + result + "\n");
			Logger.printAll();
		}
		reader.close();
	}
}
