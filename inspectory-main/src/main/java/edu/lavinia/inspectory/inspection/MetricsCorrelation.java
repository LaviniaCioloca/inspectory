package edu.lavinia.inspectory.inspection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import edu.lavinia.inspectory.am.beans.FileWithAstronomicalMethods;
import edu.lavinia.inspectory.am.inspection.AstronomicalMethodsInspection;
import edu.lavinia.inspectory.beans.FileDecapsulations;
import edu.lavinia.inspectory.op.beans.MethodsInFileAffectedByOwnershipProblems;
import edu.lavinia.inspectory.op.inspection.MethodOwnershipInspection;

public class MetricsCorrelation {
	private final MethodOwnershipInspection methodsOwnershipProblemsInspection;
	private final AstronomicalMethodsInspection astronomicalMethodsInspection;

	private final ArrayList<String> nameOfFilesWithDecapsulationProblems = new ArrayList<>();

	private Integer numberOfFilesWithSupernovaMethods;
	private Integer numberOfFilesWithPulsarMethods;

	Integer numberOfFilesWithOwnershipAndDecapsulationProblems = 0;
	Integer numberOfFilesWithSupernovaAndPulsarMethods = 0;
	Integer numberOfFilesWithSupernovaAndOwnershipProblems = 0;
	Integer numberOfFilesWithPulsarAndOwnershipProblems = 0;
	Integer numberOfFilesWithSupernovaAndDecapsulationProblems = 0;
	Integer numberOfFilesWithPulsarAndDecapsulationProblems = 0;

	public MetricsCorrelation(
			final MethodOwnershipInspection methodsOwnershipProblemsInspection,
			final AstronomicalMethodsInspection astronomicalMethodsInspection) {
		this.methodsOwnershipProblemsInspection = methodsOwnershipProblemsInspection;
		this.astronomicalMethodsInspection = astronomicalMethodsInspection;
	}

	public JsonObject getJsonObjectFromFile() {
		final String currentDirectory = System.getProperty("user.dir");

		// FIXME change the file name to correspond with the repository
		// analyzed.
		final String path = currentDirectory + "/decapsulations_result.json";

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

	public void countCorrelationsBetweenAstronomicalMethods() {
		final Map<String, FileWithAstronomicalMethods> filesWithSupernovaMethods = astronomicalMethodsInspection
				.getFilesWithSupernovaMethods();
		numberOfFilesWithSupernovaMethods = filesWithSupernovaMethods.size();

		final Map<String, FileWithAstronomicalMethods> filesWithPulsarMethods = astronomicalMethodsInspection
				.getFilesWithPulsarMethods();
		numberOfFilesWithPulsarMethods = filesWithPulsarMethods.size();

		for (final HashMap.Entry<String, FileWithAstronomicalMethods> entry : filesWithPulsarMethods
				.entrySet()) {
			if (filesWithSupernovaMethods.containsKey(entry.getKey())) {
				++numberOfFilesWithSupernovaAndPulsarMethods;
			}
		}
	}

	public void countCorrelationsBetweenDecapsulationsAndOwnership() {
		for (final HashMap.Entry<String, MethodsInFileAffectedByOwnershipProblems> entry : methodsOwnershipProblemsInspection
				.getFilesAffectedAndTheirSeverity().entrySet()) {

			if (nameOfFilesWithDecapsulationProblems.contains(entry.getKey())) {
				++numberOfFilesWithOwnershipAndDecapsulationProblems;
			}
		}
	}

	public void countCorrelationsBetweenAstronomicalAndOwnership() {
		final Map<String, FileWithAstronomicalMethods> filesWithSupernovaMethods = astronomicalMethodsInspection
				.getFilesWithSupernovaMethods();
		final Map<String, FileWithAstronomicalMethods> filesWithPulsarMethods = astronomicalMethodsInspection
				.getFilesWithPulsarMethods();

		for (final HashMap.Entry<String, MethodsInFileAffectedByOwnershipProblems> entry : methodsOwnershipProblemsInspection
				.getFilesAffectedAndTheirSeverity().entrySet()) {

			if (filesWithSupernovaMethods.containsKey(entry.getKey())) {
				++numberOfFilesWithSupernovaAndOwnershipProblems;
			}

			if (filesWithPulsarMethods.containsKey(entry.getKey())) {
				++numberOfFilesWithPulsarAndOwnershipProblems;
			}
		}
	}

	public void countCorrelationsBetweenAstronomicalAndDecapsulations() {
		for (final HashMap.Entry<String, FileWithAstronomicalMethods> entry : astronomicalMethodsInspection
				.getFilesWithSupernovaMethods().entrySet()) {

			if (nameOfFilesWithDecapsulationProblems.contains(entry.getKey())) {
				++numberOfFilesWithSupernovaAndDecapsulationProblems;
			}
		}

		for (final HashMap.Entry<String, FileWithAstronomicalMethods> entry : astronomicalMethodsInspection
				.getFilesWithPulsarMethods().entrySet()) {

			if (nameOfFilesWithDecapsulationProblems.contains(entry.getKey())) {
				++numberOfFilesWithPulsarAndDecapsulationProblems;
			}
		}
	}

	public void createAllCorrelationsResults() {
		getFilesWithDecapsulationProblemsNames();
		countCorrelationsBetweenAstronomicalMethods();
		countCorrelationsBetweenAstronomicalAndOwnership();
		countCorrelationsBetweenAstronomicalAndDecapsulations();
		countCorrelationsBetweenDecapsulationsAndOwnership();
	}

	public void printCorrelationsResults() {
		System.out.println("\nIn the repository there were:");
		System.out.println("\n\t- " + numberOfFilesWithSupernovaMethods
				+ " files with Supernova Methods");
		System.out.println("\t- " + numberOfFilesWithPulsarMethods
				+ " files with Pulsar Methods");
		System.out.println("\t- " + numberOfFilesWithSupernovaAndPulsarMethods
				+ " files with both Supernova & Pulsar methods");

		System.out.println("\n\t- "
				+ methodsOwnershipProblemsInspection
						.getFilesAffectedAndTheirSeverity().size()
				+ " files with Ownership Problems");
		System.out.println("\t- "
				+ numberOfFilesWithSupernovaAndOwnershipProblems
				+ " files with both Supernova Methods & Ownership Problems");
		System.out.println("\t- " + numberOfFilesWithPulsarAndOwnershipProblems
				+ " files with both Pulsar Methods & Ownership Problems");

		System.out
				.println("\n\t- " + nameOfFilesWithDecapsulationProblems.size()
						+ " files with Decapsulation Problems");
		System.out.println("\n\t- "
				+ numberOfFilesWithSupernovaAndDecapsulationProblems
				+ " files with both Supernova Methods & Decapsulation Problems");
		System.out.println("\t- "
				+ numberOfFilesWithPulsarAndDecapsulationProblems
				+ " files with both Pulsar Methods & Decapsulation Problems");
		System.out.println("\t- "
				+ numberOfFilesWithOwnershipAndDecapsulationProblems
				+ " files with both Decapsulations & Ownership problems");
	}
}
