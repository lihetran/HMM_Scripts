package deBruijn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

public class deBruijn {

	public static void main(String argv[]) throws IOException {
		
		int alphabetSize = 4;

		// Parse input
		if (argv.length < 7 || argv.length > 8) {
		System.out.println("USAGE: <k> <len> <multiplicity> <attempts> <out_incompelte_file> <out_compelte_file> <RNAshapes_executable> <seed>");
		return;
		}
		int k = Integer.parseInt(argv[0]);
		int len = Integer.parseInt(argv[1]);
		int multi = Integer.parseInt(argv[2]);
		int attempts = Integer.parseInt(argv[3]);
		String out_incompelte_file = argv[4];
		String out_complete_file = argv[5];
		String RNAshapes_command = argv[6];
		long seed = argv.length == 8 ? Long.parseLong(argv[7]) : 0;

		// Find probes
		Graph g = new Graph(k-1, alphabetSize);
		g.generateGraph(multi);
		RNAshapes rnashapes = new RNAshapes(RNAshapes_command);
		String[] eulers = g.findUnstructuredEuler(len, rnashapes, attempts, seed);
		System.out.println("Euler: " + eulers[0] + " " + eulers.length + " " + 
				helperFunctions.check(eulers, k, multi, alphabetSize, true));

		// Write probes
		PrintWriter out = new PrintWriter(out_incompelte_file);
		for (int i = 0; i < eulers.length; i++)
			out.write(helperFunctions.turnToNuc(eulers[i]) + "\n");
		out.close();

		// Complete probes
		Random r = new Random(seed);
		Vector<String> finalset = new Vector<String>();
		for (int i = 0; i < eulers.length; i++)
			if (eulers[i].length() == len) finalset.add(eulers[i]);
			else { Vector<String> tmp = RNAshapes.complete(eulers[i], len, rnashapes, alphabetSize, attempts, attempts, r, k);
			for (int j = 0; j < tmp.size(); j++) finalset.add(tmp.get(j)); 
			}
		System.out.println("Final set has " + finalset.size() + " probes " + 
			helperFunctions.check(finalset.toArray(new String[0]), k, multi, alphabetSize, false));
		
		// Write complete probes to file
		out = new PrintWriter(out_complete_file);
		for (int i = 0; i < finalset.size(); i++) {
			out.write(helperFunctions.turnToNuc(finalset.get(i))+"\n");
		}
		out.close();
	}
}
