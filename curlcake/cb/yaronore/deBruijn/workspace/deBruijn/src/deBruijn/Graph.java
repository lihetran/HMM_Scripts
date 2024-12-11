package deBruijn;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

public class Graph {

	public int k;
	public int alphabetSize;
	public Vector<Vertex> vertices;
	public int numEdges;
	
	public Graph(int _k, int _alphabetSize) {
		k = _k; alphabetSize = _alphabetSize;
	}
	
	// Generate a new graph with vertices and edges * multi
	public void generateGraph(int multi) {
		vertices = new Vector<Vertex>();
		for (int i = 0; i < Math.pow(alphabetSize, k); i++)
			vertices.add(new Vertex(i, k, multi, alphabetSize));
		numEdges = (int)Math.pow(alphabetSize, k+1) * multi;
	}
	
	// Getters
	private Vertex getVertex(String label) {
		return vertices.get(helperFunctions.stringToInt(label, alphabetSize));
	}
	
	private Edge getEdge(String label) {
		Vertex v = getVertex(label.substring(0,label.length()-1));
		int i = 0;
		while (v.edges.get(i++).label.compareTo(label) != 0) {
		};
		return v.edges.get(i-1);
	}
	
	// The main algorithm
	public String[] findUnstructuredEuler(int len, RNAshapes r, int attempts, long seed) throws IOException {

		// Keep all unfinished vertices
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < vertices.size(); i++) list.add(vertices.get(i).label);
		
		String probe = "";
		int probes = 0; int weak = 0;
		Vector<String> probes_vec = new Vector<String>();
		
		Vertex curr = getVertex(list.get(0));
		Edge e;
		
		// Random
		Random rand = new Random(seed);
		
		// Continue while not all edges have been traversed
		while (numEdges > 0) {

			// Beginning of probe search
			if (probe.length() == 0) probe += curr.label;
			boolean end = false;
			double probe_structure = 0; int count = 0; String ext = "";

			// While probe is shorter than required length 
			// and not run too many attempts yet
			while (probe.length() < len && !end) {
				count = 0;
				int locallen = Math.min(Math.min(len, probe.length()*2), numEdges+probe.length());
				probe_structure = r.runRNAshapesCompete(probe);
				System.out.println("locallen = " + locallen + " " + probe + " " + probe_structure);
				ext = "";
				
				// While probe is still structured and not over number of attempts
				do {
				if (++count % 100 == 0) System.out.println(count + " " + r.runRNAshapesCompete(probe+ext) + 
						" " + probe_structure + " " + (locallen - probe.length() - (count / attempts)));
				ext = getRandomPath(rand, curr, locallen - probe.length() - (count / attempts), count % 100 == 0);
				} while (r.runRNAshapesCompete(probe+ext) > 0.5 && probe_structure < 0.5 && 
						count < attempts * (locallen - probe.length()));

				// If more than number of attempts, finish iterating
				if (count == attempts * (locallen - probe.length())) {end = true;
				System.out.println("Found non-weak extension " + probe);
				}
				if (!getVertex((probe+ext).substring((probe+ext).length()-k)).hasEdges()) {
					System.out.println("In end");
					end = true;
				}
				
				// Pick random extension if not good extension was found
				System.out.println(probe + " " + curr.label + " " + list.size() + " " + end);
				if (probe.length() == curr.label.length() && (list.size() == 1 || end))
					ext = getRandomPath(rand, curr, 1, true);

				System.out.println("Extension found = " + (probe+ext) + " " + r.runRNAshapesCompete(probe+ext));

				// Delete edges of selected extension
				for (int j = 0; j < ext.length(); j++) {
					e = getEdge(curr.label + ext.charAt(j));
					curr.removeEdge(e);
					if (!curr.hasEdges()) {			
						// Vertex has no edges to continue from
						curr.finished = true;
						list.remove(curr.label);
					}
					curr = getVertex(e.target);
					numEdges--;
				}
				probe += ext;
			}

			// Add probe to list of probe sequences
			if (probe.length() > curr.label.length()) {
			probes++; if(r.runRNAshapesCompete(probe) < 0.5) weak++;
			System.out.println("**** EXT: " + probes + " " + probe + " " + probe.length() + " " + r.runRNAshapesCompete(probe) +
					" " + (r.runRNAshapesCompete(probe) < 0.5) + " " + weak + " " + numEdges);
			probes_vec.add(probe);
			probe = "";
			}
			
			// Trying to double probe length
			// Check if vertex has any edges to traverse
			System.out.println("curr edges = " + curr.edges.size());

			if (!curr.hasEdges()) {
				// Vertex has no edges to continue from
				curr.finished = true;
				list.remove(curr.label);
				if (list.size() > 0) curr = getVertex(list.get(0));
			}
			if (probe.length() == curr.label.length()) {
				// Pick a new random vertex
				System.out.println("No probe found " + curr.label);
				curr = getVertex(list.get(rand.nextInt(list.size())));
				System.out.println("Picked a new vertex " + curr.label);
				probe = "";
			}

		}
		// Deal with the end of the run
		if (probe.length() > k) {
		probes++; if(r.runRNAshapesCompete(probe) < 0.5) weak++;
		System.out.println("**** END: " + probes + " " + probe + " " + r.runRNAshapesCompete(probe) 
				+ " " + (r.runRNAshapesCompete(probe) < 0.5) + " " + weak + " " + numEdges);
		probes_vec.add(probe);}
		
		return probes_vec.toArray(new String[0]); // was seq
	}

	// Get random path from a vertex given the required length
	private String getRandomPath(Random rand, Vertex curr, int len, boolean print) {
		String rc = "";
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		while (curr.hasEdges() && rc.length() < len && rc.length() < numEdges) {
			Edge e = null; int count = 0;
			do {
			int i = rand.nextInt(curr.edges.size());
			e = curr.edges.get(i);
			} while(map.containsKey(e.label) && ++count < len) /*&& (e.target.compareTo(e.source) != 0)*/;
			if (count >= len) break;
			map.put(e.label, 0);
			rc += e.label.substring(e.label.length()-1);
			curr = getVertex(e.target);
		}
		if (print) System.out.println("In random path " + rc);
		return rc;
	}

}
