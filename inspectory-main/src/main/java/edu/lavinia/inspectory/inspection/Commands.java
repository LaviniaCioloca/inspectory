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
import edu.lavinia.inspectory.op.inspection.OwnershipProblemsInspection;
import edu.lavinia.inspectory.utils.CSVUtils;

public class Commands {

	public final static Logger LOGGER = Logger
			.getLogger(Commands.class.getName());
	private final String[] args;
	private final Options options = new Options();

	private PersistentProject project;

	private final static String ASTRONOMICAL_METHODS_CSV_FILE_NAME = "astronomical-methods-result.csv";
	private final static String ASTRONOMICAL_METHODS_JSON_FILE_NAME = "astronomical-methods-result.json";
	private final static String ASTRONOMICAL_METHODS_DYNAMICS_CSV_FILE_NAME = "astronomical-methods-dynamics-result.csv";

	private final static String OWNERSHIP_PROBLEMS_CSV_FILE_NAME = "ownership-problems-result.csv";

	public Commands(final String[] args, final PersistentProject project) {
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

		options.addOption(helpOption);
		options.addOption(cleanOption);
		options.addOption(ammOption);
		options.addOption(opmOption);
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
			} else if (cmd.hasOption("amm")) {
				astronomicalMethodsMetric();
			} else if (cmd.hasOption("opm")) {
				ownershipProblemsMetric();
			} else {
				System.out.println("Missing valid option");
				help();
			}
		} catch (ParseException e) {
			help();
		}
	}

	private void help() {
		final HelpFormatter formater = new HelpFormatter();

		formater.printHelp("java -jar inspectory-<version>.jar <command>",
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
		} catch (IOException e) {
			System.out.println("Problem occurs when deleting the directory : "
					+ directoryPath);
			e.printStackTrace();
		}
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

		final FileWriter csvWriter;
		final FileWriter csvMethodDynamicsWriter;
		final FileWriter jsonWriter;
		try {
			csvWriter = new FileWriter(csvFile);
			jsonWriter = new FileWriter(jsonFile);
			csvMethodDynamicsWriter = new FileWriter(csvMethodDynamicsFile);
			CSVUtils.writeLine(csvWriter, Arrays.asList("File", "Class",
					"Method", "Initial size", "Actual size",
					"Number of changes", "Method was deleted", "Changes List",
					"isSupernova", "Supernova Severity",
					"Supernova - Leaps Size", "Supernova - Recent Leaps Size",
					"Supernova - Subsequent Refactoring",
					"Supernova - Method Size", "Supernova - Activity State",
					"isPulsar", "Pulsar Severity", "Pulsar - Recent Cycles",
					"Pulsar - Average Size Increase", "Pulsar - Method Size",
					"Pulsar - Activity State"));
			CSVUtils.writeLine(csvMethodDynamicsWriter,
					Arrays.asList("File", "Supernova Methods", "Pulsar Methods",
							"Supernova Severity", "Pulsar Severity"));
			final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
					project, csvWriter, csvMethodDynamicsWriter, jsonWriter);
			astronomicalMethodsInspection.analyzeAstronomicalMethods();
			csvWriter.flush();
			csvWriter.close();
			csvMethodDynamicsWriter.flush();
			csvMethodDynamicsWriter.close();
			jsonWriter.flush();
			jsonWriter.close();
			// System.out.println("Deleted nodes are: ");
			// for (final String deletedNode :
			// astronomicalMethodsInspection.getDeletedNodes()) {
			// System.out.println("- deletedNode: " + deletedNode);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ownershipProblemsMetric() {
		// final String directoryPath = System.getProperty("user.dir") +
		// "/.inspectory";

		// final File csvFile = new File(directoryPath,
		// OWNERSHIP_PROBLEMS_CSV_FILE_NAME);
		// csvFile.getParentFile().mkdirs();

		final String home = System.getProperty("user.dir");
		final Path path = Paths.get(home, ".inspectory");

		final String filePath = home + File.separator + ".inspectory"
				+ File.separator + OWNERSHIP_PROBLEMS_CSV_FILE_NAME;
		final File csvFile = new File(filePath);
		boolean directoryExists = Files.exists(path);

		final FileWriter csvWriter;
		try {
			if (directoryExists) {
				csvFile.delete();
			} else {
				Files.createDirectories(path);
			}

			csvWriter = new FileWriter(csvFile);
			CSVUtils.writeLine(csvWriter,
					Arrays.asList("File", "Total Number of changes",
							"File Creator",
							"Authors - Number of changes made"));
			final OwnershipProblemsInspection ownershipProblemsInspection = new OwnershipProblemsInspection(
					project, csvWriter);
			ownershipProblemsInspection.createResults();
			ownershipProblemsInspection.writeFileResults();
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
