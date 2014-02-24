import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DecisionTree {
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private ArrayList<Line> lines = new ArrayList<Line>();
	private String[] classValues;

	public DecisionTree(String filename){
		ArrayList<String[]> temp = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			int attributeIndex = 0;
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
							addAttribute(line.substring(end+1, line.length()), attributeIndex);
							attributeIndex++;
						}else if(type.equals("data")){
							line = br.readLine();
							while (line != null) {
								if(line.length() > 0){
									temp.add(line.split(","));
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
		int i = 0;
		double[] split = new double[attributes.size()];
		for(Attribute a : attributes){
			if(!a.isDiscrete){
				for(String[] s : temp){
					split[i] += Double.parseDouble(s[i]);
				}
				split[i] /= temp.size();
				a.split = split[i];
				
				for(String[] s : temp){
					if(Double.parseDouble(s[i]) > split[i]){
						s[i] = "above "+split[i];
					}else{
						s[i] = "below "+split[i];
					}
				}
			}
			i++;
		}
		
		classValues = attributes.get(attributes.size()-1).getValues();
		
		for(String[] s : temp){
			Line l = new Line(s);
			lines.add(l);
		}
		
		//System.out.println(attributes);// + " ("+v+")");
		
		ArrayList<Attribute> attrClone = (ArrayList<Attribute>)attributes.clone();
		attrClone.remove(attrClone.size()-1);
		
		Node root = dtl(lines, attrClone, lines);
		System.out.println(root);
	}

	// Returns true if discrete, false if continuous
	private void addAttribute(String line, int index) {
		int start = 0;
		int end = line.indexOf(' ', start);
		String name = line.substring(start, end);

		start = end+1;
		String v = line.substring(start+1, line.length()-1);
		String[] values = {};
		if(line.charAt(start) == '{'){
			v = v.trim();
			values = v.split(", ");
		}else if(line.substring(start, line.length()).equals("real")){
			// real, do nothing
		}
		Attribute a = new Attribute(name, values, index);
		attributes.add(a);
		//System.out.println(a.name + ": " + a.values);// + " ("+v+")");
	}
	private Node dtl(List<Line> examples, ArrayList<Attribute> attributes, List<Line> parentExamples){
		if(examples.isEmpty()){
			return pluralityValue(parentExamples);
		}else if(allPositive(examples)){
			return new EndNode(classValues[0]);
		}else if(allNegative(examples)){
			return new EndNode(classValues[1]);
		}else if(attributes.isEmpty()){
			return pluralityValue(examples);
		}else{
			Attribute A = null;
			double maxGain = -1;
			for(Attribute a: attributes){
				double gain = informationGain(a, examples);
				if(gain > maxGain){
					A = a;
					maxGain = gain;
				}
			}
			ArrayList<Attribute> attClone = (ArrayList<Attribute>) attributes.clone();
			attClone.remove(A);
			IntermediateNode tree = new IntermediateNode(A.name, A.getValues());
			for(String vk : A.getValues()){
				List<Line> exs = new ArrayList<Line>();
				for(Line l : examples){
					if(l.getValue(A).equals(vk)){
						exs.add(l);
					}
				}
				Node subtree = dtl(exs, attClone, examples);
				tree.add(subtree);
			}
			return tree;
		}
	}
	
	private int[] countPosAndNeg(List<Line> examples) {
		int[] posneg = new int[]{0, 0};
		for (Line line : examples) {
			String val = line.getLastValue();
			if(line.getLastValue().equals(classValues[0])){
				posneg[0]++;
			}else if(line.getLastValue().equals(classValues[1])){
				posneg[1]++;
			}else{
				System.out.println("OHH NOOO!");
			}
		}
		return posneg;
	}
	
	private boolean allNegative(List<Line> examples) {
		int[] posneg = countPosAndNeg(examples);
		return posneg[0] == 0;
	}

	private boolean allPositive(List<Line> examples) {
		int[] posneg = countPosAndNeg(examples);
		return posneg[1] == 0;
	}

	private double informationGain(Attribute a, List<Line> examples) {
		double gain = entropy(examples);
		for(String v: a.getValues()){
			List<Line> exs = new ArrayList<Line>(); 
			for(Line l: examples){
				if(l.getValue(a).equals(v)){
					exs.add(l);
				}
			}	
			gain -= entropy(exs) * exs.size() / examples.size();
		}
		
		return gain;
	}
	
	private double entropy(List<Line> exs){
		int[] posneg = countPosAndNeg(exs);
		int p = posneg[0];
		int n = posneg[1];
		double x = p / (double)(p+n);
		if(posneg[0] == 0 || posneg[1] == 0){
			return 0;
		}
		double entropy = - x * log2(x) - (1 - x) * log2(1-x); 
		return entropy;
	}
	private static double log2(double x){
		return Math.log(x) / Math.log(2);
	}

	private EndNode pluralityValue(List<Line> examples) {
		int count = 0;
		for (Line line : examples) {
			if(line.getLastValue().equals(classValues[0])){
				count++;
			}else{
				count--;
			}
		}
		return new EndNode( (count>0) ? classValues[0] : classValues[1]);
	}
	
	public static void main(String[] args){
		DecisionTree d = new DecisionTree(args[0]);
	}
}
