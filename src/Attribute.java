import java.util.ArrayList;


class Attribute{
	String name;
	ArrayList<Value> values;
	boolean isDiscrete;

	public Attribute(String n, String[] val){
		name = n;
		values = new ArrayList<Value>();
		if(val.length == 0){ // continuous value
			isDiscrete = false;
		}else{ // discrete value
			isDiscrete = true;
			for(String s : val){
				values.add(new DiscreteValue(s));
			}
		}
	}
	
	public void addPositiveToValue(String value) {
		if(isDiscrete){
			DiscreteValue v = (DiscreteValue) values.get( values.indexOf(new DiscreteValue(value)) );
			v.positives++;
		}else{
			values.add(new ContinuousValue(Double.parseDouble(value), true));
		}
	}
	public void addNegativeToValue(String value) {
		if(isDiscrete){
			DiscreteValue v = (DiscreteValue) values.get( values.indexOf(new DiscreteValue(value)) );
			v.negatives++;
		}else{
			values.add(new ContinuousValue(Double.parseDouble(value), false));
		}
	}
	
	public String toString(){
		return name+"["+values.toString()+"]";
	}
}