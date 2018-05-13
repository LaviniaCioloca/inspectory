package edu.lavinia.inspectory.op.metrics;

import java.util.ArrayList;
import java.util.HashMap;
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

	public Map<String, Object> getOwnershipProblemsCriterionValues(
			final FileChangesData methodChangesData,
			final Commit lastRepositoryCommit) {
		final Map<String, Object> ownershipProblemsCritetionValues = new HashMap<>();

		final Integer methodSize = methodChangesData.getActualSize();

		if (methodSize >= MethodThresholdsMeasure.SIGNIFICANT_METHOD_SIZE
				&& !methodChangesData.getEntityWasDeleted()
				&& checkIfMethodOlderThanMediumTimespan(
						methodChangesData.getCommits().get(0),
						lastRepositoryCommit)) {
			ownershipProblemsCritetionValues.put("fileSize",
					getMethodSizePoints(methodSize));

			ownershipProblemsCritetionValues.put(
					"numberOfDisruptiveMethodOwners",
					getDisruptiveOwnersPoints(getNumberOfDisruptiveMethodOwners(
							methodChangesData)));

			if (methodChangesData.getDistinctOwners()
					.size() >= MANY_FILE_OWNERS) {
				ownershipProblemsCritetionValues.put("hasOwnershipProblems",
						true);
			}
		}

		return ownershipProblemsCritetionValues;
	}
}
