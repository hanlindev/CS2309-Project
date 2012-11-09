import java.io.*;
import java.util.*;
import org.apache.commons.math3.stat.*;
/**
 * A tweet consists of a few sentences (< 140 characters)
 * There are two additional fields:
 *   Reliable: T/F indicating whether the information can
 *   be trusted;
 *   Verified: T/F indicating whether the tweet can be verified
 *   by our program. T = verified, not a rumor;   F = not verified
 *   and a rumor. 
 */
class Tweet {
	String value;
	boolean reliable, verified;
	
	/**
	 * Attempt to verify the tweet at time of creation
	 * @param value
	 * @param reliable
	 * @param method indicates which verification method
	 *        should be used:
	 *        1. naive without stemming
	 *        2. naive with stemming
	 *        3. naive with stemming and negative word
	 *           agreement matching
	 *        4. Bayesian (not implemented)
	 *        
	 */
	public Tweet(String value, boolean reliable, int method) {
		this.value = value;
		this.reliable = reliable;
		
		switch(method) {
		case 1:
			this.verified = Checker.naiveVerifyStemming(value, false);
			break;
		case 2:
			this.verified = Checker.naiveVerifyStemming(value, true);
			break;
		case 3:
			this.verified = Checker.verify(value);
			break;
		}
	}
	
	/**
	 * check whether our program's verification result matches with the true
	 * reliability of the tweet, if matches, it is considered a success
	 * @return true if reliable and verified have the same value
	 */
	public boolean success() {
		return (this.reliable == this.verified);
	}
}

public class Experiment {
	// size of sample
	static int size = 50;
	static ArrayList<Tweet> list;
	static int numExperiments;
	static double[] thresholds = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
	//static double[] thresholds = {0.1};
	static public void main(String[] args) throws Exception{
		//Experiment1(args);
		Experiment2(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}
	
	static public void Experiment1(String[] args) throws Exception{
		// Try different thresholds
		BufferedWriter writer;
		for (int i = 0; i < thresholds.length; ++i) {
			Checker.initialize(thresholds[i]);
			BufferedReader reader = new BufferedReader(new FileReader("Tweets"));
			list = new ArrayList<Tweet>(300);
			int method = Integer.parseInt(args[0]);
			File aDir = new File("Method" + method);
			aDir.mkdir();
			writer = new BufferedWriter(new FileWriter("Method" + method + "/" + "NaiveMethod" + method + "Threshold" + thresholds[i] + ".txt"));
			numExperiments = Integer.parseInt(args[1]);
			while (reader.ready()) {
				String tweet, reliable;
				tweet = reader.readLine();
				reliable = reader.readLine();
				boolean reli = false;
				if (reliable.equals("y")) {
					reli = true;
				}
				list.add(new Tweet(tweet, reli, method));
			}
			
			int currNo = 0;
			double[] sampleResults = new double[numExperiments];
			writer.write("Sample Results:\n");
			while (currNo < numExperiments) {
				LinkedList<Tweet> sample = new LinkedList<Tweet>();
				int largestNum = list.size();
				boolean[] taken = new boolean[list.size()];
				Arrays.fill(taken, false);
				int index = 0;
				int numPass = 0;
				Random rng = new Random();
				while(index < size) {
					int j = rng.nextInt(largestNum);
					if (!taken[j]) {
						taken[j] = true;
						Tweet aTweet = list.get(j);
						sample.add(aTweet);
						++index;
						if (aTweet.success()) {
							++numPass;
						}
					}
				}
				sampleResults[currNo++] = (double)numPass / (double)size;
				writer.write(sampleResults[currNo - 1] + "\n");
			}
			writer.write("Mean:\n");
			writer.write("" + StatUtils.mean(sampleResults) + "\n");
			writer.write("Variance:\n");
			writer.write("" + StatUtils.variance(sampleResults));
			writer.flush();
		}
	}
	
	static public void Experiment2(int method, int numExp) throws Exception{
		numExperiments = numExp;
		BufferedWriter writer;
		for (int i = 0; i < thresholds.length; ++i) {
			Checker .initialize(thresholds[i]);
			BufferedReader reader = new BufferedReader(new FileReader("Tweets"));
			ArrayList<Tweet> rumorList = new ArrayList<Tweet>();
			ArrayList<Tweet> nonRumorList = new ArrayList<Tweet>();
			File aDir = new File("Experiment2Method" + method);
			aDir.mkdir();
			writer = new BufferedWriter(new FileWriter("Experiment2Method" + method + "/" + "NaiveMethod" + method + "Threshold" + thresholds[i] + ".txt"));
			while (reader.ready()) {
				String tweet, reliable;
				tweet = reader.readLine();
				reliable = reader.readLine();
				boolean reli = false;
				if (reliable.equals("y")) {
					reli = true;
				}
				if (reli) {
					nonRumorList.add(new Tweet(tweet, reli, method));
				} else {
					rumorList.add(new Tweet(tweet, reli, method));
				}
			}
			testTweets(rumorList, writer, true);
			testTweets(nonRumorList, writer, false);
		}
	}
	
	/**
	 * Test the list of tweets
	 * @param aList
	 * @param out
	 * @param isRumor indicates whether the tweets in the list are rumors, if null, it is unknown
	 */
	static private void testTweets(ArrayList<Tweet> aList, BufferedWriter out, Boolean isRumor) throws Exception {
		int currNo = 0;
		double[] sampleResults = new double[numExperiments];
		if (isRumor == null) {
			out.write("Sample Results:\n");
		} else if (isRumor) {
			out.write("Sample Results:(nonrumor tweets)\n");
		} else {
			out.write("Sample Results:(rumor tweets)\n");
		}
		while (currNo < numExperiments) {
			LinkedList<Tweet> sample = new LinkedList<Tweet>();
			int largestNum = aList.size();
			boolean[] taken = new boolean[aList.size()];
			Arrays.fill(taken, false);
			int index = 0;
			int numPass = 0;
			Random rng = new Random();
			while(index < size) {
				int j = rng.nextInt(largestNum);
				if (!taken[j]) {
					taken[j] = true;
					Tweet aTweet = aList.get(j);
					sample.add(aTweet);
					++index;
					if (aTweet.success()) {
						++numPass;
					}
				}
			}
			sampleResults[currNo++] = (double)numPass / (double)size;
			out.write(sampleResults[currNo - 1] + "\n");
		}
		out.write("Mean:\n");
		out.write("" + StatUtils.mean(sampleResults) + "\n");
		out.write("Variance:\n");
		out.write("" + StatUtils.variance(sampleResults) + "\n\n");
		out.flush();
	}
}
