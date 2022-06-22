package cstrange;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class SurfaceComparer {
	public static void doSurfaceComparison(String assignmentPath, String progLang, String humanLang, int simThreshold,
			int minMatchingLength, String templateDirPath, File assignmentFile, String assignmentParentDirPath,
			String assignmentName,boolean isMultipleFiles, boolean isCommonCodeAllowed,
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
				humanLang, additionalKeywords);

	}

	private static void generateSTRANGEHTMLReports(String dirPath, String resultPath, String progLang, String humanLang,
			int simThreshold, int minMatchLength, String languageCode,
			ArrayList<ArrayList<String>> additionalKeywords) {
		try {
			// create the output dir
			File resultDir = new File(resultPath);
			resultDir.mkdir();

			// to store the result
			ArrayList<ExpandedComparisonPairTuple> codePairs = new ArrayList<>();

			// start processing
			File[] assignments = (new File(dirPath)).listFiles();
			for (int i = 0; i < assignments.length; i++) {
				// for each code 1

				String dirname1 = assignments[i].getName();
				File code1 = Comparer.getCode(assignments[i], progLang);

				// skip if null
				if (code1 == null)
					continue;

				for (int j = i + 1; j < assignments.length; j++) {
					// pair code 1 with code 2

					String dirname2 = assignments[j].getName();
					File code2 = Comparer.getCode(assignments[j], progLang);

					// skip if null
					if (code2 == null)
						continue;

					int surfaceSimDegree = -1;
					// generate token string
					ArrayList<FeedbackToken> tokenString1 = STRANGEPairGenerator.getTokenString(code1.getAbsolutePath(),
							progLang);
					ArrayList<FeedbackToken> tokenString2 = STRANGEPairGenerator.getTokenString(code2.getAbsolutePath(),
							progLang);

					// calculate surface sim
					surfaceSimDegree = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2, minMatchLength);

					if (surfaceSimDegree >= simThreshold) {
						// html filepath for STRANGE
						String surfaceFileName = "obs" + i + "-" + j + ".surface.html";
						String embeddedSimTagsForSurface = "";

						// generate the embedded sim tags for surface
						embeddedSimTagsForSurface = embeddedSimTagsForSurface + "				<a href=\""
								+ surfaceFileName + "\" class=\"embedbutton embedactive\">\r\n"
								+ "					Surface sim (" + surfaceSimDegree + "%) \r\n" + "				</a>";

						// generate STRANGE observation pages
						// for surface
						if (progLang.equals("java")) {
							JavaHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(), code2.getAbsolutePath(),
									dirname1, dirname2, embeddedSimTagsForSurface, MainFrame.pairTemplatePath,
									resultPath + File.separator + surfaceFileName, minMatchLength, humanLang, "",
									additionalKeywords, false);
						} else if (progLang.equals("py")) {
							PythonHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(),
									code2.getAbsolutePath(), dirname1, dirname2, embeddedSimTagsForSurface,
									MainFrame.pairTemplatePath, resultPath + File.separator + surfaceFileName,
									minMatchLength, humanLang, "", additionalKeywords, false);
						}

						// add the comparison pair
						codePairs.add(new ExpandedComparisonPairTuple("", "", dirname1, dirname2,
								new double[] { -1, -1, surfaceSimDegree },
								new String[] { "", "", surfaceFileName }, surfaceSimDegree));
					}
				}
			}
			// sort in descending order based on average syntax
			Collections.sort(codePairs);

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
