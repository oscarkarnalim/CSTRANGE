package cstrange;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class SurfaceComparer {
	public static void doSurfaceComparison(String assignmentPath, String progLang, String humanLang, int simThreshold,
			int minMatchingLength, int maxPairs, String templateDirPath, File assignmentFile,
			String assignmentParentDirPath, String assignmentName, boolean isMultipleFiles, boolean isCommonCodeAllowed,
			String additionalKeywordsPath, ArrayList<File> filesToBeDeleted) {

		// if multiple files, merge them
		if (isMultipleFiles) {
			String newAssignmentPath = assignmentParentDirPath + File.separator + "[merged] " + assignmentName;
			CodeMerger.mergeCode(assignmentPath, progLang, newAssignmentPath);
			assignmentPath = newAssignmentPath;
			// mark new assignment path to be deleted after the whole process
			filesToBeDeleted.add(new File(newAssignmentPath));
		}

		// remove common and template code for java and python if needed
		assignmentPath = JavaPyCommonTemplateRemover.removeCommonAndTemplateCodeJavaPython(assignmentPath,
				minMatchingLength, templateDirPath, isCommonCodeAllowed, progLang, assignmentParentDirPath,
				assignmentName, filesToBeDeleted, additionalKeywordsPath);

		// get additional keywords for STRANGE
		ArrayList<ArrayList<String>> additionalKeywords = new ArrayList<ArrayList<String>>();
		if (progLang.equals("java"))
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(MainFrame.javaAdditionalKeywords);
		else if (progLang.equals("py"))
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(MainFrame.pyAdditionalKeywords);

		// set result dir based on the name of assignment
		String resultPath = assignmentParentDirPath + File.separator + "[out] " + assignmentName;

		// generate STRANGE reports
		generateSTRANGEHTMLReports(assignmentPath, resultPath, progLang, humanLang, simThreshold, minMatchingLength,
				maxPairs, humanLang, additionalKeywords);

	}

	private static void generateSTRANGEHTMLReports(String dirPath, String resultPath, String progLang, String humanLang,
			int simThreshold, int minMatchLength, int maxPairs, String languageCode,
			ArrayList<ArrayList<String>> additionalKeywords) {
		try {
			// create the output dir
			File resultDir = new File(resultPath);
			resultDir.mkdir();

			// to store the result
			ArrayList<ExpandedComparisonPairTuple> codePairs = new ArrayList<>();

			// start processing
			File[] assignments = (new File(dirPath)).listFiles();

			ArrayList<ArrayList<FeedbackToken>> tokenStringsSurface = new ArrayList<ArrayList<FeedbackToken>>();

			for (int i = 0; i < assignments.length; i++) {
				File code = Comparer.getCode(assignments[i], progLang);
				// add empty lists if null
				if (code == null) {
					tokenStringsSurface.add(new ArrayList<FeedbackToken>());
				} else {
					// generate token string
					ArrayList<FeedbackToken> tokenString = STRANGEPairGenerator.getTokenString(code.getAbsolutePath(),
							progLang);
					// add
					tokenStringsSurface.add(tokenString);
				}
			}

			for (int i = 0; i < assignments.length; i++) {
				// for each code 1

				String dirname1 = assignments[i].getName();
				File code1 = Comparer.getCode(assignments[i], progLang);

				for (int j = i + 1; j < assignments.length; j++) {
					// pair code 1 with code 2

					String dirname2 = assignments[j].getName();
					File code2 = Comparer.getCode(assignments[j], progLang);

					// calculating for surface
					int surfaceSimDegree = STRANGEPairGenerator.getSTRANGESim(tokenStringsSurface.get(i),
							tokenStringsSurface.get(j), minMatchLength);

					if (surfaceSimDegree >= simThreshold) {
						// add the comparison pair
						codePairs.add(new ExpandedComparisonPairTuple(code1.getAbsolutePath(), code2.getAbsolutePath(),
								dirname1, dirname2, new double[] { -1, -1, surfaceSimDegree}, new String[] {},
								surfaceSimDegree));
					}
				}
			}
			// sort in descending order based on average syntax
			Collections.sort(codePairs);

			// remove extra pairs
			while (codePairs.size() > maxPairs) {
				codePairs.remove(codePairs.size() - 1);
			}

			// generate the similarity report
			for (int i = 0; i < codePairs.size(); i++) {
				// get the comparison pair tuple
				ExpandedComparisonPairTuple ecpt = codePairs.get(i);

				// html filepath for STRANGE
				String surfaceFileName = "obs" + i + ".surface.html";
				String embeddedSimTagsForSurface = "";

				embeddedSimTagsForSurface = embeddedSimTagsForSurface + "				<a href=\"" + surfaceFileName
						+ "\" class=\"embedbutton embedactive\">\r\n" + "					Surface sim ("
						+ ecpt.getSimResults()[2] + "%) \r\n" + "				</a>";

				if (progLang.equals("java")) {
					JavaHtmlGenerator.generateHtmlForCSTRANGE(ecpt.getCodePath1(), ecpt.getCodePath2(),
							ecpt.getAssignmentName1(), ecpt.getAssignmentName2(), embeddedSimTagsForSurface,
							MainFrame.pairTemplatePath, resultPath + File.separator + surfaceFileName, minMatchLength,
							humanLang, "", additionalKeywords, false);
				} else if (progLang.equals("py")) {
					PythonHtmlGenerator.generateHtmlForCSTRANGE(ecpt.getCodePath1(), ecpt.getCodePath2(),
							ecpt.getAssignmentName1(), ecpt.getAssignmentName2(), embeddedSimTagsForSurface,
							MainFrame.pairTemplatePath, resultPath + File.separator + surfaceFileName, minMatchLength,
							humanLang, "", additionalKeywords, false);
				}

				// set html paths
				ecpt.setHtmlPaths(new String[] { "", "", surfaceFileName });
			}

			// generate the index HTML
			IndexHTMLGenerator.generateHtml(dirPath, codePairs, MainFrame.indexTemplatePath, resultPath, simThreshold,
					"surface", humanLang);
			// generate additional files
			STRANGEPairGenerator.generateAdditionalDir(resultPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
