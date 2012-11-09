import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// A collection of all articles collected
public class ArticleCollection implements Iterable<Article> {
	List<Article> articles;
	String rootDir = "Topics";
	
	public ArticleCollection() throws IOException{
		HashSet<String> usedDirectories = new HashSet<String>();
		articles = new LinkedList<Article>();
		Path root = Paths.get(rootDir);
		DirectoryStream<Path> stream = Files.newDirectoryStream(root);
		
		for (Path topicFolder : stream) {
			try {
				DirectoryStream<Path> subDir = Files.newDirectoryStream(topicFolder);
				for (Path file : subDir) {
					articles.add(new Article(file));
				}
			} catch(Exception e) {}
		}
	}
	
	@Override
	public Iterator<Article> iterator() {
		return articles.iterator();
	}
}

class Article implements Iterable<String> {
	List<String> sentences;

	public Article(Path file) throws IOException{
		sentences = new LinkedList<String>();
		BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		
		// Read lines from the file
		while (reader.ready()) {
			sentences.add(reader.readLine().toLowerCase());
		}
	}
	
	@Override
	public Iterator<String> iterator() {
		return sentences.iterator();
	}
	
}