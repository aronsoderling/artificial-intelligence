import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class DecisionTree {
	private ArrayList<Attribute> attributes;

	public DecisionTree(String filename){
		attributes = new ArrayList<Attribute>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				if(line.length() > 0){
					char startChar = line.charAt(0);

					if(startChar == '%'){
						// comment, do nothing
					}else if(startChar == '@'){
						int end = line.indexOf(' ');
						String type;

						if(end == -1){
							type = line.substring(1);
						}else{
							type = line.substring(1, end);
						}

						if(type.equals("attribute")){
							addAttribute(line.substring(end+1, line.length()));
						}else if(type.equals("data")){
							line = br.readLine();
							while (line != null) {
								if(line.length() > 0){
									String[] d = line.split(",");
									boolean positive = d[ d.length-1 ].equals("yes");
									for(int i=0; i<d.length-1; i++){
										if(positive){
											attributes.get(i).addPositiveToValue(d[i]);
										}else{
											attributes.get(i).addNegativeToValue(d[i]);
										}
									}
								}
								line = br.readLine();
							}
						}
					}
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(attributes);// + " ("+v+")");
	}

	private void addAttribute(String line) {
		int start = 0;
		int end = line.indexOf(' ', start);
		String name = line.substring(start, end);

		start = end+1;
		String v = line.substring(start+1, line.length()-1);
		String[] values = {};
		if(line.charAt(start) == '{'){
			values = v.split(", ");
		}else if(line.substring(start, line.length()).equals("real")){
			// real, do nothing
		}
		Attribute a = new Attribute(name, values);
		attributes.add(a);
		System.out.println(a.name + ": " + a.values);// + " ("+v+")");
	}

	private Node dtl(){
		if(examples.isEmpty()){
			return pluralityValue(parentExamples);
		}else if(examples.allPositive()){
			return new Node(true);
		}else if(examples.allNegative()){
			return new Node(false);
		}else if(attributes.isEmpty()){
			return pluralityValue(examples);
		}else{
			Attribute A;
			int maxGain = Integer.MIN_VALUE;
			for(Attribute a: attributes){
				int gain = informationGain(a, examples);
				if(gain > maxGain){
					A = a;
					maxGain = gain;
				}
			}
			Node node = new Node();
			for(){
				
			}
			return node;
		}
	}
	private class Node{
		
		private Node(boolean b){
			
		}
		private Node(){
			
		}
	}
	
	public static void main(String[] args){
		DecisionTree d = new DecisionTree("weather.arff");
	}
}
