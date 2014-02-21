import java.util.ArrayList;
import java.util.List;


class Attribute{
	String name;
	ArrayList<Value> values;
	boolean isDiscrete;
	public int split;

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
		return name+values.toString();
	}
	
	private void calculateSplit(){
		split = 0;
		for(Value v : values){
			if(v instanceof ContinuousValue){
				ContinuousValue cv = ((ContinuousValue) v);
				split += cv.value;
			}
		}
		split /= values.size();
		
	}

	public void discretize() {
		if(!isDiscrete){
			calculateSplit();
			List<Value> continuous = (List<Value>) values.clone();
			values.clear();

			DiscreteValue above = new DiscreteValue("above split");
			DiscreteValue below = new DiscreteValue("below split");
			values.add(above);
			values.add(below);
			
			for(Value v : continuous){
				ContinuousValue cv = (ContinuousValue)v;
				if(cv.value > split){
					if(cv.positive){
						above.positives++;
					}else{
						above.negatives++;
					}
				}else{
					if(cv.positive){
						below.positives++;
					}else{
						below.negatives++;
					}
				}
			}
			
		}
	}
}