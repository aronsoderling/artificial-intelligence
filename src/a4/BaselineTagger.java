package a4;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.TreeMap;
import java.util.Map.Entry;


public class BaselineTagger {
	
	static final int WORD = 1;
	static final int LEMMA = 2;
	static final int POS = 4;
	static final int PPOS = 5;
	public BaselineTagger() {
		
	}
	
	public TreeMap<String, String> init(String file){
		TreeMap<String, TreeMap<String, Integer>> map = new TreeMap<String, TreeMap<String, Integer>>();
		
		try{
			BufferedReader r = getReader(file);
			String line = null;
			String[] splitLine = null;
			String word = null;
			String pos = null;
			int count;
			
			while ((line = r.readLine()) != null){
				splitLine = line.split("\t");
				if(splitLine.length > LEMMA){
					word = splitLine[LEMMA];
					pos = splitLine[POS];
					
					TreeMap<String, Integer> posMap = map.get(word);
					if(posMap == null){
						posMap = new TreeMap<String, Integer>();
						map.put(word, posMap);
					}
					
					count = 0;
					if(posMap.containsKey(pos)){
						count = posMap.get(pos);
					}
					posMap.put(pos, count+1);
				}
			}
			r.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		TreeMap<String, String> berit = new TreeMap<String, String>();
		
		String eword = null;
		for(Entry<String, TreeMap<String, Integer>> e : map.entrySet()){
				eword = e.getKey();
				
				int max = 0;
				String maxPos = "";
				for(Entry<String, Integer> e2 : e.getValue().entrySet()){
					int val = e2.getValue();
					if(val > max){
						max = val;
						maxPos = e2.getKey();
					}
				}
				
				berit.put(eword, maxPos);
		}
		
		return berit;
	}
	
	public void tag(String inputFile, String outputFile, TreeMap<String, String> berit){
		PrintWriter w = getWriter(outputFile);
		BufferedReader r = getReader(inputFile);
		String line = null;
		String[] s = null;
		String word = null;
		String pos = null;
		int count;
		
		try{
			while ((line = r.readLine()) != null){
				s = line.split("\t");
				if(s.length > LEMMA){
					word = s[LEMMA];
					
					//System.out.printf("%i\n", s[0]);
					w.printf("%s	%s	%s	%s	%s	%s\n", s[0], s[1], s[2], s[3], s[4], berit.get(word));
				}
			}
			r.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public PrintWriter getWriter(String file){
		PrintWriter writer = null;
		try {
		    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(file), "utf-8")));
			/*writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			          System.out, "utf-8")));*/
		} catch (IOException ex) {
		  ex.printStackTrace();
		} 
		return writer;
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
	
	public static void main(String[] args) {
		BaselineTagger b = new BaselineTagger();
		TreeMap<String, String> berit = b.init("corpus-train-pos.txt");
		System.out.println(berit);
		
		b.tag("corpus-development-pos.txt", "huhu.txt", berit);
	}
}
