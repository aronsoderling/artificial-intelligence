
public class EndNode extends Value {
	String name;
	int positives;
	int negatives;
	
	public EndNode(String n) {
		this.name = n;
	}
	
	public String toString(){
		return name +"["+positives+","+negatives+"]";
	}

	public boolean equals(Object o){
		return ((EndNode)o).name.equals(name);
	}
}
