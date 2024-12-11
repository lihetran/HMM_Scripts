package deBruijn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

public class RNAshapes {

	public String command;
	
	// Constructor gets RNAshapes executable
	public RNAshapes (String _command) {
		command = _command;
	}

	// Run RNAshapes with two linekrs, pick the minimum one
	public double runRNAshapesCompete(String seq) throws IOException {
		String seqn = helperFunctions.turnToNuc(seq);
		RNAshapes r = new RNAshapes (command);
		return Math.min(r.runRNAshapes("AGG"+seqn), r.runRNAshapes("AGA"+seqn));
	}
	
	// Complete the string to an unstructured len-long sequence
	public static Vector<String> complete(String seq, int len, RNAshapes rna, int alphabetSize, int attempts, int kmerattempts, Random r, int k) throws IOException {
		String probe = "";
		Vector<String> rc = new Vector<String>();
		int count = 0;
		System.out.println("Complete: " + seq + " " + rna.runRNAshapesCompete(seq) + " " + len + " " + seq.length());
		
		// Attempt to randomly extend the sequence to an complete unstructured sequence
		do{
		probe = "";
		int index = r.nextInt(len - seq.length()+1); 
		for (int i = 0; i < len - seq.length() + 1; i++) {
			if (i == index) probe += seq;
			else probe += r.nextInt(alphabetSize);
		}
		if (++count % 100 == 0) System.out.println("Completing (" + count + ") " + probe + " " + probe.length() + " " + rna.runRNAshapesCompete(probe));
		} while (rna.runRNAshapesCompete(probe) > 0.5 && (count < attempts || (seq.length() == k && count < kmerattempts)));
		
		System.out.println("Done: " + probe + " " + rna.runRNAshapesCompete(probe));
		// If sequnce is still structured, continute recursively
		if ((rna.runRNAshapesCompete(probe) > 0.5 || probe.length() < len) && seq.length() > k) {
			int addition1 = (k % 2 == 0) ? k/2 : (k-1)/2;
			int addition2 = (k % 2 == 0) ? k/2-1 : (k-1)/2;
			String seq1 = seq.substring(0, seq.length()/2 + addition1);
			String seq2 = seq.substring(seq.length()/2-addition2);
			System.out.println("Recursing " + seq1 + " " + seq1.length() +
					" " + seq2 + " " + seq2.length());
			Vector<String> sol1 = complete(seq1, len, rna, alphabetSize, attempts, kmerattempts, r, k);
			Vector<String> sol2 = complete(seq2, len, rna, alphabetSize, attempts, kmerattempts, r, k);
			for (int i = 0; i < sol1.size(); i++) rc.add(sol1.get(i));
			for (int i = 0; i < sol2.size(); i++) rc.add(sol2.get(i));
		}
		else
		// If it is structured or is of k-length, return it
			rc.add(probe);
		return rc;
	}
	
	// Run RNAshapes with parameters and parse the output
	public double runRNAshapes(String seq) throws IOException {
		
		String s = command + " -s -c 70.0 -r -M 30 -t 1 -o 2";
		Process p = Runtime.getRuntime().exec(s);
	
		BufferedWriter stdOutput = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		stdOutput.write(seq);stdOutput.close();
		// read the output from the command
		stdInput.readLine();
		double sum = 0;
		while ((s = stdInput.readLine()) != null) {
			String[] words = s.split(" ");
			double energy = Double.parseDouble(words[0]);
			int start = s.indexOf('(')+1; int end = s.indexOf(')');
			double prob = Double.parseDouble(s.substring(start, end));
			if (energy < -2.5) sum += prob;
		}
	return sum;	

	}
}
