package cstrange.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import cstrange.MainFrame;
import cstrange.STRANGEPairGenerator;
import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class _E_Optimised_SimDegree {
	public static void main(String[] args) throws Exception {
		long timebefore = System.nanoTime();
		FileWriter fw = new FileWriter("resultSyntacticOptimised.txt");
		// Main.isRKRGSTOptimised = false;
		String assessmentRootPath = "C:\\Users\\oscar\\Desktop\\data\\";
		String jplagResultRootPath = "C:\\Users\\oscar\\Desktop\\data\\_jplag results";

		File[] jplagResultPerCourse = new File(jplagResultRootPath).listFiles();
		for (int i = 0; i < jplagResultPerCourse.length; i++) {
			if (jplagResultPerCourse[i].isDirectory() && jplagResultPerCourse[i].getName().contains("[uon]") == false) {
				// only do the process if it is a directory

				// do further process per assessment
				File[] jplagResultPerAssessment = jplagResultPerCourse[i].listFiles();
				for (int j = 0; j < jplagResultPerAssessment.length; j++) {
					// only do the process if it is a directory
					if (jplagResultPerAssessment[j].isDirectory()) {

						if (jplagResultPerAssessment[j].getAbsolutePath().equals(
								"C:\\Users\\oscar\\Desktop\\data\\_jplag results\\_jplag [Python][1 class][pairprog] Introductory Programming class B Odd Semester 2018-2019\\T04E")
								|| jplagResultPerAssessment[j].getAbsolutePath().equals(
										"C:\\Users\\oscar\\Desktop\\data\\_jplag results\\_jplag [Python][1 class][pairprog] Introductory Programming class C Odd Semester 2018-2019\\T04E"))
							continue;

						System.out.println(jplagResultPerAssessment[j]);
						extract(jplagResultPerAssessment[j] + "\\index.html", fw, assessmentRootPath,
								jplagResultPerCourse[i].getName());
					}
				}
			}
		}
		fw.close();

		System.out.println("Processing time\t" + (System.nanoTime() - timebefore));
	}

	public static void extract(String jplagIndexPath, FileWriter fw, String assessmentRooPath, String courseName)
			throws Exception {
		// extract similarity results from jplag-output directory

		// generate the assessment path
		String assessmentPath = assessmentRooPath
				+ jplagIndexPath.substring(jplagIndexPath.indexOf("_jplag results\\_jplag ") + 22);
		assessmentPath = assessmentPath.substring(0, assessmentPath.length() - 11);
		String assessmentName = new File(assessmentPath).getName();
		// set the merged assessment path
		String mergedAssessmentPath = assessmentPath.substring(0, assessmentPath.lastIndexOf("\\") + 1) + "[merged] "
				+ assessmentPath.substring(assessmentPath.lastIndexOf("\\") + 1);
		// check if the merged assessment path has a file on there
		if (new File(mergedAssessmentPath).exists())
			assessmentPath = mergedAssessmentPath;

		// get the programming language and minimum matching length
		String progLang = "py";
		int minMatchLength = 5;
		if (assessmentPath.contains("[Java]")) {
			// for Java
			progLang = "java";
			minMatchLength = 10;
		}

		// get additional keywords for STRANGE
		ArrayList<ArrayList<String>> additionalKeywords = new ArrayList<ArrayList<String>>();
		if (progLang.equals("java"))
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(MainFrame.javaAdditionalKeywords);
		else if (progLang.equals("py"))
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(MainFrame.pyAdditionalKeywords);

		double totalSimForAssessment = 0;
		int totalPairs = 0;

		// do the comparison for all submissions in the assessment
		File[] submissions = new File(assessmentPath).listFiles();
		for (int i = 0; i < submissions.length; i++) {
			for (int j = i + 1; j < submissions.length; j++) {
				// do the comparison

				try { // use try to deal with empty folders
						// get the token strings
					ArrayList<FeedbackToken> tokenString1 = STRANGEPairGenerator
							.getTokenString(Comparer.getCode(submissions[i], progLang).getAbsolutePath(), progLang);
					ArrayList<FeedbackToken> tokenString2 = STRANGEPairGenerator
							.getTokenString(Comparer.getCode(submissions[j], progLang).getAbsolutePath(), progLang);

					// calculate surface sim
//					totalSimForAssessment += STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
//							minMatchLength);

					// generalise tokens
					if (progLang.equals("java")) {
						tokenString1 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1,
								additionalKeywords, true);
						tokenString2 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2,
								additionalKeywords, true);
					} else if (progLang.equals("py")) {
						PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1, additionalKeywords);
						PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2, additionalKeywords);
					}

					// calculate syntactic sim
					totalSimForAssessment += STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
							minMatchLength);

					// increment the total pair counter
					totalPairs++;
				} catch (Exception e) {

				}
			}
		}

		fw.write(courseName + "\t" + assessmentName + "\t" + (totalSimForAssessment / totalPairs)
				+ System.lineSeparator());

	}
}
