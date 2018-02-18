/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.lavinia.inspectory.inspection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	public final static Logger logger = Logger.getLogger(Commands.class.getName());
	private String[] args = null;
	private Options options = new Options();

	private PersistentProject project;

	private final static String astronomicalMethodsCsvFileName = "astronomical-methods-result.csv";
	private final static String astronomicalMethodsJsonFileName = "astronomical-methods-result.json";
	private final static String astronomicalMethodsDyanmicsCsvFileName = "astronomical-methods-dynamics-result.csv";

	private final static String ownershipProblemsCsvFileName = "ownership-problems-result.csv";

	public Commands(String[] args, PersistentProject project) {
		this.args = args;
		this.project = project;

		generateOptions();
	}

	private void generateOptions() {

		final Option helpOption = Option.builder("h").longOpt("help").required(false).hasArg(false)
				.desc("Show usage instructions for every command.").build();

		final Option cleanOption = Option.builder("c").longOpt("clean").required(false)
				.desc("Clean previously files of inspectory results.").build();

		final Option ammOption = Option.builder("amm").longOpt("astronomical").required(false)
				.desc("Atronomical Methods - Supernova & Pulsar - Metric applied on the current repository.").build();

		final Option opmOption = Option.builder("opm").longOpt("ownership").required(false)
				.desc("Ownership Problems Metric applied on the current repository.").build();

		options.addOption(helpOption);
		options.addOption(cleanOption);
		options.addOption(ammOption);
		options.addOption(opmOption);
	}

	public void parse() {
		CommandLineParser parser = new DefaultParser();

		CommandLine cmd = null;
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
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("java -jar inspectory-<version>.jar <command>", options);
		System.exit(0);
	}

	private void clean() {
		String directoryPath = System.getProperty("user.dir") + "/.inspectory";
		File file = new File(directoryPath);

		try {
			// Deleting the directory recursively using FileUtils.
			FileUtils.deleteDirectory(file);
			System.out.println("Directory " + directoryPath + " has been deleted recursively!");
		} catch (IOException e) {
			System.out.println("Problem occurs when deleting the directory : " + directoryPath);
			e.printStackTrace();
		}
	}

	private void astronomicalMethodsMetric() {
		String directoryPath = System.getProperty("user.dir") + "/.inspectory";

		File csvFile = new File(directoryPath, astronomicalMethodsCsvFileName);
		File jsonFile = new File(directoryPath, astronomicalMethodsJsonFileName);
		File csvMethodDynamicsFile = new File(directoryPath, astronomicalMethodsDyanmicsCsvFileName);

		csvFile.getParentFile().mkdirs();

		FileWriter csvWriter = null, csvMethodDynamicsWriter = null, jsonWriter = null;
		try {
			csvWriter = new FileWriter(csvFile);
			jsonWriter = new FileWriter(jsonFile);
			csvMethodDynamicsWriter = new FileWriter(csvMethodDynamicsFile);
			CSVUtils.writeLine(csvWriter, Arrays.asList("File", "Class", "Method", "Initial size", "Actual size",
					"Number of changes", "Changes List", "isSupernova", "Supernova Severity", "Supernova - Leaps Size",
					"Supernova - Recent Leaps Size", "Supernova - Subsequent Refactoring", "Supernova - Method Size",
					"Supernova - Activity State", "isPulsar", "Pulsar Severity", "Pulsar - Recent Cycles",
					"Pulsar - Average Size Increase", "Pulsar - Method Size", "Pulsar - Activity State"));
			CSVUtils.writeLine(csvMethodDynamicsWriter, Arrays.asList("File", "Supernova Methods", "Pulsar Methods",
					"Supernova Severity", "Pulsar Severity"));
			AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project,
					csvWriter, csvMethodDynamicsWriter, jsonWriter);
			astronomicalMethodsInspection.getHistoryFunctionsAnalyze();
			csvWriter.flush();
			csvWriter.close();
			csvMethodDynamicsWriter.flush();
			csvMethodDynamicsWriter.close();
			jsonWriter.flush();
			jsonWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ownershipProblemsMetric() {
		String directoryPath = System.getProperty("user.dir") + "/.inspectory";

		File csvFile = new File(directoryPath, ownershipProblemsCsvFileName);
		csvFile.getParentFile().mkdirs();

		FileWriter csvWriter = null;
		try {
			csvWriter = new FileWriter(csvFile);
			CSVUtils.writeLine(csvWriter,
					Arrays.asList("File", "File Owner", "Number of changes", "Authors - Number of changes made"));
			OwnershipProblemsInspection ownershipProblemsInspection = new OwnershipProblemsInspection(project,
					csvWriter);
			ownershipProblemsInspection.writeFileResults();
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
