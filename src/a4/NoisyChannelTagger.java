package a4;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class NoisyChannelTagger {
	public static LinkedHashMap<String, TreeMap<String, Double>> generateTable(String[] words, TreeMap<String, TreeMap<String, Double>> pwt) {
		LinkedHashMap<String, TreeMap<String, Double>> result = new LinkedHashMap<String, TreeMap<String, Double>>();
		for (String woord : words) {
			TreeMap<String, Double> coolumn = new TreeMap<String, Double>();
			for (Map.Entry<String, TreeMap<String, Double>> s : pwt.entrySet()) {
				if(s.getValue().containsKey(woord)){
					coolumn.put(s.getKey(), s.getValue().get(woord));
				}
			}
			result.put(woord, coolumn);
		}
		return result;
	}
	
	
	static LinkedHashMap<String, String> exhaustiveSearch(LinkedHashMap<String, TreeMap<String, Double>> table) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (Map.Entry<String, TreeMap<String, Double>> e : table.entrySet()) {
			double max = -1;
			String maxPos = null;
			for (Map.Entry<String, Double> e2 : e.getValue().entrySet()) {
				if(e2.getValue() > max){
					max = e2.getValue();
					maxPos = e2.getKey();
				}
			}
			result.put(e.getKey(), maxPos);
		}
		return result;
	}
	
	public static void main(String[] args) {
		TreeMap<String, TreeMap<String, Double>> pwt = U.generatePwt("corpus-train-pos.txt");
		LinkedHashMap<String, TreeMap<String, Double>> table = generateTable("that round table might collapse".split(" "), pwt);
		System.out.println(table);
		
		LinkedHashMap<String, String> optimalPath = exhaustiveSearch(table);
		System.out.println("Exhaustive search optimal path: "+optimalPath);
	}
}
