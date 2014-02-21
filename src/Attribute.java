import java.util.ArrayList;
import java.util.List;


class Attribute{
	String name;
	boolean isDiscrete;
	public int split;
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
	
	private void calculateSplit(List<Line> lines){
		/*split = 0;
		Line<>
		for(Value v : values){
			if(v instanceof ContinuousValue){
				ContinuousValue cv = ((ContinuousValue) v);
				split += cv.value;
			}
		}
		split /= values.size();*/
		
	}

	public void discretize() {
		/*if(!isDiscrete){
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
			
		}*/
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
			return new String[]{"above", "below"};
		}
	}
}