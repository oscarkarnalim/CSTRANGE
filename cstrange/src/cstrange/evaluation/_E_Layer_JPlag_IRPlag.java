package cstrange.evaluation;

import java.io.File;
import java.util.ArrayList;

public class _E_Layer_JPlag_IRPlag {
	public static void main(String[] args) {
		String dirpath = "C:\\Users\\oscar\\Desktop\\IR-Plag\\[formatted for JPlag] IR-Plag-Dataset";

		long before = System.currentTimeMillis();
		// generate the jplag result
		File[] tasks = new File(dirpath).listFiles();
		for (File task : tasks) {
			File[] levels = task.listFiles();
			for (File level : levels) {
				System.out.println(level.getAbsolutePath());
				execute1(level.getAbsolutePath(), dirpath, task.getName());
			}
		}
		System.out.println(System.currentTimeMillis() - before);
	}

	public static void execute1(String currentAssessmentDirPath, String rootDirPath, String targetName) {
		int simThreshold = 0;

		File assessmentDirFile = new File(currentAssessmentDirPath);

		// prepare the arguments
		ArrayList<String> jplagArgs = new ArrayList<String>();

		// prog language
		jplagArgs.add("-l");
		jplagArgs.add("java19");

		// sim threshold
		jplagArgs.add("-m");
		jplagArgs.add(simThreshold + "%");

		// minimum matching length
		jplagArgs.add("-t");
		jplagArgs.add("10");

		// set result dir based on the name of assignment
		String jPlagResultPath = new File(rootDirPath).getParentFile().getAbsolutePath() + File.separator + "[out] "
				+ simThreshold + " " + targetName + " " + assessmentDirFile.getName();
		jplagArgs.add("-r");
		jplagArgs.add(jPlagResultPath);

		// just to ensure jplag checks for subdirectories
		jplagArgs.add("-s");
		// assignment path
		jplagArgs.add(currentAssessmentDirPath);

		// duplicate to an array of string
		String[] jplagArgsArr = new String[jplagArgs.size()];
		for (int i = 0; i < jplagArgsArr.length; i++) {
			jplagArgsArr[i] = jplagArgs.get(i);
		}

		// run JPlag
		// JPlag.main(jplagArgsArr);
	}
}
