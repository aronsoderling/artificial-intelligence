package a4;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Bigram {
	public static TreeMap<String, TreeMap<String, Integer>> generateBigrams(String file){
		TreeMap<String, TreeMap<String, Integer>> map = new TreeMap<String, TreeMap<String, Integer>>();

		try{
			BufferedReader r = U.getReader(file);
			String line = null;
			String[] splitLine = null;
			String currentPos = null;
			String previousPos = null;
			TreeMap<String, Integer> coolTree;

			while ((line = r.readLine()) != null){
				splitLine = line.split("\t");
				if(splitLine.length > U.POS){
					currentPos = splitLine[U.POS];
					if(previousPos != null){
						if(map.containsKey(previousPos)){
							coolTree = map.get(previousPos);
							if(coolTree.containsKey(currentPos)){
								coolTree.put(currentPos, coolTree.get(currentPos) + 1);
								
							}else{
								coolTree.put(currentPos, 1);
							}
						}else{
							coolTree = new TreeMap<String, Integer>();
							coolTree.put(currentPos, 1);
							map.put(previousPos, coolTree);
						}
					}
					previousPos = currentPos;
				}
			}
			r.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}

	public static TreeMap<String, TreeMap<String, Double>> generatePtt(TreeMap<String, TreeMap<String, Integer>> bigrams){
		TreeMap<String, TreeMap<String, Double>> r = new TreeMap<String, TreeMap<String, Double>>();
		
		for (Map.Entry<String, TreeMap<String, Integer>> e : bigrams.entrySet()) {
			int total = 0;
			for (Map.Entry<String, Integer> e2 : e.getValue().entrySet()) {
				total += e2.getValue();
			}
			
			TreeMap<String, Double> kuulT = new TreeMap<String, Double>();
			for (Map.Entry<String, Integer> e2 : e.getValue().entrySet()) {
				kuulT.put(e2.getKey(), e2.getValue() / (double) total);
			}
			r.put(e.getKey(), kuulT);
		}
		
		return r;
	}
	
	
	
	public static void printClasses(String word, TreeMap<String, TreeMap<String, Double>> pwt) {
		for (Map.Entry<String, TreeMap<String, Double>> s : pwt.entrySet()) {
			if(s.getValue().containsKey(word)){
				System.out.println(word+": "+s.getKey()+" "+s.getValue().get(word));
			}
		}
	}

	public static void main(String[] args) {
		TreeMap<String, TreeMap<String, Integer>> bigrams = generateBigrams("corpus-development-pos.txt");
		TreeMap<String, TreeMap<String, Double>> ptt = generatePtt(bigrams);
		
		System.out.println(ptt);
		
		
	}
}
