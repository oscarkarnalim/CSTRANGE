package cstrange.evaluation.additional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.text.similarity.LongestCommonSubsequence;

import cstrange.MainFrame;
import cstrange.STRANGEPairGenerator;
import cstrange.evaluation.JplagEntryTuple;
import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class AdditionalEvalIRPlag {
	public static void main(String[] args) {
		// top-K precision where K is the number of copied programs
		
		String assessmentRooPath = "C:\\Users\\oscar\\Desktop\\[formatted for JPlag] IR-Plag-Dataset";

		// get additional keywords for STRANGE
		ArrayList<ArrayList<String>> additionalKeywords = AdditionalKeywordsManager
				.readAdditionalKeywords(MainFrame.javaAdditionalKeywords);
		
		// start the process
		long before = System.currentTimeMillis();
		File[] cases = new File(assessmentRooPath).listFiles();
		for (int i = 0; i < cases.length; i++) {
			File[] levels = cases[i].listFiles();
			for (int j = 0; j < levels.length; j++) {
				compare(levels[j].getAbsolutePath(), cases[i].getName(), levels[j].getName(), additionalKeywords);
			}
		}
		long time = System.currentTimeMillis() - before;
		System.out.println(time);

	}

	private static void compare(String assessmentPath, String taskName, String level,
			ArrayList<ArrayList<String>> additionalKeywords) {
		int minMatchLength = 10;
		String progLang = "java";

		ArrayList<ComparisonTuple> eligibleAvgPairTuples = new ArrayList<ComparisonTuple>();

		// generate the comparison tuple here
		File[] submissions = new File(assessmentPath).listFiles();
		for (int i = 0; i < submissions.length - 1; i++) {

			for (int j = i + 1; j < submissions.length; j++) {
				String name1 = submissions[i].getName();
				String name2 = submissions[j].getName();
				double sim = 0;

				if (name1.contains("DS_Store") || name2.contains("DS_Store"))
					continue;

				try {
					// Char level
					// String s1 = new String(
					//		Files.readAllBytes(Paths.get(submissions[i].listFiles()[0].getAbsolutePath())));
					// String s2 = new String(
					//		Files.readAllBytes(Paths.get(submissions[j].listFiles()[0].getAbsolutePath())));
					
					// LCS
					// LongestCommonSubsequence t = new LongestCommonSubsequence();
					// sim = Double.valueOf(t.apply(s1, s2)) / Math.min(s1.length(), s2.length()) * 100;
					
					// String alignment
					// sim = AdditionalStringAlignment.calculateSimDegree(s1, s2) * 100;
					
					// RKRGST
					 // sim = AdditionalRKRGST.calcSimDegree(s1, s2) * 100;
					
					
					// token level
					File code1 = Comparer.getCode(submissions[i], progLang);
					File code2 = Comparer.getCode(submissions[j], progLang);
					
					// generate token string
					ArrayList<FeedbackToken> tokenString1 = STRANGEPairGenerator.getTokenString(code1.getAbsolutePath(),
							progLang);
					ArrayList<FeedbackToken> tokenString2 = STRANGEPairGenerator.getTokenString(code2.getAbsolutePath(),
							progLang);
					
					// sim = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2, minMatchLength)*100;
					
					if (progLang.equals("java")) {
						tokenString1 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1,
								additionalKeywords, true);
						tokenString2 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2,
								additionalKeywords, true);
					} else if (progLang.equals("py")) {
						PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1, additionalKeywords);
						PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2, additionalKeywords);
					}
					sim = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2, minMatchLength);
					
					System.gc();

				} catch (Exception e) {
					e.printStackTrace();
				}

				eligibleAvgPairTuples.add(new ComparisonTuple(name1, name2, sim));
			}

		}

		// sort the tuples
		Collections.sort(eligibleAvgPairTuples);

//		for (ComparisonTuple t : eligibleAvgPairTuples) {
//			System.out.println(t.simDegree);
//		}

		
		// calculate the number of total copied
		int totalCopied = 0;
		for (File s : submissions) {
			try {
				Integer.parseInt(s.getName());
				totalCopied++;
			} catch (Exception e) {

			}
		}

		// calculate the effectiveness
		int copiedAndSuggested = 0;
		int suggested = totalCopied;
		for (int i = 0; i < suggested; i++) {
			ComparisonTuple r = eligibleAvgPairTuples.get(i);
			String line = r.path1 + " " + r.path2;

			if (line.contains("orig") && line.contains("np") == false)
				copiedAndSuggested++;

		}
		if (suggested == 0)
			suggested = 1;

		double precision = copiedAndSuggested * 1.0 / suggested;
		
		System.out.println(taskName + "\t" + level + "\t" + precision);
	}
}
