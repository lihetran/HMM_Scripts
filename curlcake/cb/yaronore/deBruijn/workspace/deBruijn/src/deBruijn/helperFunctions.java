package deBruijn;

public class helperFunctions {

	// Get the string representing i
	public static String getString(long i, int k, int alphabetSize) {
		String rc = "";
		for (int j = 0; j < k; j++) {
			rc += (i % alphabetSize);
			i = i / alphabetSize;
		}
		return rc;
	}
	
	// Get the label representing i
	public static String getLabel(int i, int k, int alphabetSize) {
		String rc = "";
		for (int j = 0; j < k; j++) {
			rc += (i % alphabetSize);
			i = i / alphabetSize;
		}
		return rc;
	}
	
	// Get the integer represented by label
	public static int stringToInt(String label, int alphabetSize) {
		int rc = 0;
		for (int i = 0; i < label.length(); i++)
			rc += charToInt(label.charAt(i)) * Math.pow(alphabetSize, i);
		return rc;
	}
	
	// Check that in the set of sequences each k-mer occurs exactly/at least multi times
	public static boolean check(String[] seqs, int k, int multi, int alphabetSize, boolean exact) {
		int[] check = new int[(int)Math.pow(alphabetSize, k)];
		for(int j = 0; j < seqs.length; j++) {
			String seq = seqs[j];
			for (int i = 0; i < seq.length()-k+1; i++) {
				check[stringToInt(seq.substring(i, k+i), alphabetSize)]++;
			}
		}
		// Exactly or at least depends on parameter exact
		boolean rc = true;
		for (int i = 0; i < check.length; i++)
			if (check[i] != multi && exact || (check[i] < multi && !exact)) {System.out.println(getString(i, k, alphabetSize) + " " + i + " " + check[i]); 
				rc = false;	}
		return rc;
	}
	
	public static int charToInt(char c) {
		return Integer.parseInt(""+c);
	}
	
	public static char intToChar(int c) {
		return (""+c).charAt(0);
	}
	
	public static String turnToNuc(String a) {
		String rc = "";
		for (int i = 0;i < a.length(); i++)
			rc += turnToNuc(a.charAt(i));
		return rc;
	}
	
	public static char turnToNuc(char i) {
		switch(i) {
		case '0': return 'A';
		case '1': return 'C';
		case '2': return 'G';
		case '3': return 'U';
		default: return 'N';
		}
	}
}
