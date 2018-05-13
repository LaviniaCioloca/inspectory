package edu.lavinia.inspectory.metrics;

public interface TimeThresholdsMeasure {
	/**
	 * A time frame is made of 28 days.
	 */
	public final static Integer TIME_FRAME = 21;

	/**
	 * A <b>short timespan<b/> is made of 1
	 * {@link edu.lavinia.inspectory.metrics.TimeThresholdsMeasure.TIME_FRAME
	 * TIME_FRAME}.
	 */
	public final static Integer SHORT_TIMESPAN = 1;

	/**
	 * A <b>medium timespan<b/> is made of 3
	 * {@link edu.lavinia.inspectory.metrics.TimeThresholdsMeasure.TIME_FRAME
	 * TIME_FRAME}.
	 */
	public final static Integer MEDIUM_TIMESPAN = 3;

	/**
	 * A <b>long timespan<b/> is made of 6
	 * {@link edu.lavinia.inspectory.metrics.TimeThresholdsMeasure.TIME_FRAME
	 * TIME_FRAME}.
	 */
	public final static Integer LONG_TIMESPAN = 6;

}
