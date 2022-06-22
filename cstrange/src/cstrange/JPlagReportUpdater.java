package cstrange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import support.AdditionalKeywordsManager;

public class JPlagReportUpdater {

	public static void update(String jplagDirPath, String assignmentPath, String progLang, String humanLang,
			double simThreshold, int maxPairs, int minMatchLength, String templateDirPath,
			String assignmentParentDirPath, String assignmentName, boolean isMultipleFiles, boolean isCommonCodeAllowed,
			String additionalKeywordsPath, ArrayList<File> filesToBeDeleted, boolean isSyntactic, boolean isSurface) {
		// embed STRANGE's observation layout on each JPlag's observation page

		// copy the path for STRANGE process
		String strangeExlcusiveAssignmentPath = assignmentPath;

		// additional keyword list, starting empty
		ArrayList<ArrayList<String>> additionalKeywords = new ArrayList<ArrayList<String>>();

		if (isSurface || isSyntactic) {
			// get additional keywords for STRANGE
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(additionalKeywordsPath);

			// if multiple files, merge them
			if (isMultipleFiles) {
				strangeExlcusiveAssignmentPath = assignmentParentDirPath + File.separator + "[merged] "
						+ assignmentName;
				CodeMerger.mergeCode(assignmentPath, progLang, strangeExlcusiveAssignmentPath);
				// mark new assignment path to be deleted after the whole process
				filesToBeDeleted.add(new File(strangeExlcusiveAssignmentPath));
			}

			// remove common and template code for java and python if needed
			strangeExlcusiveAssignmentPath = JavaPyCommonTemplateRemover.removeCommonAndTemplateCodeJavaPython(
					strangeExlcusiveAssignmentPath, minMatchLength, templateDirPath, isCommonCodeAllowed, progLang,
					assignmentParentDirPath, assignmentName, filesToBeDeleted, additionalKeywordsPath);
		}

		String filepath = jplagDirPath + File.separator + "index.html";
		try {
			// start parsing with jsoup
			Document comparisonHTMLDoc = Jsoup.parse(new File(filepath), "UTF-8");
			// take all table tags as they are the containers of sim degree
			// results
			Elements tables = comparisonHTMLDoc.select("TABLE");

			// get average similarity pairs
			Element tableAvg = tables.get(2);

			// to store all comparison pairs
			ArrayList<ExpandedComparisonPairTuple> codePairs = new ArrayList<ExpandedComparisonPairTuple>();

			// rename jplag logo from logo.png to logojplag.png
			File logojplag = new File(jplagDirPath + File.separator + "logo.png");
			logojplag.renameTo(new File(jplagDirPath + File.separator + "logojplag.png"));

			// Jplag tag for pair observation page
			String jplagTag = "					<div class=\"embedx\">X</div>"
					+ "					<img class=\"embedimage\" src=\"logojplag.png\" alt=\"logo JPlag\">\r\n";

			// ==================================================================
			// Listing suspected results
			// ==================================================================

			// to temporarily store suspected results
			ArrayList<TempJPlagTupleHolder> tempSuspectedResults = new ArrayList<TempJPlagTupleHolder>();

			// start extracting all rows
			Elements rows = tableAvg.select("tr");
			// iterate per row
			for (int i = 0; i < rows.size(); i++) {
				Element row = rows.get(i);
				// take all the column
				Elements col = row.select("td");
				// first column is the first dir name
				String dirname1 = col.get(0).text();
				// get the first code
				File code1 = Comparer.getCode(new File(strangeExlcusiveAssignmentPath + File.separator + dirname1),
						progLang);

				// start from the third column are the second filename and target
				// html for replacement
				for (int j = 2; j < col.size(); j++) {
					// get the second dir name
					String dirname2 = col.get(j).text();
					dirname2 = dirname2.substring(0, dirname2.lastIndexOf("(") - 1);

					// get the second code
					File code2 = Comparer.getCode(new File(strangeExlcusiveAssignmentPath + File.separator + dirname2),
							progLang);

					// similarity
					String semanticSimDegreeS = col.get(j).text();
					semanticSimDegreeS = semanticSimDegreeS.substring(semanticSimDegreeS.lastIndexOf("(") + 1,
							semanticSimDegreeS.length() - 2);
					int semanticSimDegree = (int) (Double.parseDouble(semanticSimDegreeS));

					// generate the reports if higher than the threshold
					if (semanticSimDegree >= simThreshold) {
						tempSuspectedResults.add(new TempJPlagTupleHolder(dirname1, dirname2, code1, code2,
								semanticSimDegree, col.get(j)));
					}
				}

				// sort the temporary result
				Collections.sort(tempSuspectedResults);
			}

//			for(TempJPlagTupleHolder t : tempSuspectedResults) {
//				System.out.println(t.code1.getAbsolutePath() + " " + t.code2.getAbsolutePath()  + " " + t.semanticSimDegree);
//			}

			// ==================================================================
			// Generate reports
			// ==================================================================
			int reportSize = Math.min(maxPairs, tempSuspectedResults.size());
			for (int i = 0; i < reportSize; i++) {
				TempJPlagTupleHolder t = tempSuspectedResults.get(i);
				String dirname1 = t.dirname1;
				String dirname2 = t.dirname2;
				File code1 = t.code1;
				File code2 = t.code2;
				int semanticSimDegree = t.semanticSimDegree;
				Element colj = t.colj;

				// calculating for syntactic and surface if needed
				int syntacticSimDegree = -1;
				int surfaceSimDegree = -1;
				if (isSurface || isSyntactic) {
					// generate token string
					ArrayList<FeedbackToken> tokenString1 = STRANGEPairGenerator.getTokenString(code1.getAbsolutePath(),
							progLang);
					ArrayList<FeedbackToken> tokenString2 = STRANGEPairGenerator.getTokenString(code2.getAbsolutePath(),
							progLang);

					// calculate surface sim
					if (isSurface) {
						surfaceSimDegree = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
								minMatchLength);
					}

					// calculate syntactic sim
					if (isSyntactic) {
						// if syntactic sim is needed, calculate
						if (progLang.equals("java")) {
							tokenString1 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1,
									additionalKeywords, true);
							tokenString2 = JavaFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2,
									additionalKeywords, true);
						} else if (progLang.equals("py")) {
							PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString1, additionalKeywords);
							PythonFeedbackGenerator.syntaxTokenStringPreprocessing(tokenString2, additionalKeywords);
						}
						syntacticSimDegree = STRANGEPairGenerator.getSTRANGESim(tokenString1, tokenString2,
								minMatchLength);
					}
				}

				// get the link
				String jplagMainFileName = colj.select("a").get(0).attr("href");
				String targetFilepath = jplagDirPath + File.separator + jplagMainFileName;
				Document descHTMLDoc = Jsoup.parse(new File(targetFilepath), "UTF-8");

				// embed CSS
				descHTMLDoc.head()
						.append("  <script src=\"strange_html_layout_additional_files/run_prettify.js\"></script>  "
								+ "  <style>\r\n" + "	div.embeddiv{\r\n" + "		float:left;\r\n"
								+ "		width:100%;\r\n" + "		margin-bottom:5px;\r\n" + "	}\r\n"
								+ "	div.embedimage{\r\n" + "		float:left;\r\n" + "		margin-right:10px;\r\n"
								+ "	}\r\n" + "	div.embedx{\r\n" + "		float:left;\r\n"
								+ "		font-size:24px;\r\n" + "		margin-top:10px;\r\n"
								+ "		margin-left:5px;\r\n" + "		margin-right:5px;\r\n" + "	}\r\n"
								+ "	img.embedimage{\r\n" + "		float:left;\r\n" + "	}\r\n" + "	a.button {\r\n"
								+ "		border:none;\r\n" + "		outline: none;\r\n" + "		float: left;\r\n"
								+ "		cursor: pointer;\r\n" + "		padding: 10px 20px;\r\n"
								+ "		padding-bottom:0px;\r\n" + "		height:30px;\r\n"
								+ "		transition: 0.3s;\r\n" + "		background-color: rgba(0,140,186,1);\r\n"
								+ "		color: white;\r\n" + "		margin-top:5px;\r\n"
								+ "		text-decoration: none;\r\n" + "	}\r\n" + "	a.button:hover {\r\n"
								+ "	  background-color: rgba(20,160,206,1);\r\n" + "	}\r\n"
								+ "	a.button.active {\r\n" + "	  background-color: rgba(40,180,226,1);\r\n"
								+ "	}\r\n" + "\r\n" + "	hr{\r\n" + "		margin-bottom:5px;\r\n" + "	}  \r\n"
								+ "	div.codeview{\r\n" + "		float:left;\r\n" + "		width: 49%;\r\n"
								+ "		height:61%;\r\n" + "		margin-right: 1%;\r\n" + "	}\r\n" + "	pre{\r\n"
								+ "		tab-size: 2;\r\n" + "		overflow: auto;\r\n" + "	}\r\n"
								+ "	.linenums li {\r\n" + "		list-style-type: decimal;\r\n" + "	}"
								+ "	div.jplagnotice{\r\n" + "		float:left;\r\n" + "		margin-top:10px;\r\n"
								+ "		margin-bottom:10px;\r\n" + "		width:100%;\r\n" + "	}" + " </style>");
				// generate additional HTML
				String addedHTML = "<div class=\"embeddiv\"> \r\n" + "	<div class=\"embedimage\">\r\n"
						+ "		<img class=\"embedimage\" style=\"height:50px\" src=\"logojplag.png\" alt=\"logo JPlag\">\r\n"
						+ "		<div class=\"embedx\">X</div>\r\n"
						+ "		<img class=\"embedimage\" style=\"height:50px\" src=\"strange_html_layout_additional_files/logo.png\" alt=\"logo STRANGE\">\r\n"
						+ "	</div>\r\n" +

						"	<a class=\"button active\" href=\"" + jplagMainFileName + "\" target=\"_top\">\r\n"
						+ "		Pseudo-semantic sim (" + semanticSimDegree + "%)\r\n" + "	</a>\r\n";

				// prepare html tags for STRANGE
				String embeddedSimTagsForSyntactic = "<a href=\"" + jplagMainFileName + "\" class=\"embedbutton\">\r\n"
						+ "					Pseudo-semantic sim (" + semanticSimDegree + "%)\r\n"
						+ "				</a>\r\n";
				String embeddedSimTagsForSurface = embeddedSimTagsForSyntactic;

				// html filepath for STRANGE
				String syntacticFileName = jplagMainFileName.substring(0, jplagMainFileName.length() - 5)
						+ ".syntax.html";
				String surfaceFileName = jplagMainFileName.substring(0, jplagMainFileName.length() - 5)
						+ ".surface.html";

				if (isSyntactic) {
					// if syntactic, embed link to syntactic sim
					addedHTML = addedHTML + "	<a class=\"button\" href=\"" + syntacticFileName
							+ "\" target=\"_top\">Syntactic sim (" + syntacticSimDegree + "%)</a>\r\n";

					embeddedSimTagsForSyntactic = embeddedSimTagsForSyntactic + "				<a href=\""
							+ syntacticFileName + "\" class=\"embedbutton embedactive\">\r\n"
							+ "					Syntactic sim (" + syntacticSimDegree + "%) \r\n"
							+ "				</a>\r\n";

					embeddedSimTagsForSurface = embeddedSimTagsForSurface + "				<a href=\""
							+ syntacticFileName + "\" class=\"embedbutton\">\r\n" + "					Syntactic sim ("
							+ syntacticSimDegree + "%) \r\n" + "				</a>\r\n";
				}

				if (isSurface) {
					// if surface, embed link to surface sim
					addedHTML = addedHTML + "	<a class=\"button\" href=\"" + surfaceFileName
							+ "\" target=\"_top\">Surface sim (" + surfaceSimDegree + "%)</a>\r\n";

					embeddedSimTagsForSyntactic = embeddedSimTagsForSyntactic + "				<a href=\""
							+ surfaceFileName + "\" class=\"embedbutton\">\r\n" + "					Surface sim ("
							+ surfaceSimDegree + "%) \r\n" + "				</a>";

					embeddedSimTagsForSurface = embeddedSimTagsForSurface + "				<a href=\""
							+ surfaceFileName + "\" class=\"embedbutton embedactive\">\r\n"
							+ "					Surface sim (" + surfaceSimDegree + "%) \r\n" + "				</a>";
				}

				// add back button and closing the div tag
				addedHTML = addedHTML + "	<a class=\"button\" href=\"index.html\" target=\"_top\">\r\n"
						+ "		Back \r\n" + "	</a>\r\n" + "   <div class=\"jplagnotice\">\r\n"
						+ "   The report is generated and beautified from JPlag 3.0.0 at which all links somehow do not work. Hence, these links are removed for convenience.\r\n"
						+ "   </div>" + "</div><hr />";
				// embed HTML
				descHTMLDoc.selectFirst("BODY").child(0).before(addedHTML);
				
				
				// further preprocessing to beautify JPlag results
				// preprocessing pre tags
				Elements els = descHTMLDoc.select("pre");
				for(Element el: els) {
					// add prettyprint and linenums as additional classes of pre
					el.addClass("prettyprint");
					el.addClass("linenums");
					// remove all <a href> tags from pre
					Elements ahrefs = el.select("a");
					for(Element ahref: ahrefs) {
						ahref.remove();
					}
				}
				
				// add codeview class for two frames displaying code. Brute force though.
				String html = descHTMLDoc.html().replaceAll("<div style=\"flex-grow: 1;\">", "<div style=\"flex-grow: 1;\" class=\"codeview\">");
				
				// replace all font tag with span. Brute force again
				html = html.replaceAll("<font color=\"", "<span style=\"background-color:");
				html = html.replaceAll("</font>","</span>");
				
				// deactivate all <a href> tags from the table. Brute force again
				html = html.replaceAll("<a href=\"javascript:ZweiFrames", "<a nhref=\"javascript:ZweiFrames");

				// write the changes
				FileOutputStream fos = new FileOutputStream(targetFilepath, false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				osw.write(html);
				osw.close();

				// generate STRANGE observation pages
				// for syntactic
				if (isSyntactic) {
					if (progLang.equals("java")) {
						JavaHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(), code2.getAbsolutePath(),
								dirname1, dirname2, embeddedSimTagsForSyntactic, MainFrame.pairTemplatePath,
								jplagDirPath + File.separator + syntacticFileName, minMatchLength, humanLang, jplagTag,
								additionalKeywords, true);
					} else if (progLang.equals("py")) {
						PythonHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(), code2.getAbsolutePath(),
								dirname1, dirname2, embeddedSimTagsForSyntactic, MainFrame.pairTemplatePath,
								jplagDirPath + File.separator + syntacticFileName, minMatchLength, humanLang, jplagTag,
								additionalKeywords, true);
					}
				}

				// for surface
				if (isSurface) {
					if (progLang.equals("java")) {
						JavaHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(), code2.getAbsolutePath(),
								dirname1, dirname2, embeddedSimTagsForSurface, MainFrame.pairTemplatePath,
								jplagDirPath + File.separator + surfaceFileName, minMatchLength, humanLang, jplagTag,
								additionalKeywords, false);
					} else if (progLang.equals("py")) {
						PythonHtmlGenerator.generateHtmlForCSTRANGE(code1.getAbsolutePath(), code2.getAbsolutePath(),
								dirname1, dirname2, embeddedSimTagsForSurface, MainFrame.pairTemplatePath,
								jplagDirPath + File.separator + surfaceFileName, minMatchLength, humanLang, jplagTag,
								additionalKeywords, false);
					}
				}

				// add the pair to codePairs
				codePairs.add(new ExpandedComparisonPairTuple("", "", dirname1, dirname2,
						new double[] { semanticSimDegree, syntacticSimDegree, surfaceSimDegree },
						new String[] { jplagMainFileName, syntacticFileName, surfaceFileName }, semanticSimDegree));
			}
			// sort the comparison pairs based on sum of all similarity degrees
			Collections.sort(codePairs);

			// rewriting the index
			IndexHTMLGenerator.generateHtml(assignmentPath, codePairs, MainFrame.indexTemplatePath, jplagDirPath,
					simThreshold, "semantic", humanLang);
			// generate additional files
			STRANGEPairGenerator.generateAdditionalDir(jplagDirPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class TempJPlagTupleHolder implements Comparable<TempJPlagTupleHolder> {
	public String dirname1, dirname2;
	public File code1, code2;
	public int semanticSimDegree;
	public Element colj;

	public TempJPlagTupleHolder(String dirname1, String dirname2, File code1, File code2, int semanticSimDegree,
			Element colj) {
		super();
		this.dirname1 = dirname1;
		this.dirname2 = dirname2;
		this.code1 = code1;
		this.code2 = code2;
		this.semanticSimDegree = semanticSimDegree;
		this.colj = colj;
	}

	@Override
	public int compareTo(TempJPlagTupleHolder o) {
		// TODO Auto-generated method stub
		return (int) ((-semanticSimDegree + o.semanticSimDegree) * 100000);
	}

}