import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;

public class Web_Crawler {
	/*
	 * creating a list of URL
	 */
	   public static List<URL> crawl(URL rootSite, int limit) {
	      List<URL> listOfURLs = new ArrayList<URL>(limit);
	      listOfURLs.add(rootSite);

	       /*
	        * We make a set of URL named setURL which is same as the listOfURLs, because set contains only one instance of URL,
	        * where as list can contain multiple entries for same URL.
	        */
	      Set<URL> setURL = new HashSet<URL>(listOfURLs);
	      int currentPosition = 0;
	      /*
	       * Loop until desired limit achieved or there isn't any new URL to visit.  
	       */
	      while (listOfURLs.size() < limit && currentPosition < listOfURLs.size()) {
	          URL currentUrl = listOfURLs.get(currentPosition);
	          /*
	           * extract all links from current web page
	           */
	          for (URL url : extractLinks(currentUrl)) {
	        	  /*
	        	   * check if URL is already in set or not, If not then add in set and list
	        	   */
	              if (setURL.add(url)) {
	                  listOfURLs.add(url);
	                  /*
	                   * If limit is achieved then break the loop
	                   */
	                  if (listOfURLs.size() == limit) {
	                      break;
	                  }
	              }
	          }
	          currentPosition++;
	      }
	      return listOfURLs;
	   }

	   public static void main(String[] args) {
	       try {
	           URL i = new URL("http://www.uwindsor.ca");
	           int limit = 1000;

	           long startTime = System.currentTimeMillis();
	           List<URL> fetchedURLs = Web_Crawler.crawl(i, limit);
	           long endTime = System.currentTimeMillis();
	           System.out.println("Fetched URLs: ");
	           /*
	            * start with 1 because at 0 it stores root website's URL
	            */
	           int currentPosition = 1;
	           Iterator<URL> iterator = fetchedURLs.iterator();
	           /*
	            * store fetched URLs in text file.
	            */
	           String textFileName = "FetchedURLs.txt";
	           FileWriter fw=new FileWriter(textFileName);
	           while (iterator.hasNext() && currentPosition <= limit) {
	        	   URL currentURL = iterator.next();
	        	   fw.write(currentURL.toString() +"\n");
	               System.out.println(currentPosition+". \t" +currentURL);
	               currentPosition++;
	           }
	           fw.close();
	           System.out.println("total time to crawl : " + (endTime-startTime) + " ms");
	       }
	       catch (MalformedURLException e) {
	           System.err.println("The URL to start crawling with is invalid.");
	       } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	    
	   	/*
	   	 * Extract all links contained in web page
	   	 * return set of links in order they occur
	    */
	   private static LinkedHashSet<URL> extractLinks(URL url) {
	       LinkedHashSet<URL> links = new LinkedHashSet<URL>();
	       Pattern p = Pattern.compile("href=\"((http://|https://|www).*?)\"", Pattern.DOTALL);
	       Matcher m = p.matcher(fetchContent(url));

	       while (m.find()) {
	           String linkStr = normalizeUrlStr(m.group(1));
	           try {
	               URL link = new URL(linkStr);
	               links.add(link);
	           }
	           catch (MalformedURLException e) {
	               System.err.println("Page at " + url + " has a link to invalid URL : " + linkStr + ".");
	           }
	       }
	       return links;
	   }

	   /*
	    * Fetch the content from URL and return fetched content
	    */
	   private static String fetchContent(URL url) {
	       StringBuilder stringBuilder = new StringBuilder();
	       try {
	           BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	           String line;
	           while ((line = in.readLine()) != null) {
	               stringBuilder.append(line);
	           }
	           in.close();
	       }
	       catch (IOException e) {
	           System.err.println("An error occured from " + url);
	       }
	       return stringBuilder.toString();
	   }

	  /*
	   * It normalizes the string representation of URL, to transform to URL object.
	   */
	   private static String normalizeUrlStr(String urlStr) {
	       if (!urlStr.startsWith("http"))
			urlStr = "http://" + urlStr;
	       if (urlStr.endsWith("/")) 
	           urlStr = urlStr.substring(0, urlStr.length() - 1);
	       if (urlStr.contains("#")) 
	           urlStr = urlStr.substring(0, urlStr.indexOf("#"));
	       
	       return urlStr;
	   }
}
