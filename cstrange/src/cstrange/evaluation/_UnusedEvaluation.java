package cstrange.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class _UnusedEvaluation {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// directory path containing all courses
		String allCourseDirPath = "C:\\Users\\oscar\\Desktop\\data";

		try {
			FileWriter myWriter = new FileWriter("result.txt");
			// write the header
			myWriter.write(
					"course name\tassessment path\tJPlag sim\tSyntactic sim\tSurface sim" + System.lineSeparator());

			File[] courses = new File(allCourseDirPath).listFiles();
			for (File course : courses) {

				// skip jplag results
				if (course.getName().startsWith("_"))
					continue;

				if (course.isDirectory()) {
					// get the file extension
					String fileExtension = course.getName().split("]")[0].substring(1);
					if (fileExtension.equalsIgnoreCase("java"))
						fileExtension = "java";
					else if (fileExtension.equalsIgnoreCase("python"))
						fileExtension = "py";
					else // data sets with uncovered programming languages
						continue;

					String jplagResultPath = course.getParentFile().getAbsolutePath() + "\\_jplag results\\_jplag "
							+ course.getName();
					String syntacticResultPath = course.getParentFile().getAbsolutePath()
							+ "\\_syntactic results\\_syntactic " + course.getName();
					String surfaceResultPath = course.getParentFile().getAbsolutePath()
							+ "\\_surface results\\_surface " + course.getName();

					// per assessment
					File[] assessments = course.listFiles();
					for (File assessment : assessments) {

						if (assessment.isDirectory()) {

							// do the process
							execute(assessment.getAbsolutePath(), course.getAbsolutePath(), jplagResultPath,
									syntacticResultPath, surfaceResultPath, myWriter);
						}
					}
				}
			}

			myWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void execute(String assessmentPath, String coursePath, String jplagPath, String syntacticPath,
			String surfacePath, FileWriter fw) {
		try {
			File assessmentDir = new File(assessmentPath);

			if (assessmentDir.getName().startsWith("[merged]"))
				return;

			if (assessmentPath.equals(
					"C:\\Users\\oscar\\Desktop\\data\\[Python][1 class][pairprog] Introductory Programming class B Odd Semester 2018-2019\\T04E")
					|| assessmentPath.equals(
							"C:\\Users\\oscar\\Desktop\\data\\[Python][1 class][pairprog] Introductory Programming class C Odd Semester 2018-2019\\T04E")) {
				return;
			}

			String jplagFile = jplagPath + "\\" + assessmentDir.getName() + ".txt";
			// if jplag file is not found, skip the process as the data is new and not
			// included in ICALT data set
			if (new File(jplagFile).exists() == false) {
				return;
			}

			String syntacticFile = syntacticPath + "\\" + assessmentDir.getName() + ".txt";
			String surfaceFile = surfacePath + "\\" + assessmentDir.getName() + ".txt";

			fw.write(coursePath.substring(coursePath.lastIndexOf("\\") + 1) + "\t" + assessmentDir.getName() + "\t"
					+ getRepeatedSimilarity(jplagFile, true) + "\t" + getRepeatedSimilarity(syntacticFile, false) + "\t"
					+ getRepeatedSimilarity(surfaceFile, false) + System.lineSeparator());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static double getRepeatedSimilarity(String simFilePath, boolean isJplag) throws Exception {
		// count how many repetitive values
		// For non-jplag, each value is times to 100

		

		ArrayList<Integer> values = new ArrayList<Integer>();

		Scanner sc = new Scanner(new File(simFilePath));
		while (sc.hasNextDouble()) {
			double t = sc.nextDouble();
			if (isJplag == false)
				t = t * 100;
			values.add((int)t);
		}
		sc.close();

		// count how many repetitive numbers
		int repetitivecounter = 0;
		// count total numbers which is no less than 50%
		int counter = 0;
		for (int i = 0; i < values.size(); i++) {
			// if the sim degree is higher or equal to 50%
			if (values.get(i) >= 50) {
				// add the counter
				counter++;
				
				// search if there is any similar numbers
				for (int j = i + 1; j < values.size(); j++) {
					if (values.get(i) == values.get(j)) {
						repetitivecounter++;
						break;
					}
				}
			}
		}

		return repetitivecounter*1.0/counter;
	}
}
