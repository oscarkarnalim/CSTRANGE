package cstrange.evaluation.additional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.apache.commons.text.similarity.LongestCommonSubsequence;

import cstrange.MainFrame;
import support.AdditionalKeywordsManager;

public class AdditionalEvalSOCO {
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

		
		compare(assessmentRooPath, goldResult, additionalKeywords);
	}

	private static void compare(String assessmentPath, ArrayList<String> goldResult,
			ArrayList<ArrayList<String>> additionalKeywords) {
		int minMatchLength = 10;
		String progLang = "java";

		ArrayList<ComparisonTuple> eligibleAvgPairTuples = new ArrayList<ComparisonTuple>();

		// generate the comparison tuple here
		long before = System.currentTimeMillis();
		File[] submissions = new File(assessmentPath).listFiles();
		for (int i = 0; i < submissions.length - 1; i++) {
			System.out.println(submissions[i].getName());
			
			for (int j = i + 1; j < submissions.length; j++) {
				String name1 = submissions[i].getName();
				String name2 = submissions[j].getName();
				double sim = 0;

				if (name1.contains("DS_Store") || name2.contains("DS_Store"))
					continue;
				

				try {
					// LCS
					String s1 = new String(
							Files.readAllBytes(Paths.get(submissions[i].listFiles()[0].getAbsolutePath())));
					String s2 = new String(
							Files.readAllBytes(Paths.get(submissions[j].listFiles()[0].getAbsolutePath())));

					LongestCommonSubsequence t = new LongestCommonSubsequence();
					sim = Double.valueOf(t.apply(s1, s2)) / Math.min(s1.length(), s2.length()) * 100;
					System.gc();

				} catch (Exception e) {
					e.printStackTrace();
				}

				eligibleAvgPairTuples.add(new ComparisonTuple(name1, name2, sim));
			}

		}
		long time = System.currentTimeMillis() - before;
		
		// sort the tuples
		Collections.sort(eligibleAvgPairTuples);
		
		for(ComparisonTuple t: eligibleAvgPairTuples) {
			System.out.println(t.simDegree);
		}

		int k = 10;

		while (k <= 100) {
			// calculate the effectiveness
			int copiedAndSuggested = 0;
			int suggested = k;
			for (int i = 0; i < k; i++) {
				ComparisonTuple r = eligibleAvgPairTuples.get(i);
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
		
		System.out.println(time);
	}
}