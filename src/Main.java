import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	/*
	 * User will enter word that he wants to search,
	 * Already web crawled URLs are stored in fetchedURLs.txt file.
	 */
	public static void main(String[] args) {
		
		SearchEngine searchEngine = new SearchEngine("FetchedURLs.txt");
		
		Scanner sc= new Scanner(System.in);
		
		System.out.println("Enter a word you want to search in uwindsor.ca");

		String word = sc.nextLine();

		try {
			/*
			 * Loop until user enter "no" or input is null.
			 */
			while (!word.equals("no") && !word.equals(null)) {
				/*
				 * split word based on unnecessary characters
				 */
				String[] wordArray = word.split(" ");
				/*
				 * webPages is array of URLs that contains the word
				 */
				String[] webPages = searchEngine.search(wordArray);
				try {
			  		if (webPages == null) {
						System.out.println("Did you mean?");
						/*
						 * display suggestions to the user using EditDistanceMethod.
						 */
						Map<String, Integer> suggestions= searchEngine.suggestions(word);
						int i =1;
						/*
						 * print only top 5 suggestions.
						 */
						for(Map.Entry<String, Integer> entry : suggestions.entrySet()) {
							if(i <= 5) {
								System.out.println(i+". " + entry.getKey());
								i++;
							}
							else
								break;
						}
					}
					else {
						/*
						 * store input words total occurrence in given specific URL using map data structure.
						 */
						Map<String, Integer> wordOccurence = new HashMap<>();
						/*
						 * count total number of occurrence of input word for each URL in webPages array and put it in map.
						 */
						for (String url : webPages) {
							int wordCount = CountWord.getWordCount(url, word);
							wordOccurence.put(url, wordCount);
						}
						//to sort the links according to occurrence of the word in descending order
						Map<String, Integer> sortedMap = new LinkedHashMap<>();
						sortedMap = rankPage(wordOccurence);
				        
						/*
						 * print URLs related to search
						 */
						int i=1;
						System.out.println("List of Top 10 related webpages (High to Low)");
						System.out.println("occurrence \t Your Search Result");
				        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
				        	if(i<=10) {
				        		System.out.println(entry.getValue()+"\t \t"+entry.getKey());
				        		i++;
				        	}
				        	else
				        		break;
				        }

					}
			  		

				}catch (NullPointerException e) {

					System.out.println("sorry");
				}

				System.out.println("\nEnter another word to search (\"no\" to exit):");
				
				word = sc.nextLine();
			}
		} catch (NullPointerException e) {
			
		}

		

	}
	
	/*
	 * this will rank pages using sorting by word's occurrences in webpages
	 */
	public static Map<String, Integer> rankPage(Map<String, Integer> unsortedList){
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        unsortedList
        	.entrySet()
        	.stream()
        	.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        
        return sortedMap;
	}
	
}


