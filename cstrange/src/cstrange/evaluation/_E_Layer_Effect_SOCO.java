package cstrange.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cstrange.MainFrame;
import cstrange.STRANGEPairGenerator;
import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class _E_Layer_Effect_SOCO {
	public static void main(String[] args) throws Exception {
		String rootdirpath = "C:\\Users\\oscar\\Desktop\\soco_f";
		String assessmentRooPath = "C:\\Users\\oscar\\Desktop\\soco_f\\formatted";

		// read the gold standard data
		Scanner rf = new Scanner(new File("C:\\Users\\oscar\\Desktop\\soco_f\\soco_train_clones_fixed.txt"));
		ArrayList<String> goldResult = new ArrayList<String>();
		while (rf.hasNextLine()) {
			goldResult.add(rf.nextLine());
		}

		// get additional keywords for STRANGE
		ArrayList<ArrayList<String>> additionalKeywords = AdditionalKeywordsManager
				.readAdditionalKeywords(MainFrame.javaAdditionalKeywords);

		// get jplag dirs
		String jplagDirPath = rootdirpath + File.separator + "[out] 0 formatted" + File.separator + "index.html";
		int threshold = 0;
		extract(jplagDirPath, assessmentRooPath, threshold, goldResult, additionalKeywords);

	}

	private static void extract(String jplagIndexPath, String assessmentPath, int threshold,
			ArrayList<String> goldResult, ArrayList<ArrayList<String>> additionalKeywords) {
		int minMatchLength = 10;
		String progLang = "java";

		try {
			// start parsing with jsoup
			Document doc = Jsoup.parse(new File(jplagIndexPath), "UTF-8");
			// take all table tags as they are the containers of sim degree
			// results
			Elements tables = doc.select("TABLE");

			// table for average similarity
			Element tableAvg = tables.get(2);
			ArrayList<JplagEntryTuple> avgPairTuples = _E_Layer_SimDegree.getSourceCodePairTuples(tableAvg);

			// to store eligible pairs (which sim is no less than the threshold
			ArrayList<JplagEntryTuple> eligibleAvgPairTuples = new ArrayList<JplagEntryTuple>();
			// get the eligible pairs
			for (int k = 0; k < avgPairTuples.size(); k++) {
				JplagEntryTuple cur = avgPairTuples.get(k);
				if (cur.simDegreeJplag >= threshold) {
					String submissionPath1 = assessmentPath + "\\" + cur.path1;
					String submissionPath2 = assessmentPath + "\\" + cur.path2;

					try {
						// get the token strings
						ArrayList<FeedbackToken> tokenString1 = STRANGEPairGenerator.getTokenString(
								Comparer.getCode(new File(submissionPath1), progLang).getAbsolutePath(), progLang);
						ArrayList<FeedbackToken> tokenString2 = STRANGEPairGenerator.getTokenString(
								Comparer.getCode(new File(submissionPath2), progLang).getAbsolutePath(), progLang);

						// calculate surface sim
						cur.simDegreeSurface = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
								minMatchLength);

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
						cur.simDegreeSyntactic = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
								minMatchLength);
					} catch (Exception e) {
						System.out.println(submissionPath1);
						System.out.println(submissionPath2);
					}
					// add current to the eligible ones
					eligibleAvgPairTuples.add(cur);
				}
			}

			// sort the pairs
			Collections.sort(eligibleAvgPairTuples);

			int k = 10;

			while (k <= 100) {
				// calculate the effectiveness
				int copiedAndSuggested = 0;
				int suggested = k;
				for (int i = 0; i < k; i++) {
					JplagEntryTuple r = eligibleAvgPairTuples.get(i);
					if (goldResult.contains(r.path1 + " " + r.path2))
						copiedAndSuggested++;
				}

				double precision = copiedAndSuggested * 1.0 / suggested;
				double recall = copiedAndSuggested * 1.0 / goldResult.size();
				double fscore = (2.0 * precision * recall) / (precision + recall);
				if (precision == 0 && recall == 0)
					fscore = 0;

				System.out.println(k + "\t" + precision + "\t" + recall + "\t" + fscore);
				k += 10;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
