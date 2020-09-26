import java.util.HashMap;
/*
 * key is character to be stored in Trie
 * value contains no. of occurrence of key in each page
 * children contains the reference to the children of node.
 */
public class TrieNode<t> {

	char key;
	t value;
	HashMap<Character, TrieNode<t>> children;
	
	public TrieNode() {
		this.children = new HashMap<Character, TrieNode<t>>();
	}
	
	public TrieNode(char key) {
		this.key = key;
		this.children = new HashMap<Character, TrieNode<t>>();
	}
}
