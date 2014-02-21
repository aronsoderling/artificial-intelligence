import java.util.ArrayList;


public class IntermediateNode extends Node {
	private Attribute attribute;
	private ArrayList<Node> children;
	
	public IntermediateNode(Attribute a) {
		attribute = a;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(Value v : attribute.values){
			if(v instanceof DiscreteValue){
				DiscreteValue dv = (DiscreteValue)v;
				sb.append(attribute.name);
				sb.append(" = ");
				sb.append(dv.name);
				sb.append("\n\t");
				for(Node n : children){
					sb.append(n.toString());
					sb.append("\n\t");
				}
			}else{
				throw new RuntimeException("YOU SHALL NOT TOSTRING BEFORE YOU DISCRETIZE!");
			}
		}
		
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
}
