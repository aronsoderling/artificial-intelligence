
public class DiscreteValue extends Value {
	String name;
	int positives;
	int negatives;
	
	public DiscreteValue(String n) {
		this.name = n;
	}
	
	public String toString(){
		return name +"["+positives+","+negatives+"]";
	}

	public boolean equals(Object o){
		return ((DiscreteValue)o).name.equals(name);
	}
}
