package edu.lavinia.inspectory.op.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;
import edu.lavinia.inspectory.op.beans.FileChangesData;

public class MethodOwnershipProblemsMetric {

	private final static Integer MANY_METHOD_OWNERS = 2;

	public Integer getMethodSizePoints(final Integer methodSize) {
		if (methodSize >= MethodThresholdsMeasure.EXTREMELY_LARGE_METHOD) {
			return 2;
		} else if (methodSize >= MethodThresholdsMeasure.VERY_LARGE_METHOD) {
			return 1;
		}

		return 0;
	}

	public boolean checkIfMethodOlderThanMediumTimespan(
			final Commit firstMethodCommit, final Commit lastRepositoryCommit) {

		if (MethodThresholdsMeasure.getTimeDifferenceInDays(
				firstMethodCommit.getDate(),
				lastRepositoryCommit
						.getDate()) >= MethodThresholdsMeasure.MEDIUM_TIMESPAN
								* MethodThresholdsMeasure.TIME_FRAME) {
			return true;
		}

		return false;
	}

	public Integer getNumberOfDisruptiveMethodOwners(
			final FileChangesData methodChangesData) {

		Integer numberOfDisruptiveMethodOwners = 0;
		final ArrayList<String> previousOwners = new ArrayList<>();

		final ArrayList<String> allOwners = methodChangesData.getAllOwners();
		final ArrayList<Commit> allCommits = methodChangesData.getCommits();
		previousOwners.add(allOwners.get(0));

		for (int i = 1; i < allOwners.size(); ++i) {
			// check only for new owners
			numberOfDisruptiveMethodOwners = checkPreviousOwnersInFutureCommits(
					numberOfDisruptiveMethodOwners, previousOwners, allOwners,
					allCommits, i);
		}

		return numberOfDisruptiveMethodOwners;
	}

	private Integer checkPreviousOwnersInFutureCommits(
			Integer numberOfDisruptiveMethodOwners,
			final ArrayList<String> previousOwners,
			final ArrayList<String> allOwners,
			final ArrayList<Commit> allCommits, final int i) {
		final String currentOwner = allOwners.get(i);
		boolean isCurrentOwnerDisruptive = true;

		if (!previousOwners.contains(currentOwner)) {
			for (int j = i + 1; j < allOwners.size(); ++j) {
				final String currentAuthor = allCommits.get(j).getAuthor();

				if (previousOwners.contains(currentAuthor)) {
					isCurrentOwnerDisruptive = false;
				}
			}

			if (isCurrentOwnerDisruptive) {
				++numberOfDisruptiveMethodOwners;
			}

			previousOwners.add(currentOwner);
		}

		return numberOfDisruptiveMethodOwners;
	}

	public Integer getDisruptiveOwnersPoints(
			final Integer numberOfDisruptiveMethodOwners) {
		if (numberOfDisruptiveMethodOwners >= MANY_METHOD_OWNERS + 1) {
			return 3;
		} else if (numberOfDisruptiveMethodOwners == MANY_METHOD_OWNERS) {
			return 2;
		} else if (numberOfDisruptiveMethodOwners == 1) {
			return 1;
		}

		return 0;
	}

	public int longestSequenceOfDistinctOwners(final String[] ownersArray) {
		int firstIndex = 0;
		int secondIndex = 1;
		int maximumSequence = 0;
		int currentLength = 1;
		final HashSet<String> ownersSet = new HashSet<>();
		ownersSet.add(ownersArray[0]);

		while (firstIndex < ownersArray.length - 1
				&& secondIndex < ownersArray.length) {
			if (!ownersSet.contains(ownersArray[secondIndex])) {
				++currentLength;
				ownersSet.add(ownersArray[secondIndex]);
				++secondIndex;
			} else {
				maximumSequence = Math.max(maximumSequence, currentLength);
				ownersSet.remove(ownersArray[++firstIndex]);
				--currentLength;
			}
		}

		return Math.max(currentLength, maximumSequence);
	}

