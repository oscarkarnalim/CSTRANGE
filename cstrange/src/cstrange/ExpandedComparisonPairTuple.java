package cstrange;

import java.util.ArrayList;

import p3.feedbackgenerator.comparison.ComparisonPairTuple;
import support.stringmatching.GSTMatchTuple;

public class ExpandedComparisonPairTuple extends ComparisonPairTuple {

	private String[] htmlPaths; // to store html paths of sim reports
	private int comparableConstant; // for automated sorting
	private ArrayList<GSTMatchTuple> matches; // for matches

	public ExpandedComparisonPairTuple(String codePath1, String codePath2, String assignmentName1,
			String assignmentName2, double[] simResults, String[] htmlPaths, int comparableContent) {
		super(codePath1, codePath2, assignmentName1, assignmentName2, simResults);
		this.htmlPaths = htmlPaths;
		/*
		 * The score is gained by summing all similarities
		 */
		this.comparableConstant = (int) (simResults[0] + simResults[1] + simResults[2]);

		// set match tuples as null
		this.matches = null;
	}

	public String[] getHtmlPaths() {
		return htmlPaths;
	}

	public void setHtmlPaths(String[] htmlPaths) {
		this.htmlPaths = htmlPaths;
	}

	public ArrayList<GSTMatchTuple> getMatches() {
		return matches;
	}

	public void setMatches(ArrayList<GSTMatchTuple> matches) {
		this.matches = matches;
	}

	// for automated sorting, overidding this method at parent
	// previously it returns the average syntax,
	// now it returns total avg sim of three levels
	// pseudo semantic, syntactic, and surface
	public double getAvgSyntax() {
		return comparableConstant;
	}

}
