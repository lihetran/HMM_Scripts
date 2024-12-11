package deBruijn;

public class Edge {

	public String label;
	public String source;
	public String target;
	
	public Edge(String _label) {
		label = _label;
		source = _label.substring(0,  _label.length()-1);
		target = _label.substring(1, _label.length());
	}
}
