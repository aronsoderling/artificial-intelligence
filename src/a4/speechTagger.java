package a4;
import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class speechTagger {
	
	static final int WORD = 1;
	static final int POS = 4;
	static final int PPOS = 5;
	
	
	public speechTagger(){
		
	}
	public BufferedReader getReader(String file){
		BufferedReader r = null;
		FileInputStream fs;
		try {
			fs = new FileInputStream(file);
			InputStreamReader is = new InputStreamReader(fs, "UTF-8");
			r = new BufferedReader(is);
		
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public TreeMap<String, Integer> count(String file, int position){
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		
			try{
				BufferedReader r = getReader(file);
				String line = null;
				String[] splitLine = null;
				String currentKey = null;
				int count;
				
				while ((line = r.readLine()) != null){
					splitLine = line.split("\t");
					if(splitLine.length > position){
						currentKey = splitLine[position];
						count = 1;
						if(map.containsKey(currentKey)){
							count = map.get(currentKey) + 1;
						}
						map.put(currentKey, count);
					}
				}
				r.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		return map;
	}
	
	public TreeMap<String, Double> evaluateTagger(String file){
		
		TreeMap<String, Integer> correctPos = new TreeMap<String, Integer>();
		TreeMap<String, Integer> pos = new TreeMap<String, Integer>();
		
		try {
			BufferedReader r = getReader(file);
			String line = null;
			String[] splitLine = null;
			String currentPos = null;
			String currentPpos = null;
			int currentPosCorrectCount;
			int currentPosCount;
			
			while ((line = r.readLine()) != null){
				splitLine = line.split("\t");
				if(splitLine.length > POS){
					currentPos = splitLine[POS];
					currentPpos = splitLine[PPOS];
					
					if(currentPos.equals(currentPpos)){
						currentPosCorrectCount = 1;
						if(correctPos.containsKey(currentPos)){
							currentPosCorrectCount = correctPos.get(currentPpos) + 1;
						}
						correctPos.put(currentPpos, currentPosCorrectCount);
					}
					currentPosCount = 1;
					if(pos.containsKey(currentPos)){
						currentPosCount = pos.get(currentPos) +1;
					}
					pos.put(currentPos, currentPosCount);
				}	
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		TreeMap<String, Double> eval = new TreeMap<String, Double>();
		
		String epos = null;
		int pposCount;
		int posCount;
		double accuracy;
		for(Map.Entry<String, Integer> e : correctPos.entrySet()){
				epos = e.getKey();
				pposCount = e.getValue();
				posCount = pos.get(epos);
				accuracy = (double)pposCount /(double) posCount;
				eval.put(epos, accuracy);
		}
		
		return eval;
	}
	
	public TreeMap<String, TreeMap<String, Double>> getConfusionMatrix(String file){
		TreeMap<String, TreeMap<String, Integer>> pos = new TreeMap<String, TreeMap<String, Integer>>();
		
		try {
			BufferedReader r = getReader(file);
			String line = null;
			String[] splitLine = null;
			String currentPos = null;
			String currentPpos = null;
			int currentPosCount;
			
			while ((line = r.readLine()) != null){
				splitLine = line.split("\t");
				if(splitLine.length > POS){
					currentPos = splitLine[POS];
					currentPpos = splitLine[PPOS];
					
					if(!pos.containsKey(currentPos)){
						pos.put(currentPos, new TreeMap<String, Integer>());
					}
					
					currentPosCount = 1;
					if(pos.get(currentPos).containsKey(currentPpos) ){
						currentPosCount = pos.get(currentPos).get(currentPpos) +1;
					}
					pos.get(currentPos).put(currentPpos, currentPosCount);
				}	
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		TreeMap<String, TreeMap<String, Double>> eval = new TreeMap<String, TreeMap<String, Double>>();
		
		String epos = null;
		for(Entry<String, TreeMap<String, Integer>> e : pos.entrySet()){
				epos = e.getKey();
				TreeMap<String, Double> map = new TreeMap<String, Double>();
				double all = 0;
				for(Entry<String, Integer> e2 : e.getValue().entrySet()){
					all += e2.getValue();
				}
				
				for(Entry<String, Integer> e2 : e.getValue().entrySet()){
					map.put(e2.getKey(), e2.getValue()/all);
				}
				
				eval.put(epos, map);
		}
		
		return eval;
	}

	public static void main(String[] args) {
		speechTagger st = new speechTagger();	
		/*TreeMap<String, Integer> words = st.count("corpus-train-pos.txt", POS);
		for(Map.Entry<String, Integer> e : words.entrySet()){
			System.out.println(e.getKey() + " : " + e.getValue());	
		}*/
		
		TreeMap<String, Double> eval = st.evaluateTagger("huhu.txt");
		for(Map.Entry<String, Double> e : eval.entrySet()){
			System.out.println(e.getKey() + " : " + e.getValue());	
		}
		
		TreeMap<String, TreeMap<String, Double>> confusion = st.getConfusionMatrix("corpus-development-pos.txt");
		System.out.println(confusion);
		
	}

}