	public Integer getSequenceDistinctOwnersPoints(
			final ArrayList<String> owners) {

		final String[] ownersArray = owners.toArray(new String[owners.size()]);

		if (longestSequenceOfDistinctOwners(
				ownersArray) >= MANY_METHOD_OWNERS) {
			return 2;
		} else if (longestSequenceOfDistinctOwners(ownersArray) == 1) {
			return 1;
		}

		return 0;
	}

	public static Integer getActiveMethodPoints(final Commit lastMethodCommit,
			final Commit lastRepositoryCommit) {

		if (MethodThresholdsMeasure.getTimeDifferenceInDays(
				lastMethodCommit.getDate(),
				lastRepositoryCommit
						.getDate()) <= MethodThresholdsMeasure.MEDIUM_TIMESPAN
								* MethodThresholdsMeasure.TIME_FRAME) {
			return 2;
		}

		return 1;
	}

	public Map<String, Object> getOwnershipProblemsCriterionValues(
			final FileChangesData methodChangesData,
			final Commit lastRepositoryCommit) {

		final Map<String, Object> ownershipProblemsCritetionValues = new LinkedHashMap<>();

		final Integer methodSize = methodChangesData.getActualSize();

		final ArrayList<Commit> methodCommits = methodChangesData.getCommits();
		final Commit firstMethodCommit = methodCommits.get(0);
		final Commit lastMethodCommit = methodCommits
				.get(methodCommits.size() - 1);

		Integer methodSizePoints = 0;
		Integer disruptiveOwnersPoints = 0;
		Integer distinctOwnersSequencePoints = 0;
		Integer methodActivityPoints = 0;

		if (methodSize >= MethodThresholdsMeasure.SIGNIFICANT_METHOD_SIZE
				&& !methodChangesData.getEntityWasDeleted()
				&& checkIfMethodOlderThanMediumTimespan(firstMethodCommit,
						lastRepositoryCommit)) {

			methodSizePoints = getMethodSizePoints(methodSize);
			ownershipProblemsCritetionValues.put("methodSize",
					methodSizePoints);

			disruptiveOwnersPoints = getDisruptiveOwnersPoints(
					getNumberOfDisruptiveMethodOwners(methodChangesData));
			ownershipProblemsCritetionValues.put(
					"numberOfDisruptiveMethodOwners", disruptiveOwnersPoints);

			distinctOwnersSequencePoints = getSequenceDistinctOwnersPoints(
					methodChangesData.getAllOwners());
			ownershipProblemsCritetionValues.put(
					"sequenceOfDistinctMethodOwners",
					distinctOwnersSequencePoints);

			methodActivityPoints = getActiveMethodPoints(lastMethodCommit,
					lastRepositoryCommit);
			ownershipProblemsCritetionValues.put("methodActivityPoints",
					methodActivityPoints);
		}

		putOwnershipProblemsSeverity(ownershipProblemsCritetionValues,
				methodSizePoints, disruptiveOwnersPoints,
				distinctOwnersSequencePoints, methodActivityPoints);

		checkIfMethodHasManyOwners(methodChangesData,
				ownershipProblemsCritetionValues);

		return ownershipProblemsCritetionValues;
	}

	private void checkIfMethodHasManyOwners(
			final FileChangesData methodChangesData,
			final Map<String, Object> ownershipProblemsCritetionValues) {

		if (methodChangesData.getDistinctOwners()
				.size() >= MANY_METHOD_OWNERS) {
			ownershipProblemsCritetionValues.put("hasOwnershipProblems", true);
		} else {
			ownershipProblemsCritetionValues.put("hasOwnershipProblems", false);
		}
	}

	private void putOwnershipProblemsSeverity(
			final Map<String, Object> ownershipProblemsCritetionValues,
			final Integer methodSizePoints,
			final Integer disruptiveOwnersPoints,
			final Integer distinctOwnersSequencePoints,
			final Integer methodActivityPoints) {

		if (ownershipProblemsCritetionValues.size() == 0) {
			ownershipProblemsCritetionValues.put("ownershipProblemsSeverity",
					0);
		} else {
			ownershipProblemsCritetionValues.put("ownershipProblemsSeverity",
					methodSizePoints + disruptiveOwnersPoints
							+ distinctOwnersSequencePoints
							+ methodActivityPoints);
		}
	}
}
