package edu.lavinia.inspectory.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.lavinia.inspectory.beans.Commit;

public abstract class AbstractMethodMetric {
	/**
	 * The MIN value of <b>refinement</b> threshold is considered -3 lines.
	 * Below is considered <b>refactor</b>.
	 */
	protected final static Integer MIN_REFINE_LINES = -3;

	/**
	 * The MAX value of <b>refinement</b> threshold is considered +3 lines.
	 * Above is considered <b>develop</b>. Between MIN and MAX is
	 * <b>refinement</b>.
	 */
	protected final static Integer MAX_REFINE_LINES = 3;

	/**
	 * A method is considered to have a <b>significant</b> size if it has 20
	 * lines or above.
	 */
	protected final static Integer SIGNIFICANT_METHOD_SIZE = 20;

	/**
	 * A method is considered <b>very large method </b> if it has twice the
	 * {@value SIGNIFICANT_METHOD_SIZE}, meaning more than 40 lines.
	 */
	protected final static Integer VERY_LARGE_METHOD = 2
			* SIGNIFICANT_METHOD_SIZE;

	/**
	 * A method is considered <b>extremely large method </b> if it has 3 times
	 * the {@value SIGNIFICANT_METHOD_SIZE}, meaning more than 60 lines.
	 */
	protected final static Integer EXTREMELY_LARGE_METHOD = 3
			* SIGNIFICANT_METHOD_SIZE;

	/**
	 * A time frame is made of 28 days.
	 */
	protected final static Integer TIME_FRAME = 21;

	/**
	 * A <b>short timespan<b/> is made of 1
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.TIME_FRAME
	 * TIME_FRAME}.
	 */
	protected final static Integer SHORT_TIMESPAN = 1;

	/**
	 * A <b>medium timespan<b/> is made of 3
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.TIME_FRAME
	 * TIME_FRAME}.
	 */
	protected final static Integer MEDIUM_TIMESPAN = 3;

	/**
	 * A <b>long timespan<b/> is made of 6
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.TIME_FRAME
	 * TIME_FRAME}.
	 */
	protected final static Integer LONG_TIMESPAN = 6;

	/**
	 * A <b>small size change</b> is considered to be +/- 5 lines.
	 */
	protected final static Integer SMALL_SIZE_CHANGE = 5;

	/**
	 * A <b>major sze change</b> is considered to have
	 * {@value SIGNIFICANT_METHOD_SIZE}, meaning +/- 20 lines.
	 */
	protected final static Integer MAJOR_SIZE_CHANGE = 1
			* SIGNIFICANT_METHOD_SIZE;

	/**
	 * A method is considered <b>actively changed</b> if it has more than 3
	 * changes.
	 */
	protected final static Integer ACTIVELY_CHANGED = 3;

	protected static Date now;
	protected static ArrayList<Commit> allCommits;
	protected static Map<Commit, Integer> allCommitsIntoTimeFrames;
	protected static Integer maximumTimeFrameNumber;

	/**
	 * @param start
	 *            Date
	 * @param end
	 *            Date
	 * @return A Long representing the difference in days between {@code start}
	 *         and {@code end} date.
	 */
	public static Long getTimeDifferenceInDays(final Date start,
			final Date end) {

		final Long startTime = start.getTime();
		final Long endTime = end.getTime();
		final Long diffTime = endTime - startTime;

		if (diffTime < 0) {
			return -diffTime / (1000 * 60 * 60 * 24);
		}

		return diffTime / (1000 * 60 * 60 * 24);
	}

	/**
	 * @param commits
	 * @return A HashMap for every commit in the {@code commits} list with the
	 *         number of the time-frame in which it is.
	 */
	public static HashMap<Commit, Integer> splitCommitsIntoTimeFrames(
			final ArrayList<Commit> commits) {

		final HashMap<Commit, Integer> commitsIntoTimeFrames = new HashMap<>();
		Integer currentTimeFrame = 0;
		commitsIntoTimeFrames.put(commits.get(0), currentTimeFrame);

		for (int i = 1; i < commits.size(); ++i) {
			if (getTimeDifferenceInDays(commits.get(i - 1).getDate(),
					commits.get(i).getDate()) > TIME_FRAME) {
				++currentTimeFrame;
			}

			commitsIntoTimeFrames.put(commits.get(i), currentTimeFrame);
		}

		maximumTimeFrameNumber = currentTimeFrame;
		return commitsIntoTimeFrames;
	}

	/**
	 * Categorizes commits into 3 possible types (refactor, refine and develop)
	 * and return that list.
	 *
	 * @param changesList
	 *            ArrayList with number of lines the method suffered during the
	 *            commits
	 * @return ArrayList of Strings divided by categories in:
	 *         refactor/refine/develop commits.
	 */
	public ArrayList<String> getCommitsTypes(
			final ArrayList<Integer> changesList) {

		final ArrayList<String> commitsTypes = new ArrayList<>();

		for (int i = 0; i < changesList.size(); ++i) {
			if (changesList.get(i) < MIN_REFINE_LINES) {
				commitsTypes.add("refactor");
			} else if (changesList.get(i) >= MIN_REFINE_LINES
					&& changesList.get(i) <= MAX_REFINE_LINES) {
				commitsTypes.add("refine");
			} else {
				commitsTypes.add("develop");
			}
		}

		return commitsTypes;
	}

	/**
	 * Returns the points for the method if its {@code size} is bigger than
	 * EXTREMELY_LARGE_METHOD.
	 *
	 * @param methodSize
	 *            The actual method size at the current time
	 * @return An Integer: 0 or 1 representing the points of
	 *         {@code method's size} in metrics.
	 */
	public Integer getMethodSizePoints(final Integer methodSize) {
		if (methodSize >= EXTREMELY_LARGE_METHOD) {
			return 1;
		}

		return 0;
	}

	/**
	 * If the method is active: the latest activity of the method has occurred
	 * in of the most recent MEDIUM_TIMESPAN time-frames, returns 1 point.
	 *
	 * @param lastCommit
	 *            Latest commit in list
	 * @return An Integer: 0 or 1 representing the points of method's activity
	 *         in metrics.
	 */
	public Integer getActiveMethodPoints(final Commit lastCommit) {
		for (final HashMap.Entry<Commit, Integer> currentEntry : allCommitsIntoTimeFrames
				.entrySet()) {
			if ((currentEntry.getValue() >= maximumTimeFrameNumber
					- MEDIUM_TIMESPAN)
					&& (currentEntry.getKey().equals(lastCommit))) {
				return 1;
			}
		}

		return 0;
	}

	public static void setAllCommits(final ArrayList<Commit> allCommits) {
		AbstractMethodMetric.allCommits = allCommits;
	}

	public static void setAllCommitsIntoTimeFrames() {
		AbstractMethodMetric.allCommitsIntoTimeFrames = splitCommitsIntoTimeFrames(
				allCommits);
	}

	public static void setNow(final Date now) {
		AbstractMethodMetric.now = now;
	}

}
