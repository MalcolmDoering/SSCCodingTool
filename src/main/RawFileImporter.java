package main;

import java.io.File;

public class RawFileImporter {

	public static void main(String[] args) {
		Globals.initialize();
		
		int batchNumber = 3;
		
		//Globals.fileIO.loadCrossvalidatedResults("predictedresult_crossvalidated-" + batchNumber + ".csv");
		String resultsFile = "results-" + batchNumber + ".csv";
		if (new File("data" + File.separator + resultsFile).exists() == false) {
			Globals.fileIO.saveData(resultsFile);
			System.out.println("Successfully created " + resultsFile);
		} else {
			System.err.println("Error. File exists: " + resultsFile);
			System.err.println("Please manually delete the file if you are sure you want to overwrite it.");
		}
		Globals.shutdown();
	}

}
