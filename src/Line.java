import java.util.ArrayList;
import java.util.Arrays;


public class Line {
	String[] values;
	public Line(String[] l){
		values = l;
	}
	public String getValue(Attribute a) {
		return values[a.getIndex()];
	}
	public String getLastValue() {
		return values[ values.length - 1 ];
	}
	public String toString(){
		return Arrays.toString(values);
	}
}
