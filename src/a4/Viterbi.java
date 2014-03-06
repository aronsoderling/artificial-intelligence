package a4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Viterbi {
	private static TreeMap<String, Double> BOSTree = null;
	private static TreeMap<String, TreeMap<String, Double>> ptt = null;
	
	public static ArrayList<Pair<String, TreeMap<String, Backlink>>> generateTable(String file, ArrayList<String> sentence, TreeMap<String, TreeMap<String, Double>> pwt) {
		ArrayList<Pair<String, TreeMap<String, Backlink>>> result = new ArrayList<Pair<String, TreeMap<String, Backlink>>>();
		TreeMap<String, Backlink> prevCoolumn = null;
		String prevWoord = null;
		Backlink prevBacklink = null;
		
		for (String se : sentence) {
			String[] sev = se.split("\t");
			String woord = sev[U.LEMMA];
			TreeMap<String, Backlink> coolumn = new TreeMap<String, Backlink>();
			for (Map.Entry<String, TreeMap<String, Double>> s : pwt.entrySet()) {
				if(s.getValue().containsKey(woord)){
					String tag = s.getKey();
					double pWordTag = s.getValue().get(woord);
					if(prevCoolumn == null){
						double BOS = getBOS(tag, file);
						prevBacklink = new Backlink(pWordTag*BOS, tag, se);
						coolumn.put(tag, prevBacklink);
					}else{
						double previousMax = -1;
						String maxTag = "";
						double prob = 0;
						for (Map.Entry<String, Backlink> e : prevCoolumn.entrySet()) {
							String tag1 = e.getKey();
							String tag2 = tag;
							prob = getPtt(file, tag1, tag2) * e.getValue().probability;
							if(prob > previousMax){
								previousMax = prob;
								maxTag = tag1;
							}
							//System.out.println(tag1+ "-" + tag2+": "+prob);
						}
						TreeMap<String, Double> a = pwt.get(maxTag);
						double b = a.get(prevWoord);
						Backlink c = prevCoolumn.get(maxTag);
						prevBacklink = new Backlink(prob * b, tag, c, se);
						coolumn.put(tag, prevBacklink);
					}
				}
			}
			if(coolumn.isEmpty()){
				throw new RuntimeException("Not found");
			}else{
				prevCoolumn = coolumn;
				prevWoord = woord;
			}
			
			result.add(new Pair<String, TreeMap<String, Backlink>>(woord, coolumn));
		}
		return result;
	}
	
	private static double getPtt(String file, String tag1, String tag2){
		if(ptt == null){
			TreeMap<String, TreeMap<String, Integer>> bigrams = Bigram.generateBigrams(file);
			ptt = Bigram.generatePtt(bigrams);
		}
		try{
			return ptt.get(tag1).get(tag2);
		}catch(Exception e){
			return 0;
		}
	}
	
	private static double getBOS(String tag, String file) {
		if(BOSTree == null){
			BOSTree = new TreeMap<String, Double>();
			TreeMap<String, Integer> m = new TreeMap<String, Integer>();
			try{
				BufferedReader r = U.getReader(file);
				String line = null;
				String[] splitLine = null;
				String pos = null;
				int i;
				
				while ((line = r.readLine()) != null){
					splitLine = line.split("\t");
					if(splitLine.length > U.POS){
						pos = splitLine[U.POS];
						i = Integer.parseInt(splitLine[0]);
						if(i == 1){
							if(m.containsKey(pos)){
								m.put(pos, m.get(pos)+1);
							}else{
								m.put(pos, 1);
							}
						}
					}
				}
				r.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			int total = 0;
			for (Map.Entry<String, Integer> s : m.entrySet()) {
				total += s.getValue();
			}	
			for (Map.Entry<String, Integer> s : m.entrySet()) {
				BOSTree.put(s.getKey(), s.getValue()/ (double)total);				
			}
		}
		Double d = BOSTree.get(tag);
		if(d == null){
			return 0;
		}
		return d;
	}

	public static class Backlink{
		public double probability;
		public Backlink previous;
		public String tag; 
		public String line;
		
		public Backlink(double prob, String tag, Backlink prev, String line){
			probability = prob;
			previous = prev;
			this.tag = tag;
			this.line = line;
		}
		public Backlink(double prob, String tag, String line){
			probability = prob;
			this.tag = tag;
			this.line = line;
		}
		@Override
		public String toString() {
			return ((previous != null) ? previous.tag : "cool" ) + ":" + probability;
		}
	}
	
	public static class Pair<K, V>{
		public K key;
		public V value; 
		
		public Pair(K k, V v){
			key = k;
			value = v;
		}
		@Override
		public String toString() {
			return key+":"+value;
		}
	}
	
	public static void main(String[] args) {
		String file = "corpus-train-pos.txt";
		int err = 0;
		int ok = 0;
		TreeMap<String, TreeMap<String, Double>> pwt = U.generatePwt(file);
		BufferedReader reader = U.getReader("corpus-test-words.txt");
		PrintWriter writer = U.getWriter("huhu.flexa");
		while(true){
			ArrayList<String> sentence = U.readSentence(reader, 100); //"that round table might collapse";
			if(sentence.size() == 0){
				break;
			}
			ArrayList<Pair<String, TreeMap<String, Backlink>>> table = null;
			try{
				table = generateTable(file, sentence, pwt);
			}catch(RuntimeException e){
				err++;
				continue;
			}
			ok++;
			
			String[] path = new String[table.size()];
			String[] line = new String[table.size()];
			
			Backlink backlink = null;
			double max = -1;
			
			for(Map.Entry<String, Backlink> e : table.get(table.size()-1).value.entrySet() ){
				if(e.getValue().probability > max){
					backlink = e.getValue();
				}
			}
			
			int i = table.size() -1;
			while(backlink != null){
				path[i] = backlink.tag;
				String[] split = backlink.line.split("\t");
				split[split.length-1] = backlink.tag;
				line[i] = "";
				for (String s : split) {
					line[i] += (!line[i].equals("") ? "\t" : "" ) + s;
				}
				backlink = backlink.previous;
				i--;
			}
			
			//System.out.println(table);
			
			//System.out.println(sentence + " " + Arrays.asList(path));
			for (String l : line) {
				writer.println(l);
			}
			writer.println();
		}
		writer.close();
		System.out.println("Ok:"+ok+", err:"+err);
		//System.out.println(U.evaluateTagger("huhu.flexa"));
	}
}
