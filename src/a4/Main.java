package a4;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import a4.Viterbi.Backlink;
import a4.Viterbi.Pair;

public class Main {
	public static void main(String[] args) {
		//args = new String[5];
		/*args[0] = "count";
		args[1] = "corpus-train-pos.txt";
		args[2] = "POS";*/
		
		/*args[0] = "confusion";	
		args[1] = "corpus-development-pos.txt";*/
		
		/*args[0] = "baseline";
		args[1] = "corpus-train-pos.txt";
		args[2] = "corpus-development-pos.txt";
		args[3] = "huhu.txt";*/
		
		/*args[0] = "bigrams";
		args[1] = "corpus-development-pos.txt";*/
		
		/*args[0] = "noisy";
		args[1] = "corpus-train-pos.txt";
		//args[2] = "that round table might collapse";
		args[2] = "one-line.txt";
		args[3] = "8";
		args[4] = "huhu.flexa";*/
		
		/*args[0] = "viterbi";
		args[1] = "corpus-train-pos.txt";
		args[2] = "corpus-test-words.txt"; 
		args[3] = "5";
		args[4] = "huhu.flexa";*/
		
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
		}else if(args[0].equals("confusion")){
			TreeMap<String, TreeMap<String, Double>> mapmap = U.getConfusionMatrix(args[1]);
			DecimalFormat df = new DecimalFormat("#.####");
			
			
			for (Entry<String, TreeMap<String, Double>> map : mapmap.entrySet()) {
				System.out.print(map.getKey()+" = ");
				for (Entry<String, Double> e : map.getValue().entrySet()) {
					System.out.print(e.getKey()+":"+df.format(e.getValue())+" ");
				}
				System.out.println();
			}
		}else if(args[0].equals("baseline")){
			TreeMap<String, String> berit = BaselineTagger.init(args[1]);
			//System.out.println(berit);
			
			BaselineTagger.tag(args[2], args[3], berit);
			
			TreeMap<String, Double> eval = U.evaluateTagger(args[3]);
			for(Map.Entry<String, Double> e : eval.entrySet()){
				System.out.println(e.getKey() + " : " + e.getValue());	
			}
		}else if(args[0].equals("bigrams")){
			TreeMap<String, TreeMap<String, Integer>> bigrams = Bigram.generateBigrams(args[1]);
			TreeMap<String, TreeMap<String, Double>> ptt = Bigram.generatePtt(bigrams);
			TreeMap<String, TreeMap<String, Double>> pwt = U.generatePwt(args[1]);
			System.out.println("P(Ti|Ti-1): "+ptt);
			System.out.println("P(Wi|Ti): "+pwt);
			
		}else if(args[0].equals("noisy")){
			TreeMap<String, TreeMap<String, Double>> pwt = U.generatePwt(args[1]);
			String[] split = args[2].split(" ");
			if(split.length>1){
				LinkedHashMap<String, TreeMap<String, Double>> table = NoisyChannelTagger.generateTable(split, pwt);
				LinkedHashMap<String, String> optimalPath = NoisyChannelTagger.exhaustiveSearch(table);
				System.out.println("Table: "+table);
				System.out.println("Exhaustive search optimal path: "+optimalPath);					
			}else{
				BufferedReader reader = U.getReader(args[2]);
				PrintWriter writer = U.getWriter(args[4]);
				while(true){
					ArrayList<String> sentence = U.readSentence(reader, Integer.parseInt(args[3])); //"that round table might collapse";
					if(sentence.size() == 0){
						break;
					}
					LinkedHashMap<String, TreeMap<String, Double>> table = null;
					table = NoisyChannelTagger.generateTable(sentence.toArray(new String[0]), pwt);

					LinkedHashMap<String, String> optimalPath = NoisyChannelTagger.exhaustiveSearch(table);
					//System.out.println("Table: "+table);
					System.out.println("Exhaustive search optimal path: "+optimalPath);	
				}
			}
			//System.out.println(table);			
		}else if(args[0].equals("viterbi")){
			String file = args[1];
			int err = 0;
			int ok = 0;
			TreeMap<String, TreeMap<String, Double>> pwt = U.generatePwt(file);
			BufferedReader reader = U.getReader(args[2]);
			PrintWriter writer = U.getWriter(args[4]);
			while(true){
				ArrayList<String> sentence = U.readSentenceLine(reader, Integer.parseInt(args[3])); //"that round table might collapse";
				if(sentence.size() == 0){
					break;
				}
				ArrayList<Pair<String, TreeMap<String, Backlink>>> table = null;
				try{
					table = Viterbi.generateTable(file, sentence, pwt);
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
					while(split.length < U.POS+1){
						backlink.line += "\t ";
						split = backlink.line.split("\t");
					}
					split[U.POS] = backlink.tag;
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
		}else{
			printUsage();
		}
	}

	private static void printUsage() {
		System.out.println("Syntax error");
	}
}
