package cstrange;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.Report;
import de.jplag.strategy.ComparisonMode;

public class PseudoSemanticComparer {
	public static String doSemanticComparison(String assignmentPath, String progLang, String humanLang,
			int simThreshold, int maxPairs, int minMatchingLength, String templateDirPath,
			boolean isSyntacticSimReported, boolean isSurfaceSimReported, String assignmentParentDirPath,
			String assignmentName, boolean isMultipleFiles, boolean isCommonCodeAllowed, String additionalKeywordsPath,
			ArrayList<File> filesToBeDeleted) {

		// start JPlag comparison
		// it returns error message if any

		// prog language
		LanguageOption lang = null;
		if (progLang.equals("java"))
			lang = LanguageOption.JAVA;
		else if (progLang.equals("py"))
			lang = LanguageOption.PYTHON_3;
		else if (progLang.equals("C"))
			lang = LanguageOption.C_CPP;
		else if (progLang.equals("C++"))
			lang = LanguageOption.C_CPP;
		else if (progLang.equals("C#"))
			lang = LanguageOption.C_SHARP;
		else if (progLang.equals("Scheme"))
			lang = LanguageOption.SCHEME;
		else if (progLang.equals("Text"))
			lang = LanguageOption.TEXT;

		JPlagOptions options = new JPlagOptions(assignmentPath, lang);

		// sim threshold
		options.setSimilarityThreshold(simThreshold / 100);

		// minimum matching length
		options.setMinimumTokenMatch(minMatchingLength);

		// template code marking for programming languages other than Java and Python
		if (progLang.equals("java") == false && progLang.equals("py") == false) {
			try {
				File bcDir = new File(new File(assignmentPath).getAbsolutePath() + File.separator + "[bc]");
				// copy the basecode dir to the assignment dir
				FileManipulator.copyDirectory(new File(templateDirPath), bcDir);

				// add the JPlag argument
				options.setBaseCodeSubmissionName("[bc]");

				// mark as to be removed
				filesToBeDeleted.add(bcDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// just to ensure jplag checks for subdirectories
		// jplagArgs.add("-s");

		// set result dir based on the name of assignment
		String jPlagResultPath = assignmentParentDirPath + File.separator + "[out] " + assignmentName;

		// parallel for efficiency
		options.setComparisonMode(ComparisonMode.PARALLEL);
		// average sim
		options.setSimilarityMetric(SimilarityMetric.AVG);

		// remove all debugging text (somehow cannot use JPlag's features for that)
		PrintStream originalStream = System.out;
		PrintStream dummyStream = new PrintStream(new OutputStream(){
		    public void write(int b) {
		        // NO-OP
		    }
		});
		System.setOut(dummyStream);

		// run JPlag
		try {

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();

			// save the report
			Report report = new Report(new File(jPlagResultPath), options);
			report.writeResult(result);

		} catch (Exception e) {
			// error(s) occurs
			e.printStackTrace();
			return e.getMessage();
		}

		// set the console output to be enabled again
		System.setOut(originalStream);

		// update the JPlag's output
		JPlagReportUpdater.update(jPlagResultPath, assignmentPath, progLang, humanLang, simThreshold, maxPairs,
				minMatchingLength, templateDirPath, assignmentParentDirPath, assignmentName, isMultipleFiles,
				isCommonCodeAllowed, additionalKeywordsPath, filesToBeDeleted, isSyntacticSimReported,
				isSurfaceSimReported);

		// all good
		return null;
	}
}
