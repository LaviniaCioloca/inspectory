package edu.lavinia.inspectory.beans;

public class PulsarCriteria {
	private Integer recentCyclesPoints = 0;
	private Integer averageSizeIncreasePoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	public Integer getRecentCyclesPoints() {
		return recentCyclesPoints;
	}

	public void setRecentCyclesPoints(Integer recentCyclesPoints) {
		this.recentCyclesPoints = recentCyclesPoints;
	}

	public Integer getAverageSizeIncreasePoints() {
		return averageSizeIncreasePoints;
	}

	public void setAverageSizeIncreasePoints(Integer averageSizeIncreasePoints) {
		this.averageSizeIncreasePoints = averageSizeIncreasePoints;
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
		return "PulsarCriteria [recentCyclesPoints=" + recentCyclesPoints + ", averageSizeIncreasePoints="
				+ averageSizeIncreasePoints + ", methodSizePoints=" + methodSizePoints + ", activityStatePoints="
				+ activityStatePoints + "]";
	}

}
