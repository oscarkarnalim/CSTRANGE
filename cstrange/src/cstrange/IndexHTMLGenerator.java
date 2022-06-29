package cstrange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class IndexHTMLGenerator {

	public static void generateHtml(String assignmentRootPath, ArrayList<ExpandedComparisonPairTuple> codePairs,
			String coreTemplateHTMLPath, String outputDirPath, double threshold, String comparisonMode,
			String languageCode) throws Exception {

		String tableContent = getTableContent(codePairs, languageCode, comparisonMode);

		String logoHeader = "<div class=\"embedimage\" style=\"margin-bottom:5px;\" >\r\n"
				+ "					<img class=\"embedimage\" src=\"strange_html_layout_additional_files/logo.png\" alt=\"logo STRANGE\">\r\n"
				+ "					<div class=\"embedx\">X</div>\r\n"
				+ "					<img class=\"embedimage\" src=\"logojplag.png\" alt=\"logo JPlag\">\r\n"
				+ "				</div>";
		
		if(comparisonMode.equals("syntactic") || comparisonMode.equals("surface")) {
			logoHeader = "<div class=\"embedimage\" style=\"width:15%;margin-left:0px;\">\r\n"
					+ "					<img class=\"embedimage\" src=\"strange_html_layout_additional_files/logo.png\" alt=\"logo STRANGE\">\r\n"
					+ "				</div>";
		}

		// create directory
		File dirRoot = new File(outputDirPath);
		if (dirRoot.exists() == false)
			dirRoot.mkdir();

		// generate the html page
		File templateFile = new File(coreTemplateHTMLPath);
		File outputFile = new File(outputDirPath + File.separator + "index.html");
		BufferedReader fr = new BufferedReader(new FileReader(templateFile));
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile));
		String line;
		while ((line = fr.readLine()) != null) {

			if (line.contains("@filepath")) {
				line = line.replace("@filepath", assignmentRootPath);
			}

			if (line.contains("@logo")) {
				line = line.replace("@logo", logoHeader);
			}
			
			if (line.contains("@about")) {
				line = line.replace("@about", (languageCode.equals("en")) ? getAboutEn(): getAboutID());
			}

			if (line.contains("@threshold")) {
				line = line.replace("@threshold", String.format("%.0f", threshold));
			}

			if (line.contains("@tablecontent")) {
				line = line.replace("@tablecontent", tableContent);
			}

			if (line.contains("@explanation"))
				line = line.replace("@explanation",
						(languageCode.equals("en")) ? getExplanationContentEn() : getExplanationContentID());

			fw.write(line);
			fw.write(System.lineSeparator());
		}
		fr.close();
		fw.close();
	}

	private static String getExplanationContentEn() {
		String s = "";

		// explaining three types of similarities
		s += "<b>Pseudo-semantic similarity</b> partly considers the 'meaning' of the program statements by generalising "
				+ "statements with similar meaning (type III code clone). For example, 'for' loop is considered "
				+ "similar to 'while' loop.<br/> <br />";
		s += "<b>Syntactic similarity</b> considers all syntax tokens of the programs by generalising tokens with "
				+ "similar purpose (type II code clone). For example, all variations in variable names are "
				+ "ignored.<br/> <br />";
		s += "<b>Surface similarity</b> considers superficial look of the programs excluding comments and "
				+ "white space (type I code clone). <br/> <br />";
		s += "<b>Total of the similarities</b> is simply the sum of all similarities. It is used to "
				+ "sort the program pairs so that they can be easily distinguished one another. "
				+ "Strongly-suspected program pairs are those that result in high similarities in all types. <br/> <br />";

		// explaining how the similarity should be interpreted
		s += "The characteristics of reported similarity (average similarity):<ul>";
		s += "<li>It considers all differences in its calculation.</li>";
		s += "<li>It only leads to 100% similarity if both submissions are similar, even if some fragments have been swapped.</li>";
		s += "<li>It is best used when all differences are equally important.</li>";
		s += "<li>It is calculated as <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b> where <b>M</b> is the number of matches, ";
		s += "<b>A</b> is the number of tokens on the left code, and <b>B</b> is the number of tokens on the right code.</li>";
		s = s + "</ul>\n";

		return s;
	}

	private static String getExplanationContentID() {
		String s = "";

		// explaining three types of similarities
		s += "<b>Pseudo-semantic similarity</b> memperhitungkan 'makna' dari statemen program dengan cara mengeneralisir "
				+ "statemen-statemen yang bermakna sama (code clone tipe III). Sebagai contoh, pengulangan 'for' dianggap "
				+ "sama dengan pengulangan 'while'.<br/> <br />";
		s += "<b>Syntactic similarity</b> memperhitungkan semua token sintaks program dengan cara menggeneralisir token-token "
				+ "dengan tujuan serupa (code clone tipe II). Sebagai contoh, semua variasi pada nama variabel "
				+ "diabaikan.<br/> <br />";
		s += "<b>Surface similarity</b> memperhitungkan tampilan kulit program kecuali komentar dan white space "
				+ "(code clone tipe I). <br/> <br />";
		s += "<b>Total nilai kesamaan</b> adalah hasil penjumlahan dari semua nilai kesamaan. Ini digunakan untuk "
				+ "mengurutkan pasangan program agar mereka dapat dengan mudah dibedakan satu sama lain. "
				+ "Pasangan program yang sangat mencurigakan adalah mereka yang menghasilkan kesamaan tinggi di semua tipe. <br/> <br />";

		// explaining how the similarity should be interpreted
		s += "Karakteristik dari kesamaan yang dilaporkan (rerata kesamaan):<ul>";
		s += "<li>Memperhitungkan semua aspek perbedaan dalam prosesnya.</li>";
		s += "<li>Hanya menghasilkan kesamaan 100% jika konten kedua submisi serupa, meskipun sebagian fragmennya bertukar posisi.</li>";
		s += "<li>Cocok digunakan jika semua perbedaan kode dianggap penting.</li>";
		s += "<li>Dihitung dengan persamaan: <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b>; <b>M</b> adalah jumlah token sama, ";
		s += "<b>A</b> adalah jumlah token pada kode kiri, dan <b>B</b> adalah jumlah token pada kode kanan.</li>";
		s = s + "</ul>\n";

		return s;
	}
	
	public static String getAboutEn() {
		return "<ol>\r\n"
				+ "				<li>This HTML page is generated by CSTRANGE, a comprehensive tool to observe similarities among submissions with three levels of similarity: pseudo-semantic, syntactic, and surface.</li>\r\n"
				+ "<br />				<li>CSTRANGE is an expanded version of <b>STRANGE</b> (<b>S</b>imilarity <b>TR</b>acker in <b>A</b>cademia with <b>N</b>atural lan<b>G</b>uage <b>E</b>xplanation), which details can be seen in <b><u><a href = \"https://github.com/oscarkarnalim/strange\">the Github page</a></u></b> or <b><u><a href=\"https://ieeexplore.ieee.org/document/9405994\">the corresponding publication</a></u></b></li>\r\n"
				+ "<br />				<li> CSTRANGE can be downloaded from <b><u><a href=\"https://github.com/oscarkarnalim/cstrange\">the repository</a></u></b>.\r\n"
				+ "					Alternatively, you can email a request to <b>Oscar Karnalim</b> (<b><u><a href=\"mailto:oscar.karnalim@uon.edu.au\">this email</a></u></b> or <b><u><a href=\"mailto:oscar.karnalim@it.maranatha.edu\">that email</a></u></b>).</li>\r\n"
				+ "<br />				<li>If you want to cite the program (or some parts of it), please cite it as <b><u><a href=\"#\">this paper</a></u></b>. </li>\r\n"
				+ "				</ol>";
	}
	
	public static String getAboutID() {
		return "<ol>\r\n"
				+ "				<li>Laman HTML ini dibuat dengan CSTRANGE, sebuah kakas ramah pengguna untuk mengobservasi kesamaan antar tugas dengan tiga level kesamaan: pseudo-semantic, syntactic, dan surface.</li>\r\n"
				+ "<br />				<li>CSTRANGE merupakan ekspansi dari <b>STRANGE</b> (<b>S</b>imilarity <b>TR</b>acker in <b>A</b>cademia with <b>N</b>atural lan<b>G</b>uage <b>E</b>xplanation), which details can be seen in <b><u><a href = \"https://github.com/oscarkarnalim/strange\">the Github page</a></u></b> or <b><u><a href=\"https://ieeexplore.ieee.org/document/9405994\">the corresponding publication</a></u></b></li>\r\n"
				+ "<br />				<li> CSTRANGE dapat diunduh dari <b><u><a href=\"#\">repositori ini</a></u></b>.\r\n"
				+ "					Selain itu, kamu dapat mengirimkan email permintaan ke <b>Oscar Karnalim</b> (<b><u><a href=\"mailto:oscar.karnalim@uon.edu.au\">email ini</a></u></b> atau <b><u><a href=\"mailto:oscar.karnalim@it.maranatha.edu\">email itu</a></u></b>).</li>\r\n"
				+ "<br />				<li>Jika kamu ingin mensitasi program ini (atau bagian-bagiannya), mohon sitasi <b><u><a href=\"#\">paper ini</a></u></b>. </li>\r\n"
				+ "				</ol>";
	}

	private static String getTableContent(ArrayList<ExpandedComparisonPairTuple> codePairs, String languageCode,
			String comparisonMode) {
		String textForObserve = "observe";
		if (languageCode.equals("id"))
			textForObserve = "amati";

		String s = "";
		int numID = 0;
		for (ExpandedComparisonPairTuple ct : codePairs) {
			// generate the ID
			String entryID = "r" + numID;

			// generate the string
			s += "<tr id=\"" + entryID + "\" onclick=\"selectRow('" + entryID + "','sumtablecontent')\">";
			s += ("\n\t<td><a>" + ct.getAssignmentName1() + "-" + ct.getAssignmentName2() + "</a></td>");
			double[] simResults = ct.getSimResults();
			// semantic
			if (simResults[0] == -1) {
				s += ("\n\t<td>NA</td>");
				simResults[0] = 0;
			}else
				s += ("\n\t<td>" + String.format("%.0f", simResults[0]) + " %</td>");
			// syntactic
			if (simResults[1] == -1) {
				s += ("\n\t<td>NA</td>");
				simResults[1] = 0;
			}else
				s += ("\n\t<td>" + String.format("%.0f", simResults[1]) + " %</td>");
			// surface
			if (simResults[2] == -1) {
				s += ("\n\t<td>NA</td>");
				simResults[2] = 0;
			}else
				s += ("\n\t<td>" + String.format("%.0f", simResults[2]) + " %</td>");
			// total
			double total = simResults[0] + simResults[1] + simResults[2];
			s += ("\n\t<td>" + String.format("%.0f", total) + " %</td>");

			// link to main observation page
			String observationFileName = "";
			if (comparisonMode.equals("semantic"))
				observationFileName = ct.getHtmlPaths()[0];
			else if (comparisonMode.equals("syntactic"))
				observationFileName = ct.getHtmlPaths()[1];
			else if (comparisonMode.equals("surface"))
				observationFileName = ct.getHtmlPaths()[2];

			s += ("\n\t<td><button onclick=\"window.open('" + observationFileName + "', '_self');\">" + textForObserve
					+ "</button></td>");
			s += "\n</tr>\n";

			// increment number for ID
			numID++;
		}
		return s;
	}
}
