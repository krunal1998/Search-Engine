import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SearchEngine {

	/* 
	 * The splitting is used to split the words excluding regular expressions or any other symbols in it
	 */
	private final String splitstrings = "[[ ]*|[,]*|[)]*|[(]*|[\"]*|[;]*|[-]*|[:]*|[']*|[’]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+";
	
	/*
	 * A file consisting of all the stop words like: a, an, the, etc.
	 */
	private final String stopwordsFileName = "StopWords.txt";
	
	/*
	 * A Trie is initialized using the ArrayList collection 
	 */
	private Trie<ArrayList<Integer>> trie;
	
	/*
	 * A web pages array is used to store all the crawled URLs of the web pages in a web site 
	 */
	private String [] fetchedWebPagesArray;
	
	private HashSet<String> distinctWords;
 	
	
	
	public SearchEngine(String websiteName) {
		
		System.out.println("Creating Trie for inverted index....");
		distinctWords = new HashSet<String>();
		/*
		 *  The Trie Data Structure is used for mapping words to references (i.e.) mapping words to the urls (w,L) format
		 */
		this.trie = new Trie<ArrayList<Integer>>();
		
		/*
		 *A Hash set is used to store the text information in  file and it does not contain any duplicates    
		 */
		HashSet<String> stop_words = readFile(stopwordsFileName);
		
		HashSet<String> temp = readFile(websiteName);
		
		/*
		 *  Convert the HashSet to String array and assign them to the web pages array
		 */
		this.fetchedWebPagesArray = temp.toArray(new String[0]);
		
		temp = null;
		String webPageContent;
		String word;
		String[] wordsInWebPage;
		
		/*
		 *Iterator in java which is used to get the string one by one  from the pages array that is to get each url one by one
		 */
		Iterator<String> iterator = null;
		 
		/*
		 *  The Page Index is used to keep track of the index of the URLs in a fetchedWebPagesArray
		 */
		for (int index = 0; index < this.fetchedWebPagesArray.length; ++index) {
			try {
				/*
				 * parse HTML web page into text
				 */
				webPageContent = parseWebPage(this.fetchedWebPagesArray[index]);
				webPageContent = webPageContent.toLowerCase();
				wordsInWebPage = webPageContent.split(splitstrings);
				
				/*
				 * the words are split.
				 * the wordsInWebPage array is then converted to the list and stored as hashset and assigned to temp 
				 */
				temp = new HashSet<String>(Arrays.asList(wordsInWebPage));
				 
				/*
				 *  removeall function will remove words from the array.
				 */
				temp.removeAll(stop_words); // remove all stop words from the page
				
				distinctWords.addAll(temp);
				
				iterator = temp.iterator();
				while(iterator.hasNext()) {
					word = (String) iterator.next();
					/*
					 *When we get an index call the search function in the trie if the trie is empty insert into the trie
					 *  else return list of occurrence of word in web pages
					 */
					ArrayList<Integer> ar = this.trie.search_word(word);
					if (ar == null) {
						/*
						 *  insert a new word referencing the current page
						 */
						this.trie.insert(word, new ArrayList<Integer>(Arrays.asList(index)));
					} else {
						/*
						 * add index of current page in list
						 */
						ar.add(index);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		} 
		System.out.println("The trie has " + this.trie.size + " entries.");
	}
	

	/*
	 * readPage method read text file and return data in HashSet data structure to reduce multiple instance
	 */
	private HashSet<String> readFile(String filename) {
		HashSet<String> hash = new HashSet<String>();
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				hash.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("No File Found");
			
			
		}
		catch (IOException e){
			System.out.println("Sorrry....");
		}
		return hash;
	}
	
	/*
	 * parse HTML web page into text using Jsoup library. 
	 */
	private String parseWebPage(String url) throws Exception {	
		Document doc = Jsoup.connect(url).get();
		String text = doc.body().text();
		return text;
	}
	
	/*
	 * indexTerm is array of words that will be searched, and returns the array of URLs that contains indexTerm words. 
	 */
	public String[] search (String[] indexTerm) {
		
		System.out.println("Searching....");
		int[] votes = new int[this.fetchedWebPagesArray.length];
		ArrayList<Integer> tmp = null;
		for (int i = 0; i < indexTerm.length; ++i) {
			
			tmp = this.trie.search_word(indexTerm[i].toLowerCase());
			if (tmp != null) {
				for (int k = 0; k < tmp.size(); k++) {
					votes[tmp.get(k)]++;
				}
			} else {
				System.out.println("No Word Found" );
				
				return null;
			}
		}
		/*
		 * webPages ArrayList stores the indexes of the webPages
		 */ 
		ArrayList<String> webPages = new ArrayList<String>();
		for (int p = 0; p < votes.length; ++p) {
			if (votes[p] == indexTerm.length) {
				webPages.add(this.fetchedWebPagesArray[p]);
			}
		}
		return webPages.toArray(new String[0]);
	}
	
	/*
	 * provide suggestions if search not found and return map that contains suggested word as key and editdistance as value
	 */

	public Map<String,Integer> suggestions(String word){
		Map< String,Integer> hm =  new HashMap< String,Integer>();
		for(String w: distinctWords) {
			if(w.contains(word)) {
				hm.put(w, CountWord.getEditDistance(word, w));
			}
		}
		/*
		 * sort map in ascending order
		 */
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		hm.entrySet()
		.stream()
		.sorted(Map.Entry.comparingByValue())
		.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		return sortedMap;
	}
}
