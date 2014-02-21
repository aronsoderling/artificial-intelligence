
public class EndNode extends Node {
	String name;
	int positives;
	int negatives;
	
	public EndNode(String n) {
		this.name = n;
	}
	
	public String toString(){
		return name;
	}

	public boolean equals(Object o){
		return ((EndNode)o).name.equals(name);
	}
}
