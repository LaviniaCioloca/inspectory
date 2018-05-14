package edu.lavinia.inspectory.op.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;
import edu.lavinia.inspectory.op.beans.FileChangesData;

public class MethodOwnershipProblemsMetric {

	private final static Integer MANY_FILE_OWNERS = 2;

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
			final String currentOwner = allOwners.get(i);
			final boolean isCurrentOwnerDisruptive = true;

			// check only for new owners
			numberOfDisruptiveMethodOwners = checkPreviousOwnersInFutureCommits(
					numberOfDisruptiveMethodOwners, previousOwners, allOwners,
					allCommits, i, currentOwner, isCurrentOwnerDisruptive);
		}

		return numberOfDisruptiveMethodOwners;
	}

	private Integer checkPreviousOwnersInFutureCommits(
			Integer numberOfDisruptiveMethodOwners,
			final ArrayList<String> previousOwners,
			final ArrayList<String> allOwners,
			final ArrayList<Commit> allCommits, final int i,
			final String currentOwner, boolean isCurrentOwnerDisruptive) {

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
		if (numberOfDisruptiveMethodOwners >= 3) {
			return 3;
		} else if (numberOfDisruptiveMethodOwners == 2) {
			return 2;
		} else if (numberOfDisruptiveMethodOwners == 1) {
			return 1;
		}

		return 0;
	}

	public int longestSequenceOfDistinctOwners(final String[] ownersArray) {
		int firstIndex = 0, secondIndex = 1, maximumSequence = 0,
				currentLength = 1;
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

		if (longestSequenceOfDistinctOwners(ownersArray) >= 3) {
			return 2;
		} else if (longestSequenceOfDistinctOwners(ownersArray) > 0) {
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
		final Map<String, Object> ownershipProblemsCritetionValues = new HashMap<>();

		final Integer methodSize = methodChangesData.getActualSize();

		final ArrayList<Commit> methodCommits = methodChangesData.getCommits();
		final Commit firstMethodCommit = methodCommits.get(0);
		final Commit lastMethodCommit = methodCommits
				.get(methodCommits.size() - 1);

		if (methodSize >= MethodThresholdsMeasure.SIGNIFICANT_METHOD_SIZE
				&& !methodChangesData.getEntityWasDeleted()
				&& checkIfMethodOlderThanMediumTimespan(firstMethodCommit,
						lastRepositoryCommit)) {
			ownershipProblemsCritetionValues.put("fileSize",
					getMethodSizePoints(methodSize));

			ownershipProblemsCritetionValues.put(
					"numberOfDisruptiveMethodOwners",
					getDisruptiveOwnersPoints(getNumberOfDisruptiveMethodOwners(
							methodChangesData)));

			ownershipProblemsCritetionValues.put(
					"sequenceOfDistinctMethodOwners",
					getSequenceDistinctOwnersPoints(
							methodChangesData.getAllOwners()));

			ownershipProblemsCritetionValues.put("methodActivityPoints",
					getActiveMethodPoints(lastMethodCommit,
							lastRepositoryCommit));

			if (methodChangesData.getDistinctOwners()
					.size() >= MANY_FILE_OWNERS) {
				ownershipProblemsCritetionValues.put("hasOwnershipProblems",
						true);
			}
		}

		return ownershipProblemsCritetionValues;
	}
}
