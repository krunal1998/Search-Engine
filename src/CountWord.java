import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CountWord {
	
	public static int getWordCount (String url, String word)
	{
		int wordCount= 0;
		try {
			
			Document doc=null;
			
			/*
			 * parse HTML web page into text using Jsoup library. 
			 */
			//doc = Jsoup.connect(url).get();
			doc= Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
			String fileData = doc.body().text();
			
			/*
			 * convert fileData and word into lowercase
			 */
			fileData = fileData.toLowerCase();
			word = word.toLowerCase();
	    	
			/*
			 * Search word in fileData using BoyerMoore algorithm and calculate total occurrence in file.
			 */
			int M = word.length();
	        int N = fileData.length();
	        
	        int skip;
	        int count=0;
	     // position of rightmost occurrence of c in the pattern
	        int[] right = new int[9000];
	        for (int c = 0; c < 9000; c++)
	            right[c] = -1;
	        for (int j = 0; j < M; j++)
	            right[word.charAt(j)] = j;
	    
	       for (int i = 0; i <= N - M; i += skip) {
	        	skip = 0;
	            for (int j = M-1; j >= 0; j--) {
	            	if (word.charAt(j) != fileData.charAt(i+j)) {
	                   skip = Math.max(1, j - right[fileData.charAt(i+j)]);
	                   break;
	                }
	            }
	            if (skip == 0) // found
	            {
	            	   wordCount++;
	            	  i+=M;
	            }
	        
	        }
	       

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wordCount;


	}
	
	
	
    /*
     * calculate edit distance between two words
     */
	public static Integer getEditDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}

}
