/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package edu.lavinia.inspectory.inspection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.am.inspection.AstronomicalMethodsInspection;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.inspection.FileOwnershipInspection;
import edu.lavinia.inspectory.op.inspection.MethodOwnershipInspection;
import edu.lavinia.inspectory.utils.CSVUtils;

public class Commands {

	public final static Logger LOGGER = Logger
			.getLogger(Commands.class.getName());
	private final String[] args;
	private final Options options = new Options();

	private final Optional<PersistentProject> project;

	private final static String ASTRONOMICAL_METHODS_CSV_FILE_NAME = "astronomical-methods-result.csv";
	private final static String ASTRONOMICAL_METHODS_JSON_FILE_NAME = "astronomical-methods-result.json";
	private final static String ASTRONOMICAL_METHODS_DYNAMICS_CSV_FILE_NAME = "astronomical-methods-dynamics-result.csv";

	private final static String OWNERSHIP_PROBLEMS_CLASSES_CSV_FILE_NAME = "ownership-problems-classes-result.csv";
	private final static String OWNERSHIP_PROBLEMS_METHODS_CSV_FILE_NAME = "ownership-problems-methods-result.csv";
	private final static String OWNERSHIP_PROBLEMS_CLASSES_JSON_FILE_NAME = "ownership-problems-classes-result.json";
	private final static String OWNERSHIP_PROBLEMS_METHODS_JSON_FILE_NAME = "ownership-problems-methods-result.json";

	public Commands(final String[] args,
			final Optional<PersistentProject> project) {
		this.args = args;
		this.project = project;

		generateOptions();
	}

	private void generateOptions() {
		final Option helpOption = Option.builder("h").longOpt("help")
				.required(false).hasArg(false)
				.desc("Show usage instructions for every command.").build();

		final Option cleanOption = Option.builder("c").longOpt("clean")
				.required(false)
				.desc("Clean previously files of inspectory results.").build();

		final Option ammOption = Option.builder("amm").longOpt("astronomical")
				.required(false)
				.desc("Atronomical Methods - Supernova & Pulsar - Metric applied on the current repository.")
				.build();

		final Option opmOption = Option.builder("opm").longOpt("ownership")
				.required(false)
				.desc("Ownership Problems Metric applied on the current repository.")
				.build();

		final Option allOption = Option.builder("all").longOpt("all")
				.required(false)
				.desc("Astronomical Methods & Ownership Problems Metrics applied on the current repository.")
				.build();

		addOptionsToMenu(helpOption, cleanOption, ammOption, opmOption,
				allOption);
	}

	private void addOptionsToMenu(final Option helpOption,
			final Option cleanOption, final Option ammOption,
			final Option opmOption, final Option allOption) {

		options.addOption(allOption);
		options.addOption(ammOption);
		options.addOption(opmOption);
		options.addOption(helpOption);
		options.addOption(cleanOption);
	}

