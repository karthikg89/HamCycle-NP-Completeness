import java.io.*;
import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;

/**
	A Program to take a 3-SAT formula and encode it into a directed graph with
	the property that the graph has a hamiltonian path if and only if the formula
	is satisfiable.

	Outputs a .tex file.

	Usage "java Prog <filename.tex> < <input file>"
	OR	  "java Prog <filename.tex>" //Read from Standard in
*/

class Clause {
	String left, middle, right;

	public Clause (String left, String middle, String right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

}
class Prog {
	static HashSet<String> vars = new HashSet<String>();
	static ArrayList<Clause> clauses = new ArrayList<Clause>();

	public static void drawClauseEdge (int clause_num,
			String s, BufferedWriter output) throws IOException {
		String temp = s;
		int from = 3 * clause_num - 1;
		int to = from + 1;
		if (temp.charAt(0) == '-') {
			temp = s.substring(1, s.length());
			int tempnum = from;
			from = to;
			to = tempnum;
		}
		output.write("\\draw[edge] (" +
					temp + "_" + from +
					") to [bend left=15] (" +
					"C_" + clause_num +
					");\n");
		output.write("\\draw[edge] (" +
					"C_" + clause_num +					
					") to [bend left=15] (" +
					temp + "_" + to +
					");\n");
	}

	public static String addToVar (String s) {
		String temp = s;
		if (temp.charAt(0) == '-') {
			temp = s.substring(1, s.length());
		}
		vars.add(temp);

		return s;
	}

	public static void main (String[] args) {
		BufferedWriter output = null;
		Scanner s = null;
		try {
			File file = null;
			try {
				file = new File(args[0]);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Usage \"java Prog <filename.tex> < <input file>\"");
				System.exit(1);
			}
	        output = new BufferedWriter(new FileWriter(file));
			s = new Scanner (System.in);

			while (true) {
				String var1 = s.nextLine();
				if (var1.equalsIgnoreCase("done")) 
					break;
				addToVar(var1);
				String var2 = addToVar(s.nextLine());
				String var3 = addToVar(s.nextLine());

				clauses.add (new Clause (var1, var2, var3));
			}
			// Read all input
			//Writing beginning of doc
			output.write ("\\documentclass[11pt]{article}\n" +
				"\\usepackage{geometry,graphicx}\n" +
				"\\usepackage{tikz}\n" +
				"\\usetikzlibrary{arrows,calc}\n" +
				"\\geometry{\n" +
				  "lmargin = .2in,\n" +
				"rmargin = .2in,\n" +
			 	 "tmargin = .2in,\n" +
				  "bmargin = .2in\n" +
				"}\n" +
				"\\begin{document}\n" +
				"\\resizebox{.9\\textwidth}{.9\\textheight}{" +
				"" +
				"\\begin{tikzpicture}\n" +
				"\\tikzset{vertex/.style = {shape=circle,draw,minimum size=1.5em, fill=gray!20}}\n" + 
				"\\tikzset{edge/.style = {->,> = latex'}}\n" +
				"\\tikzset{clause/.style = {shape=rectangle,draw,minimum size=2em, fill=gray!20}}\n");


			ArrayList<String> varList = new ArrayList<String>();
			for (String str : vars) {
				varList.add(str);
			}

			for (int ind = 0; ind < varList.size(); ind++) {
				int y = ind * 4;
				String var = varList.get(ind);
				output.write("\\node[] (lab) at (-1.5," + y + ") {$" + var + "$};");
				output.write("\\node[vertex] (" + var + "_" + 0 + ") at (" + (3 * clauses.size()) +
							"," + (y + 2) + ") {};\n");
				output.write("\\node[vertex] (" + var + "_" + (3 * clauses.size() + 2) 
							+ ") at (" + (3 * clauses.size()) +
							"," + (y - 2) + ") {};\n");
				for (int i = 1; i <= (3 * clauses.size() + 1); i++) {
					output.write("\\node[vertex] (" + var + "_" + i + ") at (" + ((i-1) * 2) +
							"," + y + ") {};\n");
					if (i > 1) {
						output.write("\\draw[edge] (" + 
							var + "_" + (i - 1) +
							") to[bend left=15] (" +
							var + "_" + i +
							");\n");
						output.write("\\draw[edge] (" + 
							var + "_" + (i) +
							") to[bend left=15] (" +
							var + "_" + (i - 1) +
							");\n");
					}
				}
				output.write ("\\draw[edge] ("+
					var + "_" + 0 +
					") to (" +
					var + "_" + 1 +
					");\n");
				output.write ("\\draw[edge] ("+
					var + "_" + 0 +
					") to (" +
					var + "_" + (3 * clauses.size() + 1) +
					");\n");
				output.write ("\\draw[edge] ("+
					var + "_" + 1 +
					") to (" +
					var + "_" + (3 * clauses.size() + 2) +
					");\n");
				output.write ("\\draw[edge] ("+
					var + "_" + (3 * clauses.size() + 1) +
					") to (" +
					var + "_" + (3 * clauses.size() + 2) +
					");\n");
			}

			for (int i = 0; i < clauses.size(); i++) {				
				output.write("\\node[clause] (C_" + (i+1) + ") at (" 
					+ (i*2) +
					"," + (-3) + ") {$C_" + (i+1) + "$};\n");
			}

			for (int i = 0; i < clauses.size(); i++) {
				Clause clause = clauses.get(i);
				drawClauseEdge(i+1, clause.left, output);
				drawClauseEdge(i+1, clause.middle, output);
				drawClauseEdge(i+1, clause.right, output);
			}


			output.write ("\\end{tikzpicture}\n}\n\\end{document}");

			output.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}