package edu.lavinia.inspectory.inspection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import edu.lavinia.inspectory.beans.FileDecapsulations;
import edu.lavinia.inspectory.op.beans.MethodsInFileAffectedByOwnershipProblems;
import edu.lavinia.inspectory.op.inspection.MethodOwnershipInspection;

public class MetricsCorrelation {
	final MethodOwnershipInspection methodsOwnershipProblemsInspection;

	private final ArrayList<String> nameOfFilesWithDecapsulationProblems = new ArrayList<>();

	public MetricsCorrelation(
			final MethodOwnershipInspection methodsOwnershipProblemsInspection) {
		this.methodsOwnershipProblemsInspection = methodsOwnershipProblemsInspection;
	}

	public JsonObject getJsonObjectFromFile() {
		final String currentDirectory = System.getProperty("user.dir");

		// FIXME change the file name to correspond with the repository
		// analyzed.
		final String path = currentDirectory + "/tomcat_decapsulations.json";

		final JsonParser parser = new JsonParser();
		Object object = null;
		try {
			object = parser.parse(new FileReader(path));
		} catch (final JsonIOException e) {
			e.printStackTrace();
		} catch (final JsonSyntaxException e) {
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		final JsonObject jobject = (JsonObject) object;

		// final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// final String json = gson.toJson(jobject);
		// System.out.println("\n\n: Json Result: " + json);

		return jobject;
	}

	public void getFilesWithDecapsulationProblemsNames() {
		final JsonObject jObject = getJsonObjectFromFile();

		final JsonArray jArray = jObject.getAsJsonArray("result");

		final Gson gson = new Gson();
		final FileDecapsulations fileDecapsulations[] = gson.fromJson(jArray,
				FileDecapsulations[].class);

		for (int i = 0; i < fileDecapsulations.length; ++i) {
			if (fileDecapsulations[i].getValue() == 0) {
				break;
			} else {
				nameOfFilesWithDecapsulationProblems
						.add(fileDecapsulations[i].getFile());
			}
		}
	}

	public void countCorrelationsOfFiles() {
		Integer numberOfFilesInBothMetrics = 0;

		for (final HashMap.Entry<String, MethodsInFileAffectedByOwnershipProblems> entry : methodsOwnershipProblemsInspection
				.getFilesAffectedAndTheirSeverity().entrySet()) {
			if (nameOfFilesWithDecapsulationProblems.contains(entry.getKey())) {
				++numberOfFilesInBothMetrics;
			}
		}

		System.out.println("\nIn the repository there were:");
		System.out.println("\t- " + nameOfFilesWithDecapsulationProblems.size()
				+ " files with Decapsulation Problems");
		System.out.println("\t- "
				+ methodsOwnershipProblemsInspection
						.getFilesAffectedAndTheirSeverity().size()
				+ " files with Ownership Problems");
		System.out.println("\t- " + numberOfFilesInBothMetrics
				+ " files with BOTH problems");
	}
}
