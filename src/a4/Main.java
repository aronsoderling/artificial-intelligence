package a4;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Main {
	public static void main(String[] args) {
		args = new String[3];
		args[0] = "count";
		args[1] = "corpus-train-pos.txt";
		args[2] = "POS";
		
		if(args[0].equals("eval")){
			TreeMap<String, Double> map = U.evaluateTagger(args[1]);
			for (Entry<String, Double> e : map.entrySet()) {
				System.out.println(e.getKey()+":"+e.getValue());
			}
		}else if(args[0].equals("count")){
			int pos = U.WORD;
			if(args[2] == "POS"){
				pos = U.POS;
			}
			TreeMap<String, Integer> map = U.count(args[1], pos);
			for (Entry<String, Integer> e : map.entrySet()) {
				System.out.println(e.getKey()+": "+e.getValue());
			}
		}
	}
}
