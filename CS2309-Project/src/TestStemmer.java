import org.tartarus.snowball.ext.*;

public class TestStemmer {
	static public void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		englishStemmer stemmer = new englishStemmer();
		stemmer.setCurrent("didn't");
		stemmer.stem();
		String curr = stemmer.getCurrent();
		System.out.println(curr);
	}
}
