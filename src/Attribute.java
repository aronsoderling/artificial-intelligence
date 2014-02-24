import java.util.ArrayList;
import java.util.List;


class Attribute{
	String name;
	boolean isDiscrete;
	public double split;
	private int index;
	private String[] values;

	public Attribute(String n, String[] val, int index){
		name = n;
		values = val;
		this.index = index;
		if(val.length == 0){ // continuous value
			isDiscrete = false;
		}else{ // discrete value
			isDiscrete = true;
		}
	}
	
	public String toString(List<Line> lines){
		return name;//+values.toString();
	}

	public int getIndex() {
		return index;
	}
	
	public String toString(){
		return name;
	}

	public String[] getValues() {
		if(isDiscrete){
			return values;
		}else{
			return new String[]{"above "+split, "below "+split};
		}
	}
}