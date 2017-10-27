package edu.lavinia.inspectory.beans;

public class SupernovaCriteria {
	private Integer leapsSizePoints = 0;
	private Integer recentLeapsSizePoints = 0;
	private Integer subsequentRefactoringPoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	public Integer getLeapsSizePoints() {
		return leapsSizePoints;
	}

	public void setLeapsSizePoints(Integer leapsSizePoints) {
		this.leapsSizePoints = leapsSizePoints;
	}

	public Integer getRecentLeapsSizePoints() {
		return recentLeapsSizePoints;
	}

	public void setRecentLeapsSizePoints(Integer recentLeapsSizePoints) {
		this.recentLeapsSizePoints = recentLeapsSizePoints;
	}

	public Integer getSubsequentRefactoringPoints() {
		return subsequentRefactoringPoints;
	}

	public void setSubsequentRefactoringPoints(Integer subsequentRefactoringPoints) {
		this.subsequentRefactoringPoints = subsequentRefactoringPoints;
	}

	public Integer getMethodSizePoints() {
		return methodSizePoints;
	}

	public void setMethodSizePoints(Integer methodSizePoints) {
		this.methodSizePoints = methodSizePoints;
	}

	public Integer getActivityStatePoints() {
		return activityStatePoints;
	}

	public void setActivityStatePoints(Integer activityStatePoints) {
		this.activityStatePoints = activityStatePoints;
	}

	@Override
	public String toString() {
		return "SupernovaCriteria [leapsSizePoints=" + leapsSizePoints + ", recentLeapsSizePoints="
				+ recentLeapsSizePoints + ", subsequentRefactoringPoints=" + subsequentRefactoringPoints
				+ ", methodSizePoints=" + methodSizePoints + ", activityStatePoints=" + activityStatePoints + "]";
	}

}
