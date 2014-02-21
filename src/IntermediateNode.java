import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class IntermediateNode extends Node {
	private String s;
	private ArrayList<Node> children = new ArrayList<Node>();
	private List<String> branchNames;
	
	public IntermediateNode(String s, String[] strings) {
		this.s = s;
		branchNames = Arrays.asList(strings);
	}
	
	public String toString(){
		String r = "";
		int i = 0;
		for(Node n : children){
			String b =  n.toString();
			r += s + " = " + branchNames.get(i);
			if(n instanceof EndNode){
				r += " : " + b + "\n";
			}else{
				b = b.replaceAll("(?m)^", "\t");
				r += "\n" + b;
			}
			//r += "\n";
			
			i++;
		}
		
		return r;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public void add(Node subtree) {
		children.add(subtree);
	}
}
