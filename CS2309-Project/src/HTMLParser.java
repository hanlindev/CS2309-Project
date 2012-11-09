/*
 * Parse HTML file, only store headings and paragraphs
 */
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.BreakIterator;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
public class HTMLParser implements Runnable{
	Path dir;
	BufferedWriter writer;// the writer used to overwrite the original file
	Document doc;
	Elements headings;
	Elements paragraphs;
	
	public HTMLParser(Path dir){
		this.dir = dir;
	}
	
	@Override
	public void run(){
		try {
			doc = Jsoup.parse(dir.toFile(), null);
		} catch(Exception e) {
			System.err.println(e);
			return;
		}
		
		// Get heading from 1 to 6
		String headingTag = "h";
		for (int i = 1; i <= 6; ++i) {
			String tag = headingTag + i;
			Elements tempStore = doc.getElementsByTag(tag);
			if (headings == null) {
				headings = tempStore;
			} else {
				headings.addAll(tempStore);
			}
		}
		
		// Get paragraphs
		paragraphs = doc.getElementsByTag("p");
		
		// In the following processing, it is assumed that the html
		// elements are well formed. Specifically, no other heading
		// elements are contained in another heading element. And no
		// paragraph is contained in another paragraph.
		
		// What we'll do is to parse the content into sentences and write
		// it out to the original file. Each line is a sentence.
		// The name of the original file won't
		// be modified so although the extension is still htm, it won't be
		// a html file anymore.
		
		try {
			writer = Files.newBufferedWriter(dir, StandardCharsets.UTF_8);
			// Process headings
			ProcessElements(headings);
			ProcessElements(paragraphs);
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
	}
	
	private void ProcessElements(Elements aElts) throws IOException {
		// This is used to parse text content into sentences.
		BreakIterator sentenceBoundary = BreakIterator.getSentenceInstance();
		//Iterate through all elements in the list
		for (Element elt : aElts) {
			// get the text content of an element and its children
			String text = elt.text();
			sentenceBoundary.setText(text);
			
			// Break the text content into sentences and print to the buffered writer.
			int start = sentenceBoundary.first();
			for (int end = sentenceBoundary.next(); end != BreakIterator.DONE; start = end, end = sentenceBoundary.next()) {
				writer.write(text.substring(start, end) + "\n");
			}
			writer.flush();
		}
	}

}
