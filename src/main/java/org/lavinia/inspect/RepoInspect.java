package org.lavinia.inspect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.lavinia.utils.CSVUtils;
import org.metanalysis.core.project.PersistentProject;

public class RepoInspect {

	public final static Logger logger = Logger.getLogger(RepoInspect.class.getName());

	public static PersistentProject getProject() {
		try {
			return PersistentProject.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		/*
		 * FileModelInspect fileModelInspect = new
		 * FileModelInspect(getProject());
		 * fileModelInspect.getModelFunctionsAnalyze();
		 */
		if (args.length != 1) {
			System.out.println("Usage: java -jar inspectory-0.0.1-SNAPSHOT.jar <cvs_file_name>");
			System.exit(1);
		}
		String csvFileName = args[0];
		if (!csvFileName.endsWith(".csv")) {
			csvFileName += ".csv";
		}
		// FileHistoryInspect fileHistoryInspect = new
		// FileHistoryInspect(getProject());
		// fileHistoryInspect.getHistoryFunctionsAnalyze();
		FileWriter writer;
		try {
			writer = new FileWriter(csvFileName);
			CSVUtils.writeLine(writer, Arrays.asList("File", "Class", "Method", "Initial size", "Actual size",
					"Number of changes", "isPulsar", "isSupernova"));
			FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(getProject(), writer);
			fileHistoryInspect.getHistoryFunctionsAnalyze();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
