package edu.lavinia.inspectory.metrics;

public class FileThresholdsMeasure implements TimeThresholdsMeasure {
	protected final static Integer SIGNIFICANT_FILE_SIZE = 250;

	protected final static Integer VERY_LARGE_FILE = 2 * SIGNIFICANT_FILE_SIZE;

	protected final static Integer EXTREMELY_LARGE_FILE = 3
			* SIGNIFICANT_FILE_SIZE;

	protected final static Integer MANY_FILE_OWNERS = 3;

}
