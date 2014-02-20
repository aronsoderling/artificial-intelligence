
public class ContinuousValue extends Value {
	double value;
	boolean positive;
	
	public ContinuousValue(double value, boolean positive) {
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
