package cstrange.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

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

public class _E_Layer_SimDegree {
	public static void main(String[] args) throws Exception {
		int threshold = 50;

		while (threshold <= 100) {
			FileWriter fw = new FileWriter("result" + threshold + ".txt");
			String assessmentRootPath = "C:\\Users\\oscar\\Desktop\\data\\";
			String jplagResultRootPath = "C:\\Users\\oscar\\Desktop\\data\\_jplag results";

			File[] jplagResultPerCourse = new File(jplagResultRootPath).listFiles();
			for (int i = 0; i < jplagResultPerCourse.length; i++) {
				if (jplagResultPerCourse[i].isDirectory()) {
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
							extract(jplagResultPerAssessment[j] + "\\index.html", fw, threshold, assessmentRootPath,
									jplagResultPerCourse[i].getName());
						}
					}
				}
			}
			fw.close();

			// plus ten for the threshold
			threshold += 10;
		}
	}

	public static void extract(String jplagIndexPath, FileWriter fw, int minSimDegree, String assessmentRooPath,
			String courseName) {
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

		try {
			// start parsing with jsoup
			Document doc = Jsoup.parse(new File(jplagIndexPath), "UTF-8");
			// take all table tags as they are the containers of sim degree
			// results
			Elements tables = doc.select("TABLE");

			// table for average similarity
			Element tableAvg = tables.get(2);
			ArrayList<JplagEntryTuple> avgPairTuples = getSourceCodePairTuples(tableAvg);

			// to store eligible pairs (which sim is no less than the threshold
			ArrayList<JplagEntryTuple> eligibleAvgPairTuples = new ArrayList<JplagEntryTuple>();
			// get the eligible pairs
			for (int i = 0; i < avgPairTuples.size(); i++) {
				JplagEntryTuple s = avgPairTuples.get(i);
				if (s.simDegreeJplag >= minSimDegree) {
					eligibleAvgPairTuples.add(s);
				}
			}

			// for each eligible pair, calculate syntactic and surface similarities
			for (int i = 0; i < eligibleAvgPairTuples.size(); i++) {
				JplagEntryTuple cur = eligibleAvgPairTuples.get(i);
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
			}

			// count how many pairs with the same similarity degree
			int jplagRepetitiveSim = 0;
			int syntacticRepetitiveSim = 0;
			int surfaceRepetitiveSim = 0;
			for (int i = 0; i < eligibleAvgPairTuples.size(); i++) {
				JplagEntryTuple cur = eligibleAvgPairTuples.get(i);

				// check the next entries for jplag
				for (int j = i + 1; j < eligibleAvgPairTuples.size(); j++) {
					JplagEntryTuple next = eligibleAvgPairTuples.get(j);
					// for JPlag
					if (Math.abs(cur.simDegreeJplag - next.simDegreeJplag) < 0.1) {
						jplagRepetitiveSim++;

						// for syntactic
						if (Math.abs(cur.simDegreeSyntactic - next.simDegreeSyntactic) < 0.1) {
							syntacticRepetitiveSim++;

							// for surface
							if (Math.abs(cur.simDegreeSurface - next.simDegreeSurface) < 0.1) {
								surfaceRepetitiveSim++;
							}
						}
						break;
					}
				}
			}

			fw.write(courseName + "\t" + assessmentName + "\t" + jplagRepetitiveSim + "\t" + syntacticRepetitiveSim
					+ "\t" + surfaceRepetitiveSim + "\t" + eligibleAvgPairTuples.size() + System.lineSeparator());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<JplagEntryTuple> getSourceCodePairTuples(Element processedTable) {
		// prepare a variable to store all the results
		ArrayList<JplagEntryTuple> tuples = new ArrayList<>();

		// start extracting all rows
		Elements rows = processedTable.select("tr");
		// iterate per row
		for (int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			// take all the column
			Elements col = row.select("td");
			// first column is the first filename
			String filename1 = col.get(0).text();
			// start from the third column are the second filename and
			// similarity degree
			for (int j = 2; j < col.size(); j++) {
				String filename2 = col.get(j).text();
				filename2 = filename2.substring(0, filename2.lastIndexOf("(") - 1);

				String semanticSimDegreeS = col.get(j).text();
				semanticSimDegreeS = semanticSimDegreeS.substring(semanticSimDegreeS.lastIndexOf("(") + 1,
						semanticSimDegreeS.length() - 2);
				double simDegree = Double.parseDouble(semanticSimDegreeS);
				// put them in the tuples
				if (filename1.compareTo(filename2) < 0)
					tuples.add(new JplagEntryTuple(filename1, filename2, simDegree));
				else
					tuples.add(new JplagEntryTuple(filename2, filename1, simDegree));
			}
		}
		// sort the results
		Collections.sort(tuples);
		return tuples;
	}
}
