package cstrange.evaluation;

import java.io.File;
import java.util.ArrayList;

public class _E_Layer_JPlag_SOCO {
	public static void main(String[] args) throws Exception {
		long before = System.nanoTime();
		int simThreshold = 0;

		String dirpath = "C:\\Users\\oscar\\Desktop\\soco_f\\formatted";
		File assessmentDirFile = new File(dirpath);

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
		String jPlagResultPath = assessmentDirFile.getParentFile().getAbsolutePath() + File.separator + "[out] "
				+ simThreshold + " " + assessmentDirFile.getName();
		jplagArgs.add("-r");
		jplagArgs.add(jPlagResultPath);

		// just to ensure jplag checks for subdirectories
		jplagArgs.add("-s");
		// assignment path
		jplagArgs.add(dirpath);

		// duplicate to an array of string
		String[] jplagArgsArr = new String[jplagArgs.size()];
		for (int i = 0; i < jplagArgsArr.length; i++) {
			jplagArgsArr[i] = jplagArgs.get(i);
		}

		// run JPlag
		// JPlag.main(jplagArgsArr);

		System.out.println(System.nanoTime() - before);
	}
}