	public void parse() {
		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd;

		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("h")) {
				help();
			} else if (cmd.hasOption("c")) {
				clean();
			} else if (cmd.hasOption("all")) {
				System.out.println("Starting to inspect the repository.....\n");

				allMetrics();

				System.out.println("Inspection successful!\n");
				System.out.println(
						"Check results in .inspectory folder in the current repository.\n");
			} else if (cmd.hasOption("amm")) {
				System.out.println("Starting to inspect the repository.....\n");

				astronomicalMethodsMetric();

				System.out.println("Inspection successful!\n");
				System.out.println(
						"Check results in .inspectory folder in the current repository.\n");
			} else if (cmd.hasOption("opm")) {
				System.out.println("Starting to inspect the repository.....\n");

				ownershipProblemsMetric();

				System.out.println("Inspection successful!\n");
				System.out.println(
						"Check results in .inspectory folder in the current repository.\n");
			} else {
				System.out.println("Missing valid option");
				help();
			}
		} catch (final ParseException e) {
			help();
		}
	}

	private void help() {
		final HelpFormatter formatter = new HelpFormatter();

		formatter.setOptionComparator(null);
		formatter.printHelp("java -jar inspectory-<version>.jar <command>",
				options);

		System.exit(0);
	}

	private void clean() {
		final String directoryPath = System.getProperty("user.dir")
				+ "/.inspectory";
		final File file = new File(directoryPath);

		try {
			// Deleting the directory recursively using FileUtils.
			FileUtils.deleteDirectory(file);
			System.out.println("Directory " + directoryPath
					+ " has been deleted recursively!");
		} catch (final IOException e) {
			System.out.println("Problem occurs when deleting the directory : "
					+ directoryPath);
			e.printStackTrace();
		}
	}

	private void allMetrics() {
		astronomicalMethodsMetric();
		ownershipProblemsMetric();
	}

	private void astronomicalMethodsMetric() {
		final String directoryPath = System.getProperty("user.dir")
				+ "/.inspectory";

		final File csvFile = new File(directoryPath,
				ASTRONOMICAL_METHODS_CSV_FILE_NAME);
		final File jsonFile = new File(directoryPath,
				ASTRONOMICAL_METHODS_JSON_FILE_NAME);
		final File csvMethodDynamicsFile = new File(directoryPath,
				ASTRONOMICAL_METHODS_DYNAMICS_CSV_FILE_NAME);

		csvFile.getParentFile().mkdirs();

		try {
			final FileWriter csvWriter = new FileWriter(csvFile);
			final FileWriter jsonWriter = new FileWriter(jsonFile);
			final FileWriter csvMethodDynamicsWriter = new FileWriter(
					csvMethodDynamicsFile);

			writeCSVFilesHeaderLine(csvWriter, csvMethodDynamicsWriter);

			final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
					project, csvWriter, csvMethodDynamicsWriter, jsonWriter);
			astronomicalMethodsInspection.analyzeAstronomicalMethods();

			flushAndCloseWriters(csvWriter, jsonWriter,
					csvMethodDynamicsWriter);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void flushAndCloseWriters(final FileWriter csvWriter,
			final FileWriter jsonWriter,
			final FileWriter csvMethodDynamicsWriter) throws IOException {

		csvWriter.flush();
		csvWriter.close();
		csvMethodDynamicsWriter.flush();
		csvMethodDynamicsWriter.close();
		jsonWriter.flush();
		jsonWriter.close();
	}

	private void writeCSVFilesHeaderLine(final FileWriter csvWriter,
			final FileWriter csvMethodDynamicsWriter) throws IOException {

		CSVUtils.writeLine(csvWriter, Arrays.asList("File", "Class", "Method",
				"Initial size", "Actual size", "Number of changes",
				"Method was deleted", "Changes List", "isSupernova",
				"Supernova Severity", "Supernova - Leaps Size",
				"Supernova - Recent Leaps Size",
				"Supernova - Subsequent Refactoring", "Supernova - Method Size",
				"Supernova - Activity State", "isPulsar", "Pulsar Severity",
				"Pulsar - Recent Cycles", "Pulsar - Average Size Increase",
				"Pulsar - Method Size", "Pulsar - Activity State"));

		CSVUtils.writeLine(csvMethodDynamicsWriter,
				Arrays.asList("File", "Supernova Methods", "Pulsar Methods",
						"Supernova Severity", "Pulsar Severity"));
	}

	private void ownershipProblemsMetric() {
		final String home = System.getProperty("user.dir");
		final Path path = Paths.get(home, ".inspectory");

		final String methodsCSVFilePath = home + File.separator + ".inspectory"
				+ File.separator + OWNERSHIP_PROBLEMS_METHODS_CSV_FILE_NAME;
		final File methodsCsvFile = new File(methodsCSVFilePath);

		final String methodsJsonFilePath = home + File.separator + ".inspectory"
				+ File.separator + OWNERSHIP_PROBLEMS_METHODS_JSON_FILE_NAME;
		final File methodsJsonFile = new File(methodsJsonFilePath);

		final String classesCSVFilePath = home + File.separator + ".inspectory"
				+ File.separator + OWNERSHIP_PROBLEMS_CLASSES_CSV_FILE_NAME;
		final File classesCsvFile = new File(classesCSVFilePath);

		final String classesJsonFilePath = home + File.separator + ".inspectory"
				+ File.separator + OWNERSHIP_PROBLEMS_CLASSES_JSON_FILE_NAME;
		// final File classesJsonFile = new File(classesJsonFilePath);
		final File classesJsonFile = null;

		final boolean directoryExists = Files.exists(path);

		try {
			checkDirectoryExistance(path, methodsCsvFile, classesCsvFile,
					directoryExists);

			final Commit lastRepositoryCommit = writeClassesOwnershipResult(
					classesCsvFile, classesJsonFile);

			writeMethodsOwnershipResult(methodsCsvFile, methodsJsonFile,
					lastRepositoryCommit);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private Commit writeClassesOwnershipResult(final File classesCsvFile,
			final File classesJsonFile) throws IOException {

		final FileWriter classesCsvWriter = new FileWriter(classesCsvFile);
		CSVUtils.writeLine(classesCsvWriter,
				Arrays.asList("File", "Number of changes", "Number of authors",
						"File Creator", "Authors - Total changes made",
						"Number of owners", "File owners [chronological order]",
						"Authors Ownership Percentages [current ownership values]",
						"Authors - Line changes made"));

		// final FileWriter methodsJsonWriter = new FileWriter(classesJsonFile);
		final FileWriter classesJsonWriter = null;

		final FileOwnershipInspection classesOwnershipProblemsInspection = new FileOwnershipInspection(
				project, classesCsvWriter, classesJsonWriter);
		classesOwnershipProblemsInspection.createResults();
		classesOwnershipProblemsInspection.writeFileResults();

		classesCsvWriter.flush();
		classesCsvWriter.close();

		return classesOwnershipProblemsInspection.getLastRepositoryCommit();
	}

	private void writeMethodsOwnershipResult(final File methodsCsvFile,
			final File methodsJsonFile, final Commit lastRepositoryCommit)
			throws IOException {

		final FileWriter methodsCsvWriter = new FileWriter(methodsCsvFile);
		CSVUtils.writeLine(methodsCsvWriter, Arrays.asList("File", "Class",
				"Method", "Number of changes", "Number of authors",
				"Method current size", "Number of owners",
				"Method owners [chronological order]",
				"Authors Ownership Percentages [current ownership values]",
				"Author - Line changes made", "Ownership Problems Severity",
				"Method has Ownership Problems"));

		final FileWriter methodsJsonWriter = new FileWriter(methodsJsonFile);

		final MethodOwnershipInspection methodsOwnershipProblemsInspection = new MethodOwnershipInspection(
				project, methodsCsvWriter, methodsJsonWriter,
				lastRepositoryCommit);
		methodsOwnershipProblemsInspection.createResults();
		methodsOwnershipProblemsInspection.writeFileResults();
		methodsOwnershipProblemsInspection.writeJSONMethodDynamicsData();

		methodsCsvWriter.flush();
		methodsCsvWriter.close();
		methodsJsonWriter.flush();
		methodsJsonWriter.close();
	}

	private void checkDirectoryExistance(final Path path,
			final File methodsCsvFile, final File classesCsvFile,
			final boolean directoryExists) throws IOException {

		if (directoryExists) {
			classesCsvFile.delete();
			methodsCsvFile.delete();
		} else {
			Files.createDirectories(path);
		}
	}
}
