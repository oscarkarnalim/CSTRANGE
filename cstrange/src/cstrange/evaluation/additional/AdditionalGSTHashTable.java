package cstrange.evaluation.additional;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * HASH table for Greedy String tiling algorithm
 */
public class AdditionalGSTHashTable {
	
	private HashMap<Long, ArrayList<Integer>> dict;
	
	public AdditionalGSTHashTable(){
		dict = new HashMap<Long,ArrayList<Integer>>();
	}
	
	/*
	 * Stores object 'ob' for key 'key' in a list. If there are already
     * objects stored in the list for the key -> 'ob' is appended.
	 */
	public void add(long h, int obj){
		ArrayList<Integer> newlist;
		if(dict.containsKey(h)){
			newlist = dict.get(h);
			newlist.add(obj);
			dict.put(h, newlist);
		}
		else{
			newlist = new ArrayList<Integer>();
			newlist.add(obj);
			dict.put(h, newlist);
		}
	}
	
	/*
	 * Returns a list with all objects for key 'key'. If the key does not exist
     * 'None' is returned.
	 */
	public ArrayList<Integer> get(long key){
		if(dict.containsKey(key))
			return dict.get(key);
		else
			return null;
	}
	
}
