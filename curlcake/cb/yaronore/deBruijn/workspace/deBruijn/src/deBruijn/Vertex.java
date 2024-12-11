package deBruijn;

import java.util.Vector;

public class Vertex {

	public String label;
	public Vector<Edge> edges;
	boolean finished;
	
	public Vertex(int i, int k, int multi, int alphabetSize) {
		label = helperFunctions.getLabel(i, k, alphabetSize);
		edges = new Vector<Edge>();
		for (int j = 0; j < alphabetSize; j++)
			for (int l = 0; l < multi; l++)
				edges.add(new Edge(label + j));
		finished = false;
	}
	
	public boolean hasEdges() {
		return edges.size() > 0;
	}
	
	public boolean removeEdge(Edge e) {
		return edges.remove(e);
	}
}
