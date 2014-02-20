
public class IntermediateNode extends Value {
	double value;
	boolean positive;
	
	public IntermediateNode(double value, boolean positive) {
		this.value = value;
		this.positive = positive;
	}
	
	public String toString(){
		return value+( (positive)?"+":"-" );
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
}
